package server;
import utils.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {

    private static Configuration conf;
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
                 Response response = Handler.handleRequest(request);
                System.out.printf("\n[DEBUG] New request from %s: \n%s", client.toString(), request);
                client.close();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not start server socket.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }
    }

    //TODO: MimeType Config for HEADER
    public static String mimeTypeConfig(String path) throws IOException {
      String extension;
      Boolean validExtension;

      //Parse mime.types file into Hashmap
      HashMap<String, String> mimetypes = parseMimeType();

      //compare extension passed with Hashmap keys
      if (path.contains("."))
        extension = path.substring(path.lastIndexOf(".") + 1);
      else
        extension = path.substring(path.lastIndexOf("/") + 1);

      validExtension = mimetypes.containsKey(extension);
      if (validExtension)
        return mimetypes.get(extension);
      return "text";
    }

    private static HashMap<String, String> parseMimeType() {
      Properties props = conf.getMime();
      HashMap<String, String> mimetypes = new HashMap<String, String>();

      for(Map.Entry<Object, Object> x : props.entrySet()) {
        for (String val: x.getValue().toString().split(" "))
          mimetypes.put(val, x.getKey().toString());
      }
      return mimetypes;
    }

    protected static void doBasicAuth(Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.print("HTTP/1.1 401 Unauthorized\r\n");
        out.print("WWW-Authenticate: Basic\r\n");
        out.print("\r\n");
        out.flush();
    }
}
