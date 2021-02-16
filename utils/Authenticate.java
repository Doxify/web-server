package utils;

import server.Response;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class Authenticate {

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
   * @return true if .htpasswd username:password equals inputted username:password, false if not
   */
  public boolean isAuthorized(Response res) {

    // retrieves .htpasswd string
    String validCredentials = validCredentials();
    // obtain Base64 encoded string ONLY from Auth Header.
    String authInfo = res.getRequest().getHeaders().get("Authorization").replace("Basic ", "");

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
}
