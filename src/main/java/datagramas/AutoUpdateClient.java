package main.java.datagramas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

/**
 * Ejercicio 5.2.1 - Sección 5.2
 * Cliente UDP que consulta la hora al servidor cada 5 segundos.
 * Si el servidor no responde, mantiene la última hora recibida
 * y sigue intentando hasta que el servidor vuelva.
 */
public class AutoUpdateClient {

    private static final Logger LOGGER = Logger.getLogger(AutoUpdateClient.class.getName());
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 4445;
    private static final int TIMEOUT_MS = 2000;  
    private static final int INTERVAL_MS = 5000; 

    public static void main(String[] args) {

        LOGGER.info("Cliente de hora iniciado. Consultando cada 5 segundos...");
        LOGGER.info("(Si el servidor se apaga, se mantiene la última hora recibida)");

        String lastKnownTime = "Sin hora aún...";

        try (DatagramSocket socket = new DatagramSocket()) {

      
            socket.setSoTimeout(TIMEOUT_MS);

            InetAddress address = InetAddress.getByName(SERVER_HOST);

            while (true) {
                try {
 
                    byte[] sendBuf = new byte[256];
                    DatagramPacket request = new DatagramPacket(sendBuf, sendBuf.length, address, SERVER_PORT);
                    socket.send(request);

                    lastKnownTime = receiveTimeFromServer(socket, lastKnownTime);

                    Thread.sleep(INTERVAL_MS);
                } catch (InterruptedException ignored) {
                    break;
                }
            }

        } catch (IOException e) {
            LOGGER.severe("Error en el socket: " + e.getMessage());
        }
    }

    private static String receiveTimeFromServer(DatagramSocket socket, String lastKnownTime) {
        try {
            byte[] receiveBuf = new byte[256];
            DatagramPacket response = new DatagramPacket(receiveBuf, receiveBuf.length);
            socket.receive(response);

            lastKnownTime = new String(response.getData(), 0, response.getLength());
            LOGGER.info("[ACTUALIZADO] Hora del servidor: " + lastKnownTime);

        } catch (SocketTimeoutException ignored) {
            LOGGER.warning("[SIN RESPUESTA] Servidor caído. Última hora conocida: " + lastKnownTime);
        } catch (IOException e) {
            LOGGER.severe("Error recibiendo datos: " + e.getMessage());
        }
        return lastKnownTime;
    }
}