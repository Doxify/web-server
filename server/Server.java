package server;
import utils.*;
import java.io.*;
import java.net.*;

import server.request.Request;

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
                
                Request request = Handler.parseRequest(client);
                System.out.printf("\n[DEBUG] New request from %s: \n%s", client.toString(), request);

                // doBasicAuth(client);
                
                client.close();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not start server socket.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }
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
