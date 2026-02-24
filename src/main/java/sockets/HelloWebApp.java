package sockets;

import java.io.IOException;
import java.net.URISyntaxException;

import sockets.HttpServerOriginal;
public class HelloWebApp {
    public static void main(String[] args) throws IOException, URISyntaxException {
        HttpServerOriginal.get("/hello", () -> "Hello world!");
        HttpServerOriginal.get("/frommethod", () -> euler());
        HttpServerOriginal.get("/pi", () -> "PI = " + Math.PI);
        HttpServerOriginal.main(args);
    }

    public static String euler(){
        return "e = " + Math.E;
    }
}
