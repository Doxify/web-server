package server.request;

import server.logs.Log;

import java.util.Map;

import server.Response;

public abstract class Request {

    private Map<String,String>  headers;
    private String              path;
    private String              method; // TODO: Turn this into an enum if feeling fancy.
    private String              version;

    public Request(Map<String,String> headers, String path, String method, String version) {
        this.headers = headers;
        this.path = path;
        this.method = method;
        this.version = version;
    }

    public abstract Response execute();

    public String getPath() {
        return this.path;
    }

    public String getMethod() {
        return this.method;
    }

    public String getVersion() {
        return this.version;
    }

    public Map<String,String> getHeaders() {
        return this.headers;
    }

//    public void logThis() {
//      for (String name: headers.keySet()){
//        String key = name.toString();
//        String value = headers.get(name).toString();
//        System.out.println(key + " " + value);
//      }
//    }

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
