package server.request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Map;
import server.Response;
import utils.Configuration;
import utils.Constants;
import utils.Status;

public class Get extends Request {

  public Get(Map<String, String> headers, String path, String method, String version, String body) {
    super(headers, path, method, version, body);
  }

  @Override
  public Response execute() {
    try {
      System.out.println("[DEBUG] Getting resource for request");

      // get the resource
      byte[] content = Files.readAllBytes(this.getResource());
      // set Dates needed
      Date today = new Date();
      Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

      // if (true) -> leave content blank and status 304
      // else -> set content, headers and status 200
      if (isCacheActive(content)) {
        res.setStatus(Status.NOT_MODIFIED); // set status code 304
        return res;
      } else {
        // set content and all content related headers
        String ext = this.getResourceFileExtension();
        res.setContent(content);
        res.setHeader("Content-Type", Configuration.getMimeType(ext));
        res.setHeader("Content-Length", String.valueOf(content.length));

        // Headers for caching - Etag is unique and contains content length to
        // compare file changes.
        res.setHeader("Expire", Constants.dateFormat.format(tomorrow));
        res.setHeader("Etag", today.getTime() + "==" + content.length);

        updateLastModified(); // update last modified date
        res.setHeader("Last-Modified", Constants.dateFormat.format(getLastModified()));

        res.setStatus(Status.OK); // set status code 200
      }
    } catch (IOException | NullPointerException e) {
      // set status code
      res.setStatus(Status.NOT_FOUND);
      System.out.printf("[DEBUG] Resource %s was not found.\n", e.getMessage());
    }

    return res;
  }

  /**
   * Indicates whether cache is stale or if contents of the file has changed.
   *
   * @return boolean
   */
  private boolean isCacheActive(byte[] content) {
    try {
      if (this.headers.get("If-Modified-Since") != null && this.headers.get("If-None-Match") != null) {
        String[] tokens = this.headers.get("If-None-Match").split("==");
        boolean currentCache = Constants.dateFormat.parse(this.headers.get("If-Modified-Since"))
            .equals(getLastModified());
        boolean currentEtag = Integer.parseInt(tokens[1]) == content.length;
        // true if cache isn't stale or if content length hasn't changed (retrieved from
        // Etag), false otherwise
        return currentCache && currentEtag;
      }
    } catch (Exception e) {
      System.out.printf("[DEBUG] Resource %s was not found.\n", e.getMessage());
    }
    // no current cache found
    return false;
  }

  /**
   * Retrieves last-modified date of the requested file
   *
   * @return Date
   */
  private Date getLastModified() {
    File file = new File(this.path); // creates file object
    long lastModified = file.lastModified(); // retrieves last-modified time
    return new Date(lastModified);
  }

  /**
   * updates last-modified of the file
   */
  private void updateLastModified() {
    FileTime now = FileTime.fromMillis(System.currentTimeMillis());
    try {
      Files.setLastModifiedTime(this.getResource(), now);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.printf("[DEBUG] Could not update last modified time for %s\n", this.path);
    }
  }

}
