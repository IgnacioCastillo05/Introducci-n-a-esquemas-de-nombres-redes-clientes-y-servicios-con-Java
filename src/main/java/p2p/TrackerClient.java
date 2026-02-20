package p2p;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Cliente para comunicarse con el Tracker.
 * Permite registrarse y consultar la lista de peers.
 */
public class TrackerClient {

    private final String host;
    private final int port;

    public TrackerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void register(String peerId, int peerPort) {
        String resp = request("REGISTER " + peerId + " " + peerPort);
        if (!resp.startsWith("OK"))
            throw new RuntimeException("Register fallido: " + resp);
        System.out.println("[TRACKER] Registrado exitosamente.");
    }

    public Map<String, HostPort> listPeers() {
        String resp = request("LIST");
        if (!resp.startsWith("PEERS"))
            throw new RuntimeException("LIST fallido: " + resp);

        String payload = resp.substring("PEERS".length()).trim();
        Map<String, HostPort> out = new HashMap<>();
        if (payload.isBlank()) return out;

        String[] entries = payload.split(",");
        for (String e : entries) {
            // formato: peerId@ip:port
            String[] a  = e.split("@");
            String peerId = a[0];
            String[] hp = a[1].split(":");
            out.put(peerId, new HostPort(hp[0], Integer.parseInt(hp[1])));
        }
        return out;
    }

    private String request(String line) {
        try (Socket s = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
             BufferedReader in  = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            out.write(line);
            out.newLine();
            out.flush();
            return in.readLine();

        } catch (IOException e) {
            throw new RuntimeException("Tracker no disponible: " + e.getMessage(), e);
        }
    }

    public static class HostPort {
        public final String host;
        public final int port;

        public HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}