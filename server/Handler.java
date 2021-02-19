package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.logger.Log;
import server.request.*;
import server.request.Request;
import utils.Authenticate;
import utils.Status;

public class Handler extends Thread {

  private Socket client;
  private Log logger;

  public Handler(Socket client, Log logger) {
    this.client = client;
    this.logger = logger;
  }

  public void run() {
    System.out.printf("\n[DEBUG] Handling request for %s: \n", client.toString());

    try {
      // parse the request
      Request req = parseRequest();
      Response res = req.getResponse();
      boolean authorized = true;

      // handle authentication
      if(Authenticate.requiresAuth(req.getPath())) {
        if(req.hasAuthHeader()) {
          if(!Authenticate.isAuthorized(req)) {
            res.setStatus(Status.FORBIDDEN);
            authorized = false;
          }
        } else {
          res.setHeader("WWW-Authenticate", "Basic"); // requests Auth Header
          res.setStatus(Status.UNAUTHORIZED); //set status code
          authorized = false;
        }
      }

      // execute request if client is authorized
      if(authorized) {
        res = req.execute();
      }

      // log the request
      logger.log(client, res);
      
      // respond to client and close the connection.
      PrintStream output = new PrintStream(client.getOutputStream());
      output.write(res.generateResponse());
      output.flush();
      client.close();
    } catch (IOException e) {
      System.out.println("Error occurred while processing socket.");
      System.out.println(e.getMessage());
    }
}

  /**
   * Parses Socket connection and returns an instance of the Request object
   * which represents the parsed connection/request.
   * 
   * Returns null if the request is not valid HTTP.
   *
   * @return Request object which represents the parsed request or null
   */
  private Request parseRequest() throws IOException {
    // reads input stream from the client's socket
    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

    // these variables are used to build the request object
    String path = null;
    String method = null;
    String version = null;
    String body = "";
    Map<String, String> headers = new HashMap<String, String>();

    // Parsing the request
    String line = null;
    String[] lineSplit;
    int contentLength = 0;

    while (!(line = reader.readLine()).isBlank()) {
      // if path is null, we are on the first line and it must be parsed
      // differently (doesn't contain a colon ":").
      if(path == null) {
        lineSplit = line.split(" ");
        method = lineSplit[0];
        path = lineSplit[1];
        version = lineSplit[2];
        continue;
      }

      // parse headers
      if((lineSplit = line.split(": ")).length == 2) {        
        headers.put(lineSplit[0], lineSplit[1]);
      }

      // grab the content length from the header
      try {
        contentLength = Integer.parseInt(headers.get("Content-Length"));
      } catch (NumberFormatException e) {
        contentLength = 0;
      }

    }

    // parse the data
    if(contentLength > 0) {
      char[] dataCArray = new char[contentLength];
      reader.read(dataCArray, 0, contentLength);
      body = new String(dataCArray);
    }

    return generateRequest(headers, path, method, version, body);
  }

  /**
   * Helper function for determining which type of Request object to instantiate.
   */
  private Request generateRequest(Map<String, String> headers, String path, String method, String version, String body) {
        switch(method) {
      case "GET": default:
        return new Get(headers, path, method, version, body);
      case "HEAD":
        return new Head(headers, path, method, version, body);
      case "POST":
        return new Post(headers, path, method, version, body);
      case "PUT": case "OPTIONS":
        return new Put(headers, path, method, version, body);
      case "DELETE":
        return new Delete(headers, path, method, version, body);
    }
  }

}
