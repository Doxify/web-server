package server.request;

import java.util.Map;

import server.Response;

public class Head extends Request {

    public Head(Map<String, String> headers, String path, String method, String version) {
        super(headers, path, method, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Response execute() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
