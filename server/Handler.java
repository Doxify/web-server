package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.request.*;
import server.request.Request;

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

        return generateRequest(headers, path, method, version);
    }

    public static Request generateRequest(Map<String, String> headers, String path, String method, String version) {
        switch(method) {
            case "GET":
                return new Get(headers, path, method, version);
            case "HEAD":
                return new Head(headers, path, method, version);
            case "POST":
                return new Post(headers, path, method, version);
            case "PUT":
                return new Put(headers, path, method, version);
            case "DELETE":
                return new Delete(headers, path, method, version);
            default: 
                return null;
        }
    }

    /**
     * This is a wrapper for handling GET, POST, PUT, DELETE, and HEAD requests.
     * 
     * @param request - the request to handle
     * @return Response object which represents the handled request.
     */
    public static Response handleRequest(Request request) {
        Response response = request.execute();
        return response;
    }

}