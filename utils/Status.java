package utils;

public enum Status {

    OK(200, "OK"), 
    CREATED(201, "Created"), 
    NO_CONTENT(204, "No Content"), 
    NOT_MODIFIED(304, "Not Modified"),
    BAD_REQUEST(400, "Bad Request"), 
    UNAUTHORIZED(401, "Unauthorized"), 
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"), 
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    public final int code;
    public final String message;

    private Status(int code, String message) {
        this.code = code;
        this.message = message;
    }
}