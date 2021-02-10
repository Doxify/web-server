package server.request;

import java.util.Map;

import server.Response;

public abstract class Request {

  protected Map<String,String>  headers;
  protected String              path;
  protected String              method; // TODO: Turn this into an enum if feeling fancy.
  protected String              version;

  public Request(Map<String,String> headers, String path, String method, String version) {
    this.headers = headers;
    this.path = path;
    this.method = method;
    this.version = version;
  }

  public abstract Response execute();

  public String getPath() {
    return this.path;
  }

  public String getMethod() {
    return this.method;
  }

  public String getVersion() {
    return this.version;
  }

  public Map<String,String> getHeaders() {
    return this.headers;
  }

  /**
   * Returns whether or not the given request requires authentication or not.
   */
  public boolean requiresAuth() {
    // check for htaccess in directory
    // return whether or not it is present
    return false;
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

    return sb.toString();
  }

}
