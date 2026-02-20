package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Servidor que recibe números y responde con operaciones trigonométricas.
 * Por defecto calcula coseno.
 * Puede cambiar de función con comandos "fun:sin", "fun:cos", "fun:tan"
 */
public class TrigServer {

    private static final int PORT = 35000;
    private static final Logger logger = Logger.getLogger(TrigServer.class.getName());
    private static volatile boolean running = true;

    public static void main(String[] args) {

        logger.info("Servidor trigonométrico escuchando en el puerto " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

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
        String currentFunction = "cos"; // Por defecto coseno
        
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                logger.info("Recibido: " + inputLine);

                // Verificar si es un comando para cambiar función
                if (inputLine.trim().toLowerCase().startsWith("fun:")) {
                    String newFunction = inputLine.trim().substring(4).toLowerCase();
                    if (newFunction.equals("sin") || newFunction.equals("cos") || newFunction.equals("tan")) {
                        currentFunction = newFunction;
                        out.println("Función cambiada a: " + currentFunction);
                        logger.info("Función cambiada a: " + currentFunction);
                    } else {
                        out.println("Error: Función no válida. Use sin, cos o tan.");
                        logger.warning("Función inválida: " + newFunction);
                    }
                } else {
                    // Interpretar como número y calcular
                    try {
                        double number = parseNumber(inputLine.trim());
                        double result = calculateTrig(currentFunction, number);
                        out.println("Respuesta: " + result);
                        logger.info("Respondido: " + result + " usando " + currentFunction);
                    } catch (NumberFormatException e) {
                        out.println("Error: '" + inputLine + "' no es un número válido.");
                        logger.warning("Entrada inválida: " + inputLine);
                    }
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

    private static double parseNumber(String input) throws NumberFormatException {
       
        input = input.toLowerCase().trim();
        
        if (input.equals("π") || input.equals("pi")) {
            return Math.PI;
        } else if (input.contains("π") || input.contains("pi")) {
         
            input = input.replace("π", String.valueOf(Math.PI));
            input = input.replace("pi", String.valueOf(Math.PI));
            // Evaluamos las expresiones simples de división
            if (input.contains("/")) {
                String[] parts = input.split("/");
                return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            }
        }
        
        return Double.parseDouble(input);
    }

    private static double calculateTrig(String function, double number) {
        switch (function) {
            case "sin":
                return Math.sin(number);
            case "cos":
                return Math.cos(number);
            case "tan":
                return Math.tan(number);
            default:
                return Math.cos(number); 
        }
    }
}
