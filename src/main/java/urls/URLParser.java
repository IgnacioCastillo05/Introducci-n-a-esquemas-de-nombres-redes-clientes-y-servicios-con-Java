package urls;

import java.net.URL;
import java.net.MalformedURLException;

public class URLParser {
    public static void main(String[] args) throws MalformedURLException {
        URL myurl = new URL("http://ldbn.is.escuelaing.edu.co:8080/respuestasexamenarep.txt?val=9&t=8&r=6#publications");

        System.out.println("Protocol: " + myurl.getProtocol());
        System.out.println("Authority: " + myurl.getAuthority());
        System.out.println("Host: " + myurl.getHost());
        System.out.println("Port: " + myurl.getPort());
        System.out.println("Path: " + myurl.getPath());
        System.out.println("Query: " + myurl.getQuery()); 
        System.out.println("File: " + myurl.getFile()); 
        System.out.println("Ref: " + myurl.getRef()); 
    }
}
