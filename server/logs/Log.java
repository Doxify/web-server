package server.logs;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

public class Log {

  // TODO: We should create a custom logger that uses a custom format
  // Something like [date time] [log type] <message>

  // We can also use this class to automatically log something to our
  // log file. Whenever we call a log method, this class could automatically
  // log to the file!

  private static final Logger LOGGER = Logger.getLogger(Log.class.getName()); // Creates Logger
  private static final DateFormat date = new SimpleDateFormat("[dd/MMM/yyyy hh:mm:ss Z]");

  // Logs messages to file
  public static void fileLog(String ip, String userId, String request) throws IOException {
    LogManager.getLogManager().reset(); // resets logging configuration
    LOGGER.setLevel(Level.ALL); // Set log levels

    // Configures the logger with handler and formatter
    FileHandler fileHandler = new FileHandler("server/logs/log.txt", true);
    fileHandler.setLevel(Level.FINE);
    fileHandler.setFormatter(new SimpleFormatter());
    LOGGER.addHandler(fileHandler);

    //test date format
//    LOGGER.log(Level.INFO, ip + " - " + userId + " " + date.format(new Date()) + " " + request + " " + status + " " + size );
  }

  // Logs messages to console
  public void consoleLog() throws IOException {
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.SEVERE);
    LOGGER.addHandler(consoleHandler);
  }

  public static String format(LogRecord record) {
    StringBuilder builder = new StringBuilder(1000);
    return builder.toString();
  }

}
