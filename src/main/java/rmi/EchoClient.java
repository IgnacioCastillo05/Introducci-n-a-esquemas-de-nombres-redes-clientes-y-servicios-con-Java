package rmi;

import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 

/** 
* 
* @author dnielben 
*/ 
public class EchoClient { 
    public void ejecutaServicio(String ipRmiregistry, int puertoRmiRegistry, 
                                            String nombreServicio) { 
        if (System.getSecurityManager() == null) { 
            System.setSecurityManager(new SecurityManager()); 
        } 
        try { 
            Registry registry = LocateRegistry.getRegistry(ipRmiregistry, puertoRmiRegistry); 
            EchoServer echoServer = (EchoServer) registry.lookup(nombreServicio); 
            System.out.println(echoServer.echo("Hola como estas?")); 
        } catch (Exception e) { 
            System.err.println("Hay un problema:"); 
            e.printStackTrace(); 
        } 
    } 
    public static void main(String[] args){ 
        EchoClient ec = new EchoClient(); 
        ec.ejecutaServicio("127.0.0.1", 23000, "echoServer"); 
    } 
    
}
