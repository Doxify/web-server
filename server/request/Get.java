package server.request;

import java.io.IOException;
import java.nio.file.Files;
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
        if (Authenticate.requiresAuth()) {
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
            res.setContent(content);

            // set status code
            res.setStatus(Status.OK);

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
