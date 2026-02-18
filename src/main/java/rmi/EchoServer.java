package rmi;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

public  interface EchoServer extends Remote { 
    public String echo(String cadena) throws RemoteException; 
}

