package sockets;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServerOriginal {

    static Map<String, WebMethod> endPoints = new HashMap<>();
    private static String staticFilesPath = null;

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
            String reqQuery = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (isFirstLine) {
                    String[] firstLineTokens = inputLine.split(" ");
                    String method = firstLineTokens[0];
                    String uristr = firstLineTokens[1];
                    String protocolVersion = firstLineTokens[2];

                    URI requestedURI = new URI(uristr);

                    reqPath = requestedURI.getPath();
                    reqQuery = requestedURI.getQuery();
                    System.out.println("Requested path: " + reqPath);
                    System.out.println("Query string: " + reqQuery);
                    isFirstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            // Intentar servir archivos estáticos primero
            if (staticFilesPath != null) {
                String filePath = "target/classes/" + staticFilesPath + reqPath;
                File file = new File(filePath);
                if (file.exists() && !file.isDirectory()) {
                    serveStaticFile(clientSocket.getOutputStream(), file);
                    out.close();
                    in.close();
                    clientSocket.close();
                    continue;
                }
            }

            // Buscar en los endpoints registrados
            Request req = new Request(reqPath, reqQuery);
            Response res = new Response();

            WebMethod wm = endPoints.get(reqPath);
            if (wm != null) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Title</title>"
                        + "</head>"
                        + "<body>"
                        + wm.execute(req, res)
                        + "</body>"
                        + "</html>";
            } else {
                outputLine = "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>404</title>"
                        + "</head>"
                        + "<body>"
                        + "Not Found"
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

    public static void get(String path, WebMethod wm) {
        endPoints.put(path, wm);
    }

    public static void staticfiles(String path) {
        staticFilesPath = path;
    }

    private static void serveStaticFile(OutputStream out, File file) throws IOException {
        byte[] content = java.nio.file.Files.readAllBytes(file.toPath());
        String contentType = getContentType(file.getName());
        String header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + content.length + "\r\n"
                + "\r\n";
        out.write(header.getBytes());
        out.write(content);
        out.flush();
    }

    private static String getContentType(String name) {
        name = name.toLowerCase();
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
