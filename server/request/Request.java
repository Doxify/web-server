package server.request;

import java.util.Map;

import server.Response;

public abstract class Request {

    protected Map<String,String>  headers;  // map of request headers
    protected String              path;     // path of the requested resource
    protected String              method;   // HTTP method
    protected String              version;  // HTTP request version

    public Request(Map<String,String> headers, String path, String method, String version) {
        this.headers = headers;
        this.path = path;
        this.method = method;
        this.version = version;
    }

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

    /**
     * This method executes this request and returns a Response object that
     * represents the outcome of this Request's execution.
     * 
     * @return Response object with all properties set.
     */
    public abstract Response execute();

    /**
     * Determines if this request requires authentication headers or not
     * 
     * @return true if auth is required, false if not
     */
    public boolean requiresAuth() {
        // check for htaccess in directory
        // return whether or not it is present
        return false;
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
