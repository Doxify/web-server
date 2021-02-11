package server.logger;
import server.Response;
import server.request.Request;
import utils.Configuration;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;


public class Log {

  // TODO: We should create a custom logger that uses a custom format
  // Something like [date time] [log type] <message>

  // We can also use this class to automatically log something to our
  // log file. Whenever we call a log method, this class could automatically
  // log to the file!
  private Logger LOGGER = Logger.getLogger(Log.class.getName()); // Creates Logger
  private FileHandler fileHandler;
  private ConsoleHandler consoleHandler;

  private final DateFormat date = new SimpleDateFormat("[dd/MMM/yyyy hh:mm:ss Z]");
  private String identd = "-";
  private String userID = "-";

  public void open() throws IOException {

    LogManager.getLogManager().reset(); // resets logging configuration
    LOGGER.setUseParentHandlers(false);
    LOGGER.setLevel(Level.ALL); // Set log levels
    System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s %5$s%6$s%n");

    // Obtain path
    String rootPathRaw = Configuration.getHttpd().getProperty("LogFile");
    String rootPath = rootPathRaw.replaceAll("\"", "");

    // File handler config
    fileHandler = new FileHandler(rootPath, true);
    fileHandler.setLevel(Level.FINE);
    fileHandler.setFormatter(new SimpleFormatter());

    // File handler config
    consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.FINE);
    consoleHandler.setFormatter(new SimpleFormatter());

    // add handlers to LOGGER
    LOGGER.addHandler(fileHandler);
    LOGGER.addHandler(consoleHandler);


  }

  public void close() throws IOException {
    fileHandler.close();
    consoleHandler.close();
  }

  public void log(Response response) throws UnknownHostException {

    //Apache Common Log: 127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
    LOGGER.log(Level.INFO,
      InetAddress.getLocalHost().getHostAddress() + " " //TODO ? Not sure how to retrieve client IP without using "Servlet"
        + this.identd + " "
        + this.userID + " "
        + date.format(new Date()) + " \""
        + response.getRequest().getMethod() + " "
        + response.getRequest().getPath() + " "
        + response.getRequest().getVersion() + "\" "
        + response.getStatus() + " "
        + response.getHeaders().get("Content-Length")+ " \r\n"
    );
  }
}

