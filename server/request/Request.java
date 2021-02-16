package server.request;

import java.util.Map;
import server.Response;
import utils.Authenticate;
import utils.Status;

public abstract class Request {

  protected Map<String,String>  headers;  // map of request headers
  protected String              path;     // path of the requested resource
  protected String              method;   // HTTP method
  protected String              version;  // HTTP request version
  protected Authenticate auth = new Authenticate(); // Handles user-privilege authentication

  public Request(Map<String,String> headers, String path, String method, String version) {
    this.headers = headers;
    this.path = path;
    this.method = method;
    this.version = version;
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

  public Map<String,String> getHeaders() {
    return this.headers;
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
   * @return true if auth is passed, false if not
   */
  public boolean auth(Response res) {
    if (res.getRequest().auth.requiresAuth()) {
      // create a response object
      if (res.getRequest().getHeaders().get("Authorization") == null) {
        res.getHeaders().put("WWW-Authenticate", "Basic"); // requests Auth Header
        res.setStatus(Status.UNAUTHORIZED); // set status code
        return false;
      }

      //User not allowed
      if (!res.getRequest().auth.isAuthorized(res)) {
        res.setStatus(Status.FORBIDDEN); // set status code
        return false;
      }
    }

    //no auth required or auth cached
    return true;
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
