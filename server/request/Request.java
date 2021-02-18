package server.request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import server.Response;
import utils.Authenticate;
import utils.Configuration;
import utils.Status;

public abstract class Request {

  protected Map<String, String> headers; // map of request headers
  protected String path; // path of the requested resource
  protected String method; // HTTP method
  protected String version; // HTTP request version
  protected String body; // HTTP request body

  public Request(Map<String, String> headers, String path, String method, String version, String body) {
    this.headers = headers;
    this.path = path;
    this.method = method;
    this.version = version;
    this.body = body;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public String getPath() {
    return this.path;
  }

  public String getMethod() {
    return this.method;
  }

  public String getVersion() {
    return this.version;
  }

  public String getBody() {
    return this.body;
  }

  /**
   * This method executes this request and returns a Response object that
   * represents the outcome of this Request's execution.
   *
   * @return Response object with all properties set.
   */
  public abstract Response execute();

  /**
   * Handles Authentication
   *
   * @return Status
   */
  Status auth() {
    // create a response object
    if (this.headers.get("Authorization") == null)
      return Status.UNAUTHORIZED;

    //User not allowed
    if (!Authenticate.isAuthorized(this))
      return Status.FORBIDDEN;

    return Status.OK;
  }

  /**
   * This function takes a path relative to the server and a Path object
   * representing the file or directory.
   *
   *
   *
   * NOTE: It uses httpd.conf:DocumentRoot as the root directory. If the path is
   * "/" it returns index.html from the root directory.
   *
   * @return - requested resource in the form of a Path object
   */
  protected Path getResource() {
    String rootPathRaw = Configuration.getHttpd().getProperty("DocumentRoot");
    String rootPath = rootPathRaw.replaceAll("\"", "");
    String fullPath;

    // resource defaults to index.html if "/" is the path
    if ("/".equals(this.path)) {
        fullPath = rootPath + "index.html";
    } else {
        fullPath = rootPath + this.path.substring(1);
    }

    System.out.printf("[DEBUG] Looking for %s\n", fullPath);
    return Paths.get(fullPath);
  }

  /**
   * Helper function that gets the extension of the file at the Request's path If
   * the path is "/index.html", this function returns "html".
   *
   * @return - the extension of the resource associated with the Request
   */
  protected String getResourceFileExtension() {
    if ("/".equals(this.path)) {
        return "html";
    } else {
        int i = this.path.lastIndexOf('.');
        if (i > 0) {
            return this.path.substring(i + 1);
        } else {
            return "";
        }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("Path: " + this.path + "\n");
    sb.append("Method: " + this.method + "\n");
    sb.append("Version: " + this.version + "\n");

    this.headers.entrySet().forEach(entry -> {
      sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
    });

    sb.append("\nBody: \n" + this.body + "\n");


    return sb.toString();
  }

}
