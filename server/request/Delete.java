package server.request;

import java.io.File;
import java.util.Map;

import server.Response;
import utils.Configuration;
import utils.Status;

public class Delete extends Request {

    public Delete(Map<String, String> headers, String path, String method, String version) {
        super(headers, path, method, version);
    }

    @Override
    public Response execute() {
        // create a response object
        Response res = new Response(this);

        System.out.println("[DEBUG] Executing a DELETE request");

        // TODO Handle auth checks here

        System.out.println("[DEBUG] Searching for resource to delete");

        String rootPathRaw = Configuration.getHttpd().getProperty("DocumentRoot");
        String rootPath = rootPathRaw.replaceAll("\"", "");
        File file = new File(rootPath + this.path);
        
        // file or directory does not exist
        if(!file.exists()) {
            String message = "The requested resource does not exist.\r\n";
            res.setStatus(Status.OK);
            res.setContent(message.getBytes());
            res.setHeader("Content-Type", "text");
            res.setHeader("Content-Length", String.valueOf(message.length()));
            System.out.println("[DEBUG] User requested a resource that doesn't exist to be deleted.");
            
            return res;
        }


        if(deleteDirectory(file)) {
            // success
            res.setStatus(Status.NO_CONTENT);
            System.out.printf("[DEBUG] Successfully deleted %s.\n", rootPath + this.path);
        } else {
            // error
            res.setStatus(Status.INTERNAL_SERVER_ERROR);
            System.out.printf("[DEBUG] Error occurred while deleting %s.\n", rootPath + this.path);
        }

        return res;
    }

    /**
     * Deletes a directory or file recursively.
     * 
     * @param directory - File object representing a directory or file
     * @return - true if directory was successfully deleted, false if not
     */
    private boolean deleteDirectory(File directory) {
        File[] contents = directory.listFiles();

        if(contents != null) {
            for(File f : contents) {
                if(f.isDirectory()) {
                    return deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        
        return directory.delete();
    }
    
}
