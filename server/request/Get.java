package server.request;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

import server.Handler;
import server.Response;
import server.Server;

public class Get extends Request {

    Response response = new Response(this);

    public Get(Map<String, String> headers, String path, String method, String version) throws IOException {
        super(headers, path, method, version);

        // TODO Auto-generated constructor stub
        response.setContentType(Server.mimeTypeConfig(path));
        response.setStatus(Server.statusConfig(path));
        response.setContentPath(Server.pathConfig(path).toString());
        response.setContent(new byte[1]);
        Handler.handleRequest(this);


    }



    @Override
    public Response execute() throws IOException {
        // TODO Auto-generated method stub

        //TODO validate that the file exists
        //TODO validate user's permission
        //TODO create absolute path [If Needed: attach extension to the end path]

        //debug
        // OutputStream outStream = response.getOutputStream();
        return null;
    }

}
