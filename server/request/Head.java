package server.request;

import java.util.Map;

import server.response.Response;

public class Head extends Get {

    public Head(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        // execute request as if it was a get request then remove the body since
        // head requests don't have one.
        super.execute();
        res.setContent(null);
        return res;
    }

}
