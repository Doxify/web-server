package utils;

import server.request.Request;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class Authenticate {

  /**
   * Determines if this request requires authentication headers or not
   *
   * @param path - request path
   * @return .htaccess file path
   */
  public static StringBuilder requiresAuth(String path) {
    // checks for htaccess in directory
    String rootPathRaw = Configuration.getConfigProperty("DocumentRoot");
    StringBuilder rootPath = new StringBuilder(rootPathRaw.replaceAll("\"", ""));
    String[] absolutePath = path.split(rootPath.toString());
    String[] dir = absolutePath[1].split("/");
    File htaccess;

    for (int i=0; i < dir.length; i++) {
      htaccess = new File(rootPath + ".htaccess");
      if (htaccess.exists()) {
        return rootPath;
      }
      rootPath.append(dir[i]).append("/");
    }
    return null;
  }

  /**
   * Compares the user inputted username:password with the AuthUserFile credentials
   *
   * @param req - Request
   * @param authPath - .htaccess file path
   * @return true if .htpasswd username:password equals inputted username:password, false if not
   */
  public static boolean isAuthorized(Request req, StringBuilder authPath) {

    // retrieves .htpasswd string
    String validCredentials = validCredentials(authPath);
    // obtain Base64 encoded string ONLY from Auth Header.
    String authInfo = req.getHeaders().get("Authorization").replace("Basic ", "");

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
   * @param authPath - .htaccess file path
   * @return username:{SHA}password
   */
  private static String validCredentials(StringBuilder authPath) {
    try {
      // checks for htaccess in directory
      File htaccess = new File(authPath + ".htaccess");

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
   * @param password = clear text password
   * @return SHA-1 encrypted user entered password
   */
  private static String encryptClearPassword( String password ) {
    try {
      MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
      byte[] result = mDigest.digest( password.getBytes() );

      return Base64.getEncoder().encodeToString( result );
    } catch( Exception e ) {
      return "";
    }
  }
}
