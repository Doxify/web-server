package server;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import server.request.Request;
import utils.Constants;
import utils.Status;

public class Response {

    private Map<String, String> headers; // map of response headers
    private Request request; // request we are responding to
    private Status status; // HTTP status of this request/response
    private byte[] content; // the response body

    public Response(Request request) {
        this.headers = new HashMap<String, String>();
        this.request = request;

        // Set default headers
        setHeader("Date", Constants.dateFormat.format(new Date()));
        setHeader("Server", "georgescu-jose-webserver");
        setHeader("Cache-Control", "max-age=86400 public"); // max-age 24hr
        setHeader("Connection", "close");
        setHeader("Content-Type", "text/html");
        setHeader("Content-Length", "0");
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
        } catch (Exception e) {
            e.printStackTrace();
            return getGenericInternalServerError();

        }

        return stream.toByteArray();
    }

    private final byte[] getGenericInternalServerError() {
        // StringBuilder response = new StringBuilder();
        // response.append("HTTP/1.1 500\r\n");

        // // write the headers
        // for (Map.Entry<String, String> entry : this.headers.entrySet()) {
        // response.append()
        // stream.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes());
        // }

        // return response.toString().getBytes();
        return null;
    }
}
