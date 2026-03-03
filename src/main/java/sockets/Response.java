package sockets;

public class Response {

    private int statusCode;
    private String contentType;

    public Response() {
        this.statusCode = 200;
        this.contentType = "text/html";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
