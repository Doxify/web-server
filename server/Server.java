package server;
import utils.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import server.request.Request;

public class Server {

    private static Configuration conf;
    private ServerSocket socket;
    private Socket client;

    public Socket getClient() { return this.client; }

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
//                System.out.printf("\n[DEBUG] New request from %s: \n%s", client.toString(), request);
                client.close();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not start server socket.\r\n");
            System.out.println(e.getMessage());
            System.exit(500);
        }
    }

    public static String mimeTypeConfig(String path) throws IOException {
      String extension;
      boolean validExtension;
      HashMap<String, String> mimetypes = parseMimeType(); //Parse mime.types file into Hashmap

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
      HashMap<String, String> mimetypes = new HashMap<>();

      for(Map.Entry<Object, Object> entry : props.entrySet()) {
        for (String extension: entry.getValue().toString().split(" "))
          mimetypes.put(extension, entry.getKey().toString());
      }
      return mimetypes;
    }

    public static int statusConfig(String path) {

      Path filePath = pathConfig(path);

      if (Files.exists(filePath))
        return 200;

      return 404;
    }

    public static Path pathConfig(String path) {

      String CONFIG_ROOT = conf.getHttpd().getProperty("DocumentRoot");
      CONFIG_ROOT = CONFIG_ROOT.replaceAll("^\"|\"$", "");

      if ("/".equals(path))
        path = "/index.html";

      return Paths.get(CONFIG_ROOT, path);
    }
}
