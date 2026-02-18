package p2p;

import java.io.*;
import java.net.*;

/**
 * Nodo peer - es a la vez servidor (recibe mensajes) y cliente (envía mensajes).
 * Se registra en el tracker y se comunica directamente con otros peers.
 */
public class PeerNode {

    private final String peerId;
    private final int listenPort;
    private final TrackerClient tracker;

    public PeerNode(String peerId, int listenPort, TrackerClient tracker) {
        this.peerId     = peerId;
        this.listenPort = listenPort;
        this.tracker    = tracker;
    }

    public void start() throws IOException {
        
        tracker.register(peerId, listenPort);
        System.out.println("[PEER " + peerId + "] Registrado en el tracker.");

        new Thread(this::listenLoop).start();

      
        consoleLoop();
    }

    private void listenLoop() {
        try (ServerSocket ss = new ServerSocket(listenPort)) {
            System.out.println("[PEER " + peerId + "] Escuchando en el puerto " + listenPort);
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> handleIncoming(s)).start();
            }
        } catch (IOException e) {
            System.out.println("[PEER " + peerId + "] Error en listener: " + e.getMessage());
        }
    }

    private void handleIncoming(Socket s) {
        try (s;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            String line = in.readLine();
            if (line == null) return;


            if (line.startsWith("MSG ")) {
                System.out.println("\n[MENSAJE RECIBIDO] " + line.substring(4));
                System.out.print("> ");
            } else {
                System.out.println("\n[RECIBIDO] " + line);
                System.out.print("> ");
            }

        } catch (IOException e) {
            System.out.println("[PEER " + peerId + "] Error recibiendo mensaje: " + e.getMessage());
        }
    }

    private void consoleLoop() throws IOException {
        System.out.println("\nComandos disponibles:");
        System.out.println("  peers              → ver peers conectados");
        System.out.println("  send <peerId> <msg> → enviar mensaje a un peer");
        System.out.println("  exit               → salir\n");

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("> ");
            String line = console.readLine();
            if (line == null) return;

            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Cerrando peer...");
                return;
            }

            if (line.equalsIgnoreCase("peers")) {
                var peers = tracker.listPeers();
                if (peers.isEmpty()) {
                    System.out.println("No hay otros peers conectados.");
                } else {
                    peers.forEach((id, hp) ->
                        System.out.println("  " + id + " → " + hp.host + ":" + hp.port));
                }
                continue;
            }

            if (line.startsWith("send ")) {
           
                String[] parts = line.split("\\s+", 3);
                if (parts.length < 3) {
                    System.out.println("Uso: send <peerId> <mensaje...>");
                    continue;
                }
                String toPeerId = parts[1];
                String msg      = parts[2];
                var peers = tracker.listPeers();
                var hp = peers.get(toPeerId);
                if (hp == null) {
                    System.out.println("Peer no encontrado: " + toPeerId);
                    continue;
                }
                sendMessage(hp.host, hp.port, msg);
                continue;
            }

            System.out.println("Comando desconocido. Usa: peers | send <peerId> <msg> | exit");
        }
    }

    private void sendMessage(String host, int port, String msg) {
        try (Socket s = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            out.write("MSG " + peerId + " " + msg);
            out.newLine();
            out.flush();
            System.out.println("[ENVIADO] a " + host + ":" + port);

        } catch (IOException e) {
            System.out.println("[ERROR ENVIANDO] " + e.getMessage());
        }
    }
}