package p2p;

/**
 * Main del Peer.
 *
 * Cómo ejecutar (3 terminales, desde la raíz del proyecto):
 *
 * Terminal 1 - Tracker:
 * mvn exec:java -Dexec.mainClass="p2p.TrackerServer"
 *
 * Terminal 2 - PeerA:
 * mvn exec:java -Dexec.mainClass="p2p.PeerMain" -Dexec.args="peerA 7001 127.0.0.1"
 *
 * Terminal 3 - PeerB:
 * mvn exec:java -Dexec.mainClass="p2p.PeerMain" -Dexec.args="peerB 7002 127.0.0.1"
 */
public class PeerMain {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Uso: PeerMain <peerId> <listenPort> <trackerHost>");
            System.out.println("Ejemplo: PeerMain peerA 7001 127.0.0.1");
            return;
        }

        String peerId      = args[0];
        int listenPort     = Integer.parseInt(args[1]);
        String trackerHost = args[2];

        TrackerClient tracker = new TrackerClient(trackerHost, 6000);
        new PeerNode(peerId, listenPort, tracker).start();
    }
}