package server.request;

import java.io.File;
import java.util.Map;

import server.response.Response;
import utils.Status;

public class Delete extends Request {

    public Delete(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        File file = new File(this.path);

        if (!file.exists()) {
            // System.out.println("[DEBUG] Client requested a resource that doesn't exist to be deleted.");
            res.setStatus(Status.NOT_FOUND);
            return res;
        }

        if (file.delete()) {
            // success
            res.setStatus(Status.NO_CONTENT);
            // System.out.printf("[DEBUG] Successfully deleted %s.\n", this.path);
        } else {
            // error
            res.setStatus(Status.INTERNAL_SERVER_ERROR);
            // System.out.printf("[DEBUG] Error occurred while deleting %s.\n", this.path);
        }

        return res;
    }

}
