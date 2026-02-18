package rmi.chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementación del servicio de chat.
 * Recibe mensajes remotos y los muestra en consola.
 */
public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    private final String myName;

    public ChatServiceImpl(String myName) throws RemoteException {
        super();
        this.myName = myName;
    }

    @Override
    public void receiveMessage(String from, String message) throws RemoteException {
        System.out.println("\n[" + from + "]: " + message);
        System.out.print("> ");
    }
}
