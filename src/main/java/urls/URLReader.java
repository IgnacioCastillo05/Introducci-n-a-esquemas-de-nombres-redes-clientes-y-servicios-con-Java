package urls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class URLReader { 

    public static void main(String[] args) throws Exception { 
        URL google = new URL("http://www.google.com/"); 
        
        // Imprimir información de la URL
        System.out.println("Protocol: " + google.getProtocol());
        System.out.println("Authority: " + google.getAuthority());
        System.out.println("Host: " + google.getHost());
        System.out.println("Port: " + google.getPort());
        System.out.println("Path: " + google.getPath());
        System.out.println("Query: " + google.getQuery());
        System.out.println("File: " + google.getFile());
        System.out.println("Ref: " + google.getRef());
        System.out.println("contenido: ");
        
        try (BufferedReader reader 
                = new BufferedReader(new InputStreamReader(google.openStream()))) { 
            String inputLine = null; 
            while ((inputLine = reader.readLine()) != null) { 
                System.out.println(inputLine); 
            } 
        } catch (IOException x) { 
            System.err.println(x); 
        } 
    } 
} 