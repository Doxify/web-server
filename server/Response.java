package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import server.request.Request;
import utils.Configuration;

public class Response {

    private Map<String, String> headers;    // map of response headers
    private Request             request;    // request we are responding to
    private int                 status;     // HTTP status of this request/response
    private byte[]              content;    // the response body

    public Response(Request request) {
        this.headers = new HashMap<String, String>();
        this.request = request;

        // Set default headers
        setHeader("Date", Configuration.df.format(new Date()));
        setHeader("Server", "georgescu-jose-webserver");
    }

    // Returns the request object associated with this response.
    public Request getRequest() {
        return this.request;
    }

    // Returns the HTTP status code for the response
    public int getStatus() {
        return this.status;
    }

    // Sets the response's HTTP status code
    public void setStatus(int status) {
        this.status = status;
    }

    // Adds a new header or resets a pre-existing response header.
    public void setHeader(String header, String value) {
        this.headers.put(header, value);
    }

    // Sets the content (body) of the response.
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Converts this response to a array of bytes.
     * 
     * If an error occurs while generating the array of bytes, a generic
     * internal-server-error response is returned.
     * 
     * @return - a byte[] representing this Response object.
     */
    public byte[] generateResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            // write the status line
            stream.write((this.request.getVersion() + " " + this.status + "\r\n").getBytes());

            // write the headers
            for (Map.Entry<String,String> entry : headers.entrySet())
                stream.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes());


            // TODO: Maybe this should be decided somewhere else?
            stream.write(("Connection: Close\r\n").getBytes());
            
            // write the content
            stream.write(("\r\n").getBytes());
            stream.write(this.content);

            stream.close();
        } catch (IOException e) {
            // TODO returns a generic Internal Server Error (500) response.
            e.printStackTrace();
        }

        return stream.toByteArray();
    }

}
