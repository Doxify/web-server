package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import server.request.Request;

public class Response {

    private Map<String, String> headers;
    private Request request;
    private int status;
    private int size;
    private String contentType;
    private int contentLength;
    private byte[] content;
    private boolean sent;

    public Response(Request request) {
        this.headers = new HashMap<String, String>();
        this.request = request;
        this.sent = false;
    }

    public Request getRequest() {
        return this.request;
    }

    public int getStatus() {
        return this.status;
    }

    public String getContentType() { return this.contentType; }

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    public Response setContentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Response setHeader(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isSent() {
        return this.sent;
    }

    // TODO: Complete this function, this is just a skeleton of what a response
    // at the moment.
    public byte[] generateResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"); // Mon, 27 Jul 2009 12:28:53 GMT

        try {
            stream.write((this.request.getVersion() + " " + this.status).getBytes());
            stream.write(("Date: " + date).getBytes());
            stream.write(("Server: Georgescu-Gonzalez-Server").getBytes());
            stream.write(("Last-Modified: " + date).getBytes()); //TODO change "date" to "last-modified" when available
            stream.write(("Content-Length: " + this.contentLength).getBytes());
            stream.write(("ContentType: " + this.contentType + "\r\n").getBytes());
            stream.write(("\r\n").getBytes());
            stream.write(this.content);
            stream.write(("Connection: Closed").getBytes()); //TODO change from string to connection status
            stream.write(("\r\n\r\n").getBytes());
        } catch (IOException e) {
            // TODO Handle exception
        }

        return stream.toByteArray();
    }

}
