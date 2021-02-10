package server.request;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import server.Response;
import utils.Configuration;

public class Get extends Request {

    public Get(Map<String, String> headers, String path, String method, String version) {
        super(headers, path, method, version);
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
            byte[] content = getResource();
            res.setContent(content);

            // set status code
            res.setStatus(200);

            // get the mime type then set the type and length header
            String ext = getResourceFileExtension();
            res.setHeader("Content-Type", Configuration.getMimeType(ext));
            res.setHeader("Content-Length", String.valueOf(content.length));

        } catch (IOException | NullPointerException e) {
            // set status code
            res.setStatus(404);

            // set response headers
            res.setHeader("Content-Length", "0");
            res.setContent("".getBytes());

            System.out.printf("[DEBUG] Resource %s was not found.\n", e.getMessage());
        }

        return res;
    }

    /**
     * This function takes a path relative to the server and returns it in the
     * form of a byte array.
     * 
     * NOTES: 
     *   It uses httpd.conf:DocumentRoot as the root directory.
     *   If the path is "/" it returns index.html from the root directory.
     * 
     * @return - requested resource in the form of a byte array
     */
    private byte[] getResource() throws IOException {
        String rootPathRaw = Configuration.getHttpd().getProperty("DocumentRoot");
        String rootPath = rootPathRaw.replaceAll("\"", "");

        if("/".equals(this.path)) {
            // return index.html from the document root.
            System.out.printf("[DEBUG] Looking for %s\n", rootPath + "index.html");
            return Files.readAllBytes(Paths.get(rootPath + "index.html"));
        }

        System.out.printf("[DEBUG] Looking for %s\n", rootPath + this.path);
        return Files.readAllBytes(Paths.get(rootPath + this.path));
    }

    /**
     * Helper function that gets the extension of the file at the Request's path
     * If the path is "/index.html", this function returns "html".
     * 
     * @return - the extension of the resource associated with the Request
     */
    private String getResourceFileExtension() {
        if("/".equals(this.path)) {
            return "html";
        } else {
            String rootPath = Configuration.getHttpd().getProperty("DocumentRoot");
            String filePath = rootPath + this.path;
            
            int i = filePath.lastIndexOf('.');
            if (i > 0) {
                return filePath.substring(i + 1);
            } else {
                return "";
            }
        }
    }
    
}
