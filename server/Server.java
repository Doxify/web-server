package server;
import utils.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static Configuration conf;


    public Server() {
        //TODO: constructor should read config files
    }

    public void start() throws IOException {

        try {
            conf = new Configuration();
        } catch (IOException e) {
            System.out.println("Error: loading config file(s) failed.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }

        //TODO: start the server Socket
        int CONFIG_PORT = Integer.parseInt(conf.getHttpd().getProperty("Listen"));
        int DEFAULT_PORT = 8080;
        // Set default port (:8080) if http.config doesn't declare one
        if (CONFIG_PORT == 0) {
            ServerSocket socket = new ServerSocket(DEFAULT_PORT);
        }
        ServerSocket socket = new ServerSocket(CONFIG_PORT);
        Socket client = null;

        while( true ) {
            client = socket.accept(); // accepts connection from client
            clientHandler(client); // client handler
            // doBasicAuth(client);
            client.close();
        }
    }

    protected static void clientHandler(Socket client) throws IOException {
        System.out.println("\n\nDebug: new client " + client.toString());
        // Reads input stream from the client's socket
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line = null, key = null, value = null;
        Map<String, String> httpRequestMap = new HashMap<String, String>();

        // Parse Request
        line = reader.readLine(); // First line must be parsed differently (doesn't contain a colon ":")
        //append HTTP Method to hashmap (key = "method", value = "GET, HEAD, POST, PUT, or DELETE")
        key = "method";
        value = line.split(" ")[0];
        httpRequestMap.put(key, value);
        // append path
        key = "path";
        value = line.split(" ")[1];
        httpRequestMap.put(key, value);
        // append version
        key = "version";
        value = line.split(" ")[2];
        httpRequestMap.put(key, value);

        while (!(line = reader.readLine()).isBlank()) {
            key = line.split(": ")[0];
            value = line.split(": ")[1];
            // Parses request into a hashmap where key = directive name, value = everything after ":"
            httpRequestMap.put(key, value);
        }

        // Outputs all httpRequestMap keys & values
        httpRequestMap.entrySet().forEach(entry->{
            System.out.println("Debug: key=" + entry.getKey() + ", value=" + entry.getValue());
        });
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
