package server.logger;
import server.Response;
import server.Server;
import utils.Configuration;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {


  FileOutputStream stream;
  OutputStreamWriter fileWrite;
  Server server;
  private final DateFormat date = new SimpleDateFormat("[dd/MMM/yyyy hh:mm:ss Z]");
  private String identd = "-";
  private String userID = "-";

  public void open() throws IOException, SecurityException {

    // Obtain path
    String rootPathRaw = Configuration.getConfigProperty("LogFile");
    String rootPath = rootPathRaw.replaceAll("\"", "");

    File file = new File(rootPath);
    file.createNewFile(); // creates log.txt if it doesn't exist

    /*
     FileOutputStream is an OutputStream for writing bytes to a file.
     OutputStreams do not accept chars (or Strings).
     By wrapping it in an OutputStreamWriter you now have a Writer,
     which does accept Strings.
     */
    stream = new FileOutputStream(rootPath, true);
    fileWrite = new OutputStreamWriter(stream);

  }

  public void close() {
    try {
      stream.close();
      fileWrite.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void log(Socket client, Response response) {

    //Apache Common Log: 127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
    String log = String.format("%s %s %s %s \"%s %s %s\" %s %s\r\n\n",
      (((InetSocketAddress) client.getRemoteSocketAddress()).getAddress()).toString().replace("/",""),
      this.identd,
      this.userID,
      date.format(new Date()),
      response.getRequest().getMethod(),
      response.getRequest().getPath(),
      response.getRequest().getVersion(),
      response.getStatus().code,
      response.getHeader("Content-Length")
    );

    // Console Handler
    System.out.println(log);

    // File Handler
    try {
      fileWrite.write(log);
      fileWrite.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

