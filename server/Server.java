package server;
import utils.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private Configuration conf;
    private ServerSocket socket;
    private Socket client;

    public Server() {
        // Load the configuration for the server from flatfile
        try {
            conf = new Configuration();
        } catch (IOException e) {
            System.out.println("Error: loading config file(s) failed.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }
    }

    public void start() {
        // get the port from the configuration file
        int CONFIG_PORT = Integer.parseInt(conf.getHttpd().getProperty("Listen", "8080"));
        
        try {
            // start the server socket
            socket = new ServerSocket(CONFIG_PORT);
            client = null;

            // wait for and process requests
            while( true ) {
                client = socket.accept(); // accepts connection from client
                parseRequest(client); // client handler
                // doBasicAuth(client);
                client.close();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not start server socket.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }
    }

    private static void parseRequest(Socket client) throws IOException {
        // reads input stream from the client's socket
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        
        // these variables are used to build the request object
        String path = null;
        String method = null;
        String version = null;
        HashMap<String, String> headers = new HashMap<String, String>();

        // Parsing the request
        String line = null;
        String[] lineSplit;

        while (!(line = reader.readLine()).isBlank()) {            
            // if path is null, we are on the first line and it must be parsed 
            // differently (doesn't contain a colon ":").
            if(path == null) {
                lineSplit = line.split(" ");
                path = lineSplit[0];
                method = lineSplit[1];
                version = lineSplit[2];
                continue;
            }

            // Parsing the headers
            lineSplit = line.split(": ");
            headers.put(lineSplit[0], lineSplit[1]);
        }

        // create the request object and print it to console for now
        // TODO: Do something with the request object, maybe put it in a queue? idk
        Request request = new Request(path, method, version, headers);
        System.out.printf("\n[DEBUG] New request from %s: \n%s", client.toString(), request);
    }

    //TODO: MimeType Config for HEADER
    public void mimeTypeConfig(String extension) throws IOException {
        //TODO: Parse mime.types file into Hashmap
        // call parse if it hasnt already
        //TODO: compare extension passed with Hashmap keys
    }


    protected static void doBasicAuth(Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.print("HTTP/1.1 401 Unauthorized\r\n");
        out.print("WWW-Authenticate: Basic\r\n");
        out.print("\r\n");
        out.flush();
    }
}
