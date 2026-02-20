package urls;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Ejercicio 2 - Sección 3.2
 * Mini-browser que pide una URL al usuario, lee su contenido
 * y lo guarda en un archivo llamado resultado.html
 */
public class MiniBrowser {

    private static final String OUTPUT_FILE = "resultado.html";

    public static void main(String[] args) {

        System.out.print("Ingresa la URL a consultar: ");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        String rawUrl;
        try {
            rawUrl = consoleReader.readLine().trim();
        } catch (IOException e) {
            System.err.println("Error leyendo la entrada: " + e.getMessage());
            return;
        }

        URL url;
        try {
            url = new URL(rawUrl);
        } catch (MalformedURLException e) {
            System.err.println("URL mal formada: " + e.getMessage());
            return;
        }

        System.out.println("Conectando a: " + rawUrl);

        try (BufferedReader webReader = new BufferedReader(new InputStreamReader(url.openStream()));
             BufferedWriter fileWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {

            String line;
            int lineCount = 0;

            while ((line = webReader.readLine()) != null) {
                fileWriter.write(line);
                fileWriter.newLine();
                lineCount++;
            }

            System.out.println("Listo. Se guardaron " + lineCount + " líneas en: " + OUTPUT_FILE);
            System.out.println("Abre el archivo en tu navegador para ver el resultado.");

        } catch (IOException e) {
            System.err.println("Error al leer o guardar el contenido: " + e.getMessage());
        }
    }

    /**
     * Obtiene el contenido de una URL como String.
     * Usado por HttpServer para servir páginas externas.
     */
    public static String fetchUrl(String urlString) throws IOException, MalformedURLException {
        URL url = new URL(urlString);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
