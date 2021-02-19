package server.request;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import server.Response;
import utils.Status;

public class Put extends Request {

    public Put(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        // no body is supplied, bad request was made.
        if(this.body.length() <= 0) {
            res.setStatus(Status.BAD_REQUEST);
            return res;
        }

        try {
            Path resourcePath = this.getResource();

            // attempt to write the body to the resource
            if(Files.exists(resourcePath)) {
                // overwrites the file
                Files.write(resourcePath, this.body.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                res.setStatus(Status.CREATED);
            } else {
                // creates a new file and writes the body to it
                Files.write(resourcePath, this.body.getBytes(),  StandardOpenOption.CREATE);
                res.setStatus(Status.NO_CONTENT);
            }

        } catch (IOException | SecurityException e) {
            res.setStatus(Status.INTERNAL_SERVER_ERROR);
            System.out.printf("[DEBUG] Error while writing to %s: %s\n", this.path, e.getMessage());
        }

        return res;
    }
    
}
