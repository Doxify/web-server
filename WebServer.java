import java.io.IOException;

import server.*;
import server.logger.Log;
import utils.Configuration;

public class WebServer {

  private static final Log  logger = new Log();
  
  private static Server     server;
  private static int        port;

  static {
    // Load the configuration
    try {
      new Configuration();
      port = Integer.parseInt(Configuration.getHttpd().getProperty("Listen", "8080"));
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
    port = Integer.parseInt(Configuration.getHttpd().getProperty("Listen", "8080"));
    server = new Server(port, logger);
  
    System.out.printf("Starting Web Server on port %d\n", port);
    server.start(); // this hangs until server is stopped

    Runtime.getRuntime().addShutdownHook(new Thread() { 
      public void run() 
        { 
          System.out.printf("Closing Web Server on port %d\n", port);
          logger.close();
        } 
    });
  }

}
