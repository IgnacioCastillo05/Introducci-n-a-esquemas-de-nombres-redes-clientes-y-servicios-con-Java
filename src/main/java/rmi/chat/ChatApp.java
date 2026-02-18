package rmi.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Ejercicio 6.4.1 - Chat usando RMI
 *
 * Cómo ejecutar (2 terminales, desde la raíz del proyecto):
 *
 * Terminal 1 - Ignacio:
 * mvn exec:java -Dexec.mainClass="rmi.chat.ChatApp" -Dexec.args="Alice 23000 127.0.0.1 23001 Bob"
 *
 * Terminal 2 - Christian:
 * mvn exec:java -Dexec.mainClass="rmi.chat.ChatApp" -Dexec.args="Bob 23001 127.0.0.1 23000 Alice"
 */
public class ChatApp {

    public static void main(String[] args) throws Exception {

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        String myName, remoteHost, remoteName;
        int myPort, remotePort;

        if (args.length == 5) {
            myName     = args[0];
            myPort     = Integer.parseInt(args[1]);
            remoteHost = args[2];
            remotePort = Integer.parseInt(args[3]);
            remoteName = args[4];
        } else {
            System.out.print("Tu nombre: ");
            myName = console.readLine().trim();

            System.out.print("Puerto donde publicarás tu servicio: ");
            myPort = Integer.parseInt(console.readLine().trim());

            System.out.print("IP del otro usuario: ");
            remoteHost = console.readLine().trim();

            System.out.print("Puerto del registry del otro usuario: ");
            remotePort = Integer.parseInt(console.readLine().trim());

            System.out.print("Nombre del otro usuario: ");
            remoteName = console.readLine().trim();
        }

        ChatServiceImpl myService = new ChatServiceImpl(myName);
        Registry myRegistry = LocateRegistry.createRegistry(myPort);
        myRegistry.rebind(myName, myService);
        System.out.println("[" + myName + "] Servicio publicado en el puerto " + myPort);

        System.out.println("Esperando que " + remoteName + " esté listo...");
        System.out.println("(Asegúrate de que " + remoteName + " ya esté corriendo, luego presiona ENTER)");
        console.readLine();

        System.out.println("Conectando con " + remoteName + " en " + remoteHost + ":" + remotePort + "...");
        Registry remoteRegistry = LocateRegistry.getRegistry(remoteHost, remotePort);
        ChatService remoteService = (ChatService) remoteRegistry.lookup(remoteName);
        System.out.println("Conectado con " + remoteName + ". ¡Ya puedes chatear!\n");

        System.out.println("Escribe tus mensajes (escribe 'exit' para salir):");
        String line;
        while (true) {
            System.out.print("> ");
            line = console.readLine();
            if (line == null || line.equalsIgnoreCase("exit")) {
                System.out.println("Cerrando chat...");
                break;
            }
            remoteService.receiveMessage(myName, line);
        }
    }
}