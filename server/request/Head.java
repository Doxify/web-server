package server.request;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import server.Response;
import utils.Configuration;
import utils.Status;

public class Head extends Request {

    public Head(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        Response res = new Response(this);

        System.out.println("[DEBUG] Executing a HEAD request");

        // TODO Handle auth

        // TODO Implement this differently? HEAD is the exact same as GET except
        //      that it has no body. Everything else is the same.

        try {
            System.out.println("[DEBUG] Getting resource for request");

            // get the resource
            byte[] content = Files.readAllBytes(this.getResource());

            // set status code
            res.setStatus(Status.OK);

            // set last-modified header
            res.setHeader("Last-Modified", "today");

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
