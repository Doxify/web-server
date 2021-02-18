package server.request;

import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import server.Response;
import utils.Authenticate;
import utils.Configuration;
import utils.Status;

public class Get extends Request {

    public Get(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {

        System.out.println("[DEBUG] Executing a GET request");

        Response res = new Response(this);

        // Handles Authentication if required
        if (Authenticate.requiresAuth(res.getRequest().getPath())) {
          switch (auth()) {
            case UNAUTHORIZED:
              res.getHeaders().put("WWW-Authenticate", "Basic"); // requests Auth Header
              res.setStatus(Status.UNAUTHORIZED); //set status code
              return res;
            case FORBIDDEN:
              res.setStatus(Status.FORBIDDEN);
              return res;
          }
        }

        try {
          System.out.println("[DEBUG] Getting resource for request");

          // get the resource
          byte[] content = Files.readAllBytes(this.getResource());
          // set Dates needed
          Date today = new Date();
          Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

          //if (true) -> leave content blank and status 304
          //else -> set content, headers and status 200
          if (cacheActive(content))
            res.setStatus(Status.NOT_MODIFIED); // set status code 304
          else {

            res.setContent(content);
            // Headers for caching - Etag is unique and contains content length to compare file changes.
            res.setHeader("Expire", Configuration.df.format(tomorrow));
            res.setHeader("Etag", today.getTime() + "==" + content.length);

            updateLastModified(); // update last modified date
            res.setHeader("Last-Modified", Configuration.df.format(lastModified()));

            res.setStatus(Status.OK); // set status code 200
          }

          // get the mime type then set the type and length header
          String ext = this.getResourceFileExtension();
          res.setHeader("Content-Type", Configuration.getMimeType(ext));
          res.setHeader("Content-Length", String.valueOf(content.length));

        } catch (IOException | NullPointerException e) {
            // set status code
            res.setStatus(Status.NOT_FOUND);
            System.out.printf("[DEBUG] Resource %s was not found.\n", e.getMessage());
        }

        return res;
    }

}
