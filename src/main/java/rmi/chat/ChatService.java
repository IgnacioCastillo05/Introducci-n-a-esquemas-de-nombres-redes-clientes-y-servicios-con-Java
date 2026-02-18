package rmi.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota del chat.
 * Define el método que puede ser llamado remotamente.
 */
public interface ChatService extends Remote {
    void receiveMessage(String from, String message) throws RemoteException;
}
