package server;
import utils.Configuration;
import java.io.*;
import java.net.*;

public class Server {

    private static Configuration conf;


    public Server() {
        //TODO: constructor should read config files
    }

    public void start() throws IOException {
        //TODO: start the server Socket
//        int port = Integer.parseInt(conf.getHttpd().getProperty("Listen"));
//        System.out.println("The port:" + port);
        int DEFAULT_PORT = 8080;
        ServerSocket socket = new ServerSocket( DEFAULT_PORT );
        Socket client = null;

        while( true ) {
            client = socket.accept();
            printHttpRequest(client);
            doBasicAuth(client);
            client.close();

        }
    }


    //TODO: MimeType Config for HEADER
    public void mimeTypeConfig(String extension) throws IOException {
        //TODO: Parse mime.types file into Hashmap

        //TODO: compare extension passed with Hashmap keys
    }

    protected static void doBasicAuth(Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.print("HTTP/1.1 401 Unauthorized\r\n");
        out.print("WWW-Authenticate: Basic\r\n");
        out.print("\r\n");
        out.flush();
    }

    protected static void printHttpRequest(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line = null;

        while ((line = reader.readLine()) != null && line.trim().length() != 0)
            System.out.println(line);
    }
}
