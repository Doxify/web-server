package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import server.request.Request;
import utils.Configuration;

public class Response {

  private Map<String, String> headers;
  private Request request;
  private int status;
  private byte[] content;
  private boolean sent;

  public Response(Request request) {
    this.headers = new HashMap<String, String>();
    this.request = request;
    this.sent = false;

    // Set the date and server headers
    setHeader("Date", Configuration.df.format(new Date()));
    setHeader("Server", "georgescu-jose-webserver");
  }

  public Request getRequest() {
    return this.request;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setHeader(String header, String value) {
    this.headers.put(header, value);
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public void setSent(boolean sent) {
    this.sent = sent;
  }

  public boolean isSent() {
    return this.sent;
  }

  public byte[] generateResponse() {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    try {
      // write the status line
      stream.write((this.request.getVersion() + " " + this.status + "\r\n").getBytes());

      // write the headers
      for (Map.Entry<String,String> entry : headers.entrySet())
        stream.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes());

      // write the content
      stream.write(("\r\n").getBytes());
      stream.write(this.content);
      stream.write(("\r\n").getBytes());

      stream.close();
    } catch (IOException e) {
      // TODO Handle exception
      e.printStackTrace();
    }

    return stream.toByteArray();
  }

}
