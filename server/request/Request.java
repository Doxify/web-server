package server.request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import server.Response;

public abstract class Request {

  protected Map<String, String> headers; // map of request headers
  protected String path; // path of the requested resource
  protected String method; // HTTP method
  protected String version; // HTTP request version
  protected String body; // HTTP request body
  protected Response res; // response for this request

  public Request(Map<String, String> headers, String path, String method, String version, String body) {
    this.headers = headers;
    this.path = path;
    this.method = method;
    this.version = version;
    this.body = body;
    this.res = new Response(this);
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
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

  public Response getResponse() {
    return this.res;
  }

  /**
   * Returns whether or not this request has the authorization header.
   *
   * @return true if it does, false if it does not.
   */
  public boolean hasAuthHeader() {
    return this.headers.get("Authorization") != null;
  }

  /**
   * Returns a Path object representing this request's resource.
   */
  public Path getResource() {
    System.out.printf("[DEBUG] Looking for %s\n", this.path);
    return Paths.get(this.path);
  }

  /**
   * This method executes this request and returns a Response object that
   * represents the outcome of this Request's execution.
   *
   * @return Response object with all properties set.
   */
  public abstract Response execute();

  /**
   * Helper function that gets the extension of the file at the Request's path .
   * If the path is "./index.html", this function returns "html".
   *
   * @return - the extension of the resource associated with the Request
   */
  protected String getResourceFileExtension() {
    int i = this.path.lastIndexOf('.');
    if (i > 0) {
      return this.path.substring(i + 1);
    } else {
      return "";
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
