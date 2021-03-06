package server.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import server.request.Request;
import utils.Constants;
import utils.Status;

public class Response {

    protected Map<String, String> headers; // map of response headers
    protected Request request; // request we are responding to
    protected Status status; // HTTP status of this request/response
    protected byte[] content; // the response body

    public Response(Request request) {
        this.headers = new HashMap<String, String>();
        this.request = request;

        // Set default headers
        setHeader("Date", Constants.dateFormat.format(new Date()));
        setHeader("Server", "georgescu-jose-webserver");
        setHeader("Cache-Control", "public max-age=86400"); // max-age 24hr
        setHeader("Connection", "close");
    }

    // Returns the request object associated with this response.
    public Request getRequest() {
        return this.request;
    }

    // Returns the HTTP status code for the response
    public Status getStatus() {
        return this.status;
    }

    // Returns the response headers
    public String getHeader(String key) {
        return this.headers.get(key);
    }

    // Sets the response's HTTP status code
    public void setStatus(Status status) {
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
     * Converts this response to a array of bytes in the proper HTTP response format.
     * If an error occurs while generating the array of bytes it returns null.
     *
     * @return - a byte[] representing this Response object.
     */
    public byte[] generateResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            // write the status line
            stream.write((this.request.getVersion() + " " + this.status.code + "\r\n").getBytes());

            // write the headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                stream.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes());
            }

            // write the content if it exists
            if (this.content != null) {
                stream.write(("\r\n").getBytes());
                stream.write(this.content);
            }

            stream.close();

            return stream.toByteArray();
        } catch (IOException e) {
            // return internal server error
            this.setStatus(Status.INTERNAL_SERVER_ERROR);
            this.setHeader("Content-Type", "text/plain");
            this.setHeader("Content-Length", "0");
            return generateGenericResponse();
        }
    }

    protected final byte[] generateGenericResponse() {
        String response = "";

        // write the status line
        response += (this.request.getVersion() + " " + this.status.code + "\r\n");
        
        // write the headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response += (entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        
        return response.getBytes();
    }
}
