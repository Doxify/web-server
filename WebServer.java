import java.io.IOException;

import server.*;
import server.logger.Log;
import utils.Configuration;

public class WebServer {

  // These may be swapped out for args to make the program more dynamic.
  private static final String HTTPD_CONFIG_PATH = "./conf/httpd.conf";
  private static final String MIME_TYPE_CONFIG_PATH = "./conf/mime.types";
  private static final String DEFAULT_PORT = "8080";

  private static final Configuration config = new Configuration(HTTPD_CONFIG_PATH, MIME_TYPE_CONFIG_PATH);
  private static final Log logger = new Log();

  static {
    // Load the configuration
    try {
      config.init();
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

  public static void main(String[] args) {
    // This file will be compiled by script and must be at
    // the root of your project directory
    String configPort = Configuration.getConfigProperty("Listen");
    int port = Integer.parseInt(configPort == null ? DEFAULT_PORT : configPort);

    Server server = new Server(port, logger);
    server.start(); // this hangs until server is stopped

    // this executes when the program is terminated/shutdown
    Runtime.getRuntime().addShutdownHook(new Thread() { 
      public void run() 
        { 
          System.out.printf("Closing Web Server on port %d\n", port);
          logger.close();
        } 
    });
  }

}
