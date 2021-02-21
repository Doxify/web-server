package server.request;

import java.util.Map;

import server.Response;

public class Post extends Request {

    public Post(Map<String, String> headers, String path, String method, String version, String body) {
        super(headers, path, method, version, body);
    }

    @Override
    public Response execute() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "arg1");
        Process p;

        // setting all request headers as enviornment variables
        Map<String, String> env = pb.environment();
        env.clear();
        this.headers.entrySet().forEach(header -> {
            env.put("HTTP_" + header.getKey().toUpperCase(), header.getValue().toUpperCase());
        });

        // script should recieve the body of PUT and POST via stdin

        try {
            p = pb.start();

            // p.

        } catch (Exception e) {
            // throw 500 error here
        }

        return res;
    }

}
