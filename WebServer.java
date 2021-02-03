import java.io.IOException;

import utils.Configuration;

public class WebServer {

  private static Configuration conf;

  public WebServer() {
    try {
      conf = new Configuration();
    } catch (IOException e) {
      System.out.println("Error occurred while loading config file(s).");
      System.out.println(e.getMessage());
      System.exit(1);
    }

    // String root = conf.getHttpd().getProperty("DocumentRoot");
    // int port = Integer.parseInt(conf.getHttpd().getProperty("Listen"));
    // String mime = conf.getMime().getProperty("application/octet-stream");
    // String alias = conf.getHttpd().getProperty("ScriptAlias");
    
    // System.out.printf("root: %s, port: %d", root, port);
    // System.out.printf("mime: %s", mime);
    // System.out.printf("alias: %s", alias);
  }

  public static void main(String[] args) {
    // This file will be compiled by script and must be at 
    // the root of your project directory

    WebServer webServer = new WebServer();
    // TODO: Do some web server initialization stuff here...
  }
}
