package server.request;

import java.util.Map;

import server.Response;

public class Post extends Request {

    public Post(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
