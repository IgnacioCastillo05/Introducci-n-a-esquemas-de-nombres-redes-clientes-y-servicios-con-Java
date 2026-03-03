package sockets;
import java.io.IOException;
import java.net.URISyntaxException;

public class HelloWebApp {
    public static void main(String[] args) throws IOException, URISyntaxException {
        HttpServerOriginal.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        HttpServerOriginal.get("/frommethod", (req, res) -> euler());
        HttpServerOriginal.get("/pi", (req, res) -> "PI = " + Math.PI);

        HttpServerOriginal.staticfiles("webroot/public");

        HttpServerOriginal.main(args);
    }

    public static String euler() {
        return "e = " + Math.E;
    }
}