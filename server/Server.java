package server;
import server.logger.Log;
import utils.*;
import java.io.*;
import java.net.*;

import server.request.Request;

public class Server {

  private Configuration conf;
  private ServerSocket socket;
  private Socket client;
  private final Log logger = new Log();

    public Server() {
        // Load the configuration
        try {
            conf = new Configuration();
        } catch (IOException e) {
            System.out.println("Error: loading config file(s) failed.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }

        // Load the logger
        try {
            logger.open();
        } catch (SecurityException e) {
            System.out.println("Error: server does not have permission to access log file.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        } catch (IOException e) {
            System.out.println("Error: opening the logger failed.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
            }
        }

    public void start() {
        // get the port from the configuration file
        int CONFIG_PORT = Integer.parseInt(Configuration.getHttpd().getProperty("Listen", "8080"));

        try {
            // start the server socket
            socket = new ServerSocket(CONFIG_PORT);
            client = null;

            // wait for and process requests
            while( true ) {

            client = socket.accept(); // accepts connection from client
            System.out.printf("\n[DEBUG] New request from %s: \n", client.toString());

            // Parses the request, then executes the request
            Request request = Handler.parseRequest(client);
            Response response = request.execute();

            logger.log(response); // logs to file and outputs to console
            client.getOutputStream().write(response.generateResponse()); // send client response

            // closes client connection
            client.close();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not start server socket.\r\n");
            System.out.println(e.getMessage());
        }

        // Seems dirty, we should clean this up sometime.
        stop();
        System.exit(500);
    }

    public void stop() {
        logger.close();
    }

    protected static void doBasicAuth(Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.print("HTTP/1.1 401 Unauthorized\r\n");
        out.print("WWW-Authenticate: Basic\r\n");
        out.print("\r\n");
        out.flush();
    }
}
