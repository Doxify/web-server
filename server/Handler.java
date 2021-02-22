package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import server.logger.Log;
import server.request.*;
import server.request.Request;
import server.response.CGI;
import server.response.Response;
import utils.Authenticate;
import utils.Configuration;
import utils.Status;

public class Handler extends Thread {

  private Socket client;
  private Log logger;

  public Handler(Socket client, Log logger) {
    this.client = client;
    this.logger = logger;
  }

  public void run() {
    try {
      // parse the request and get a generic response
      Request request = parseRequest();
      Response response = request.getResponse();

      // execute request if it has access
      if(requestIsAuthorized(request)) {
        if(requestIsScriptAliased(request)) {
          CGI cgi = new CGI(request);
          response = cgi.executeScript();
        } else {
          response = request.execute();
        }
      }

      // log the request/response
      logger.log(client, response);

      // respond to client and close the connection.
      client.getOutputStream().write(response.generateResponse());
      client.getOutputStream().flush();
      client.close();

    } catch (Exception e) {
      System.out.println("Error occurred while processing socket.");
      System.out.println(e.getMessage());
    }
  }

  /**
   * Parses Socket connection and returns an instance of the Request object which
   * represents the parsed connection/request.
   *
   * @return Request object which represents the parsed request or null if
   * invalid http is detected
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
      if (path == null) {
        lineSplit = line.split(" ");
        method = lineSplit[0];
        path = parseRequestPath(lineSplit[1]);
        version = lineSplit[2];
        continue;
      }

      // parse headers
      if ((lineSplit = line.split(": ")).length == 2) {
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
    if (contentLength > 0) {
      char[] dataCArray = new char[contentLength];
      reader.read(dataCArray, 0, contentLength);
      body = new String(dataCArray);
    }

    return generateRequest(headers, path, method, version, body);
  }

  /**
   * Helper function for determining which type of Request object to instantiate.
   */
  private static final Request generateRequest(Map<String, String> headers, String path, String method, String version,
      String body) {
    switch (method) {
      case "HEAD":
        return new Head(headers, path, method, version, body);
      case "POST":
        return new Post(headers, path, method, version, body);
      case "PUT":
        return new Put(headers, path, method, version, body);
      case "DELETE":
        return new Delete(headers, path, method, version, body);
      case "GET":
      default:
        return new Get(headers, path, method, version, body);
    }
  }

  /**
   * Parses a Request's path so that it is relative to the server's file system.
   * Handles aliases, script aliases, and resolve path.
   * 
   * @param path - raw HTTP request path
   */
  private static final String parseRequestPath(String path) {
    String parsedPath = path;
    boolean isAliased = false;

    for (Entry<String, String> alias : Configuration.getAliases().entrySet()) {
      // if uri is aliased, modify the uri with the config value
      if (path.contains(alias.getKey())) {
        String absolutePath = alias.getValue().replaceAll("\"", "");
        parsedPath = path.replace(alias.getKey(), absolutePath);
        isAliased = true;
        break;
      }
    }

    // if the uri is not aliased, resolve the path (doc_root + uri)
    if (!isAliased) {
      String rootPath = Configuration.getConfigProperty("DocumentRoot").replaceAll("\"", "");
      parsedPath = rootPath + path.substring(1);
    }

    // append DirectoryIndex if the path does not resolve to a file
    if (Files.isDirectory(Paths.get(parsedPath))) {
      String dirIndex = Configuration.getConfigProperty("DirectoryIndex");
      if(dirIndex == null) {
        dirIndex = "index.html";
      } else {
        dirIndex = dirIndex.replaceAll("\"", "");
      }

      if (parsedPath.charAt(parsedPath.length() - 1) != '/') {
        parsedPath += "/" + dirIndex;
      } else {
        parsedPath += dirIndex;
      }
      
    }

    return parsedPath;
  }

  /**
   * Determines if a request is authorized to be executed.
   * 
   * @param request - to check access for
   * @return true if authorized, false if not
   */
  private boolean requestIsAuthorized(Request request) {
    if (Authenticate.requiresAuth(request.getPath())) {
      if (request.hasAuthHeader()) {
        if (Authenticate.isAuthorized(request)) {
          return true;
        } else {
          request.getResponse().setStatus(Status.FORBIDDEN);
          return false;
        }
      } else {
        request.getResponse().setHeader("WWW-Authenticate", "Basic");
        request.getResponse().setStatus(Status.UNAUTHORIZED);
        return false;
      }
    }
    return true;
  }

  public static final boolean requestIsScriptAliased(Request request) {
    // [0] = uri alias, [1] = absolute file system path
    String[] scriptAlias = Configuration.getConfigProperty("ScriptAlias").split(" ");
    return request.getPath().contains(scriptAlias[0]);
  }

}
