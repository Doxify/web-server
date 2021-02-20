package server;
import server.logger.Log;
import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket socket;
    private Log logger;
    private int port;

    public Server(int port, Log logger) {
        this.port = port;
        this.logger = logger;
    }

    public void start() {
        try {
            // start the server socket
            System.out.printf("Starting Web Server on port %d\n", port);
            socket = new ServerSocket(port);
            
            // wait for and process requests
            while(true) {
                try {
                    Socket client = socket.accept();
                    new Handler(client, logger).start();
                } catch (IOException e) {
                    System.out.println("Error: Could not process socket connection.\r\n");
                    System.out.println(e.getMessage());
                }
                
            }
        } catch (IOException e) {
            System.out.println("Error: Could not start server socket.\r\n");
            System.out.println(e.getMessage());
            return;
        }
    }
    
}
