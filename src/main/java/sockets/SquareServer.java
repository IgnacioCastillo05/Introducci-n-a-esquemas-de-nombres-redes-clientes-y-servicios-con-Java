package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Ejercicio 4.3.1 - Sección 4.3
 * Servidor que recibe un número y responde con su cuadrado.
 *
 * Para probarlo usa el EchoClient del lab apuntando al puerto 35000,
 * escribe un número y el servidor responderá con el cuadrado.
 */
public class SquareServer {

    private static final int PORT = 35001;
    private static final Logger logger = Logger.getLogger(SquareServer.class.getName());
    private static volatile boolean running = true;

    public static void main(String[] args) {

        logger.info("Servidor de cuadrados escuchando en el puerto " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Se usa while(running) para que pueda atender clientes uno tras otro sin apagarse
            // que es diferente al EchoServer del ejemplo que solo atiende una conexión y muere.
            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Cliente conectado: " + clientSocket.getInetAddress());
                handleClient(clientSocket);
            }

        } catch (IOException e) {
            logger.severe("Error en el servidor: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                logger.info("Recibido: " + inputLine);

                try {
                    double number = Double.parseDouble(inputLine.trim());
                    double result = number * number;
                    out.println("Respuesta: " + result);
                    logger.info("Respondido: " + result);
                } catch (NumberFormatException e) {
                    out.println("Error: '" + inputLine + "' no es un número válido.");
                    logger.warning("Entrada inválida: " + inputLine);
                }
            }

        } catch (IOException e) {
            logger.severe("Error con el cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                logger.info("Cliente desconectado.");
            } catch (IOException e) {
                logger.severe("Error cerrando el socket: " + e.getMessage());
            }
        }
    }
}