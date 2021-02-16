package server.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import server.Response;
import server.Server;
import utils.Configuration;
import utils.Status;

public abstract class Request {

  protected Map<String,String>  headers;  // map of request headers
  protected String              path;     // path of the requested resource
  protected String              method;   // HTTP method
  protected String              version;  // HTTP request version

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
    if (res.getRequest().requiresAuth()) {
      // create a response object
      if (res.getRequest().getHeaders().get("Authorization") == null) {
        res.getHeaders().put("WWW-Authenticate", "Basic"); // requests Auth Header
        res.setStatus(Status.UNAUTHORIZED); // set status code
        return false;
      }

      //User not allowed
      if (!res.getRequest().isAuthorized()) {
        res.setStatus(Status.FORBIDDEN); // set status code
        return false;
      }
    }

    //no auth required or auth cached
    return true;
  }


  /**
   * Determines if this request requires authentication headers or not
   *
   * @return true if auth is required, false if not
   */
  public boolean requiresAuth() {
    // checks for htaccess in directory
    String rootPathRaw = Configuration.getHttpd().getProperty("DocumentRoot");
    String rootPath = rootPathRaw.replaceAll("\"", "");
    File htaccess = new File(rootPath + ".htaccess");

    return (htaccess.exists());
  }

  /**
   * Compares the user inputted username:password with the AuthUserFile credentials
   *
   * validCredentials(): retrieves AuthUserFile username:{SHA}password
   * encryptClearPassword(): encodes user inputted password using the SHA-1 encryption algorithm
   *
   * @return true if .htpassword username:password equals inputted username:password, false if not
   */
  public boolean isAuthorized() {

    // retrieves .htpasswd string
    String validCredentials = validCredentials();
    // obtain Base64 encoded string ONLY from Auth Header.
    String authInfo = this.headers.get("Authorization").replace("Basic ", "");

    // decode Base64 encoded string
    String credentials = new String(
      Base64.getDecoder().decode( authInfo ),
      StandardCharsets.UTF_8
    );

    String[] tokens = credentials.split( ":" ); // user entered username:password
    String[] validTokens = validCredentials.split( ":" ); // .htpasswd username:password

    // obtain .htpasswd Base64 encoded password
    String validPassword = validTokens[1].replace("{SHA}", "").trim();
    // encodes user entered password
    String encodedPassword = encryptClearPassword(tokens[1]);

    // compares .htpasswd username:password with user entered username:password
    return (validTokens[0].equals(tokens[0]) && validPassword.equals(encodedPassword));
  }

  /**
   * Obtains .htpasswd containing valid credentials username:{SHA}password
   *
   * @return username:{SHA}password
   */
  private String validCredentials() {
    try {
      // checks for htaccess in directory
      String rootPathRaw = Configuration.getHttpd().getProperty("DocumentRoot");
      String rootPath = rootPathRaw.replaceAll("\"", "");
      File htaccess = new File(rootPath + ".htaccess");

      // obtains password path
      Scanner accessScan = new Scanner(htaccess);
      String pwdPath = null;
      String validUser = null;

      while (accessScan.hasNextLine()) {
        String data = accessScan.nextLine();

        if (data.contains("AuthUserFile"))
          pwdPath = data.replaceAll("AuthUserFile |\"", "");

        // obtains valid username:password
        File htpasswd = new File(pwdPath);
        Scanner passwdScan = new Scanner(htpasswd);
        validUser = passwdScan.nextLine();
        passwdScan.close();
      }
      accessScan.close();
      return validUser;

    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Encrypts the cleartext password (that was decoded from the Base64 String
   * provided by the client) using the SHA-1 encryption algorithm
   *
   * @return SHA-1 encrypted user entered password
   */
  private String encryptClearPassword( String password ) {
    try {
      MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
      byte[] result = mDigest.digest( password.getBytes() );

      return Base64.getEncoder().encodeToString( result );
    } catch( Exception e ) {
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

    return sb.toString();
  }

}
