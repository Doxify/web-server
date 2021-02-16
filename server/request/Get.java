package server.request;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import server.Response;
import utils.Configuration;
import utils.Status;

public class Get extends Request {

    public Get(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        // create a response object
        Response res = new Response(this);

        System.out.println("[DEBUG] Executing a GET request");

        // TODO Handle auth checks here

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

    /**
     * Helper function that gets the extension of the file at the Request's path If
     * the path is "/index.html", this function returns "html".
     * 
     * @return - the extension of the resource associated with the Request
     */
    protected String getResourceFileExtension() {
        if ("/".equals(this.path)) {
            return "html";
        } else {
            int i = this.path.lastIndexOf('.');
            if (i > 0) {
                return this.path.substring(i + 1);
            } else {
                return "";
            }
        }
    }

}
