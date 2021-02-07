package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private Map<String, String> headers;
    private Request request;
    private int status;    
    private int size;
    private String contentType;
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

    public Response setStatus(int status) {
        this.status = status;
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

        try {
            stream.write((this.request.getVersion() + " \r\n" + this.status).getBytes());
            stream.write(("ContentType: " + this.contentType + "\r\n").getBytes());
            stream.write(("\r\n").getBytes());
            stream.write(this.content);
            stream.write(("\r\n\r\n").getBytes());
        } catch (IOException e) {
            // TODO Handle exception
        }

        return stream.toByteArray();
    }

}
