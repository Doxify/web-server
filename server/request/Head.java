package server.request;

import java.util.Map;

import server.Response;

public class Head extends Get {

    public Head(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        // execute request as if it was a get request
        super.execute();

        // remove body since head requests dont have a body
        res.setContent(null);

        return res;
    }

}
