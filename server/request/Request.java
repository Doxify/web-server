package server.request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import server.response.Response;

public abstract class Request {

  protected Map<String, String> headers;
  protected String path;
  protected String method;
  protected String version;
  protected String body;
  protected Response res;

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

  public boolean hasAuthHeader() {
    return this.headers.get("Authorization") != null;
  }

  public Path getResource() {
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

  /**
   * USED FOR DEBUG PURPOSES
   */
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
