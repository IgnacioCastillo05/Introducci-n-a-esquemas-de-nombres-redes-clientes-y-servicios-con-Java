package sockets;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import urls.MiniBrowser;

public class HttpServerOriginal {

    private static final String WEB_ROOT = "src/main/resources/webroot";
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

            OutputStream rawOut = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;

            boolean isFirstLine = true;

            String reqPath = "";
            String fullUri = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (isFirstLine) {
                    String[] firstLineTokens = inputLine.split(" ");
                    String method = firstLineTokens[0];
                    fullUri = firstLineTokens[1];
                    String protocolVersion = firstLineTokens[2];

                    URI requestedURI = new URI(fullUri);

                    reqPath = requestedURI.getPath();
                    String reqQuery = requestedURI.getQuery();
                    System.out.println("Requested path: " + reqPath);
                    isFirstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            // Ruta raíz → index.html
            if (reqPath.equals("/")) {
                reqPath = "/index.html";
            }

            // Ruta /fetch?url= → usar MiniBrowser
            if (fullUri.startsWith("/fetch?url=")) {
                handleFetch(rawOut, fullUri);
                in.close();
                clientSocket.close();
                continue;
            }

            // Verificar endpoints REST registrados
            WebMethod wm = endPoints.get(reqPath);
            if (wm != null) {
                String body = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">"
                        + "<title>" + reqPath + "</title></head><body>"
                        + wm.execute()
                        + "</body></html>";
                sendText(rawOut, 200, "text/html", body);
                in.close();
                clientSocket.close();
                continue;
            }

            // Servir archivo estático desde webroot
            File file = new File(WEB_ROOT + reqPath);
            if (file.exists() && !file.isDirectory()) {
                byte[] content = Files.readAllBytes(file.toPath());
                String contentType = getContentType(file.getName());
                String header = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: " + contentType + "\r\n"
                        + "Content-Length: " + content.length + "\r\n"
                        + "\r\n";
                rawOut.write(header.getBytes());
                rawOut.write(content);
                rawOut.flush();
                System.out.println("Served: " + file.getName() + " (" + contentType + ")");
            } else {
                sendText(rawOut, 404, "text/html",
                        "<!DOCTYPE html><html><body><h1>404 Not Found</h1></body></html>");
            }

            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static void get(String path, WebMethod wm) {
        endPoints.put(path, wm);
    }

    private static void handleFetch(OutputStream out, String path) throws IOException {
        try {
            String url = URLDecoder.decode(
                    path.substring("/fetch?url=".length()), "UTF-8");
            System.out.println("Fetching: " + url);
            String content = MiniBrowser.fetchUrl(url);
            sendText(out, 200, "text/html", content);
        } catch (Exception e) {
            sendText(out, 500, "text/html",
                    "<h1>Error</h1><p>" + e.getMessage() + "</p><a href='/'>Volver</a>");
        }
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
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
