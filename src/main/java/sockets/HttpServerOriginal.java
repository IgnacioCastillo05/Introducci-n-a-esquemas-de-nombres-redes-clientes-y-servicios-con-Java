package sockets;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServerOriginal {

    static Map<String, WebMethod> endPoints = new HashMap<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean running = true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;

        boolean isFirstLine = true;
        
        String reqPath = "";
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (isFirstLine) {
                String[] firstLineTokens = inputLine.split(" ");
                String method = firstLineTokens[0];
                String uristr = firstLineTokens[1];
                String protocolVersion = firstLineTokens[2];

                URI requestedURI = new URI(uristr);
                

                reqPath = requestedURI.getPath();
                String reqQuery = requestedURI.getQuery();
                System.out.println("Requested path: " + reqPath);
                isFirstLine = false;
            }
            if (!in.ready()) {
                break;
            }
        }

        WebMethod wm = endPoints.get(reqPath);
        if (wm != null) {
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<title>pi</title>"
                    + "</head>"
                    + "<body>"
                    + wm.execute()
                    + "</body>"
                    + "</html>";
        } else {
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<title>Title of the document</title>"
                    + "</head>"
                    + "<body>"
                    + "My Web Site"
                    + "</body>"
                    + "</html>";
        }
        out.println(outputLine);
        out.close();
        in.close();
        clientSocket.close();
    }
    serverSocket.close();
    }
    public static void get(String path, WebMethod wm){
        endPoints.put(path, wm);
    }
}


