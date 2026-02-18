package sockets;

import urls.MiniBrowser;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class HttpServer {

    private static final String WEB_ROOT = "src/main/resources/webroot";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        // Loop para múltiples solicitudes seguidas (no concurrentes)
        while (true) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                continue;
            }
            try {
                handleRequest(clientSocket);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                clientSocket.close();
            }
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        // Leer línea de solicitud HTTP
        String requestLine = in.readLine();
        if (requestLine == null) return;
        System.out.println("Received: " + requestLine);

        // Leer headers hasta línea vacía
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.isEmpty()) break;
        }

        // Parsear: GET /ruta HTTP/1.1
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            sendError(out, 400, "Bad Request");
            return;
        }

        String path = parts[1];
        if (path.equals("/")) path = "/index.html";

        // Ruta /fetch?url= → usar MiniBrowser para obtener página externa
        if (path.startsWith("/fetch?url=")) {
            handleFetch(out, path);
            return;
        }

        // Servir archivo local desde webroot
        File file = new File(WEB_ROOT + path);
        if (!file.exists() || file.isDirectory()) {
            sendError(out, 404, "Not Found");
            return;
        }

        byte[] content = Files.readAllBytes(file.toPath());
        String contentType = getContentType(file.getName());

        String header = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: " + contentType + "\r\n"
            + "Content-Length: " + content.length + "\r\n"
            + "\r\n";
        out.write(header.getBytes());
        out.write(content);
        out.flush();
        System.out.println("Served: " + file.getName() + " (" + contentType + ")");
    }

    private static void handleFetch(OutputStream out, String path) throws IOException {
        try {
            String url = java.net.URLDecoder.decode(
                path.substring("/fetch?url=".length()), "UTF-8");
            System.out.println("Fetching: " + url);
            String content = MiniBrowser.fetchUrl(url);
            sendText(out, 200, "text/html", content);
        } catch (Exception e) {
            sendText(out, 500, "text/html",
                "<h1>Error</h1><p>" + e.getMessage() + "</p><a href='/'>Volver</a>");
        }
    }

    private static void sendError(OutputStream out, int code, String msg) throws IOException {
        sendText(out, code, "text/html",
            "<!DOCTYPE html><html><body><h1>" + code + " " + msg + "</h1></body></html>");
    }

    private static void sendText(OutputStream out, int code, String type, String body)
            throws IOException {
        byte[] content = body.getBytes("UTF-8");
        String status = (code == 200) ? "OK" : (code == 404) ? "Not Found" : "Error";
        String header = "HTTP/1.1 " + code + " " + status + "\r\n"
            + "Content-Type: " + type + "; charset=UTF-8\r\n"
            + "Content-Length: " + content.length + "\r\n"
            + "\r\n";
        out.write(header.getBytes());
        out.write(content);
        out.flush();
    }

    private static String getContentType(String name) {
        name = name.toLowerCase();
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".css"))  return "text/css";
        if (name.endsWith(".js"))   return "application/javascript";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png"))  return "image/png";
        if (name.endsWith(".gif"))  return "image/gif";
        if (name.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
} 