package server;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import server.request.Request;

public class Response {

    private Map<String, String> headers;
    private Request request;
    private int status;
    private long size;
    private byte[] content;
    private String contentType;
    private String contentPath;
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

    public void setContentType(String contentType) {
      this.contentType = contentType;
    }

    public void setSize(long size) {
      this.size = size;
    }

    public void setContent(byte[] content) {
      this.content = content;
    }

    public void setContentPath(String path) {
      this.contentPath = path;
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
    public byte[] generateResponse() throws IOException {

        int i;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"); // Mon, 27 Jul 2009 12:28:53 GMT

        // sets content length & content from requested path
        File file = new File(this.contentPath);
        InputStream inStream = new DataInputStream(new FileInputStream(file));
        this.size = (file.length());

      try {
        // Require for all server responses
        outStream.write((this.request.getVersion() + " " + this.status  + "\r\n").getBytes());
        outStream.write(("Date: " + date.format(new Date())  + "\r\n").getBytes());
        outStream.write(("Server: Georgescu-Gonzalez-Server\r\n").getBytes());
        outStream.write(("Content-Length: " + this.size  + "\r\n").getBytes());
        outStream.write(("ContentType: " + this.contentType + "\r\n").getBytes());

        // Headers
        for (Map.Entry<String,String> entry : request.getHeaders().entrySet())
          outStream.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes());

        outStream.write(("\r\n").getBytes());

        // Content
        while ((i = inStream.read(this.content)) != -1)
          outStream.write(this.content, 0, i); // writes bytes from file into output stream

        outStream.write(("\r\n").getBytes());
        outStream.flush();
        outStream.close();

        } catch (IOException e) {
            // TODO Handle exception
        }

        return outStream.toByteArray();
    }

}
