package server.response;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import server.request.Request;
import utils.Configuration;
import utils.Status;

public class CGI extends Response {

    private Request request;
    private byte[] scriptOutput;

    public CGI(Request request) {
        super(request);
        this.request = request;
    }

    public final Response executeScript() {
        ProcessBuilder pb = generateProcessBuilder();

        try {
            Process p = pb.start();
            OutputStream stdin = p.getOutputStream();
            InputStream stdout = p.getInputStream();

            // send script the body of POST and PUT requests
            String method = this.request.getMethod();
            if(method.equals("POST") || method.equals("PUT")) {
                stdin.write(this.request.getBody().getBytes());
            }

            // get the output from the script
            BufferedInputStream stream = new BufferedInputStream(stdout);
            scriptOutput = stream.readAllBytes();
            stream.close();
            
            this.setStatus(Status.OK);
        } catch(IOException e) {
            // System.out.println("Error occurred while executing script.");
            // System.out.println(e.getMessage());
            this.setStatus(Status.INTERNAL_SERVER_ERROR);
        }

        return this;
    }

    /**
     * Converts this response to a array of bytes in the proper HTTP format.
     *
     * If an error occurs while generating the array of bytes, a generic
     * internal-server-error response is returned.
     *
     * @return - a byte[] representing this Response object.
     */
    @Override
    public byte[] generateResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            // write the status line
            stream.write((this.request.getVersion() + " " + this.status.code + "\r\n").getBytes());

            // write default headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                stream.write((entry.getKey() + ": " + entry.getValue() + "\r").getBytes());
            }

            // write the rest of the response from the script's output
            stream.write(scriptOutput);

            stream.close();

            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private final ProcessBuilder generateProcessBuilder() {
        // getting the script that will be executed
        String path = this.request.getPath();
        String scriptName = path.substring(path.lastIndexOf("/") + 1);
        
        ProcessBuilder pb = new ProcessBuilder("./" + scriptName);
        Map<String, String> env = pb.environment();

        // set the working directory of the process
        pb.directory(new File(getScriptWorkingDirectory()));

        // set all request headers as enviornment variables
        env.clear(); // only want to send request data, not system env variables. 
        this.request.getHeaders().entrySet().forEach(header -> {
            env.put("HTTP_" + header.getKey().toUpperCase(), header.getValue().toUpperCase());
        });

        return pb;
    }

    private static final String getScriptWorkingDirectory() {
        // [0] = uri alias, [1] = absolute file system path
        String[] scriptAlias = Configuration.getConfigProperty("ScriptAlias").split(" ");
        String absolutePath = scriptAlias[1].replaceAll("\"", "");
        return absolutePath;
    }
    
}
