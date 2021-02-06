package server;

import java.util.HashMap;

public class Request {

    private String path;
    private String method; // TODO: Turn this into an enum if feeling fancy.
    private String version;
    private HashMap<String,String> headers;

    public Request(String path, String method, String version, HashMap<String,String> headers) {
        this.path = path;
        this.method = method;
        this.version = version;
        this.headers = headers;
    }

    public String getPath() {
        return this.path;
    }

    public String getMethod() {
        return this.method;
    }

    public HashMap<String,String> getHeaders() {
        return this.headers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Path: " + this.path + "\n");
        sb.append("Method: " + this.method + "\n");
        sb.append("Version: " + this.version + "\n");
        
        this.headers.entrySet().forEach(entry -> {
            sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
        });

        return sb.toString();
    }
    
}
