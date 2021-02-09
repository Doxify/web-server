package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Handler {


    /**
     * Parses Socket connection and returns an instance of the Request object
     * which represents the parsed connection/request.
     *
     * @param client - the client socket who sent the request
     * @return Request object which represents the parsed request
     * @throws IOException - if an error occurs while parsing the raw request
     */
    public static Request parseRequest(Socket client) throws IOException {
        // reads input stream from the client's socket
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        // these variables are used to build the request object
        String path = null;
        String method = null;
        String version = null;
        Map<String, String> headers = new HashMap<String, String>();

        // Parsing the request
        String line = null;
        String[] lineSplit;

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

            // Parsing the headers
            lineSplit = line.split(": ");
            headers.put(lineSplit[0], lineSplit[1]);
        }

        return new Request(headers, path, method, version);
    }


    /**
     * This is a wrapper for handling GET, POST, PUT, DELETE, and HEAD requests.
     *
     * @param request - the request to handle
     * @return Response object which represents the handled request.
     */
    public static Response handleRequest(Request request) throws IOException{
        Response response;

        switch(request.getMethod()) {
            case "GET": {
                response = handleGetRequest(request);
                break;
            }
            // case "POST":
            // case "PUT":
            // case "DELETE":
            // case "HEAD":
            default: {
                return null;
            }
        }

        return response;
    }


    // TODO: Implement these...
    public static Response handleGetRequest(Request request) throws IOException {
      Response response = new Response(request);
      // set Status
      response.setContentType(Server.mimeTypeConfig(response.getRequest().getPath()));
      return response;
    }



    // private static Response handlePutRequest(Request request) {
    //     Response response = new Response(request);

    //     // put request code here...

    //     return response;
    // }

    // private static Response handleDeleteRequest(Request request) {
    //     Response response = new Response(request);

    //     // delete request code here...

    //     return response;
    // }

    // private static Response handleHeadRequest(Request request) {
    //     Response response = new Response(request);

    //     // head request code here...

    //     return response;
    // }

}
