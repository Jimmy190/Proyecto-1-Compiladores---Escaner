import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Main <ruta del archivo .abs>");
            return;
        }

        String rutaArchivo = args[0];

        try {
            // Abrir el archivo .abs
            Reader reader = new FileReader(rutaArchivo);

            // Crear el scanner pasando el Reader
            ScannerABS scanner = new ScannerABS(reader);

            String token;
            while ((token = scanner.yylex()) != null) {
                System.out.println("TOKEN -> " + token);
            }

            System.out.println("\n=== ERRORES DETECTADOS ===");
            for (String e : scanner.getErrores()) {
                System.out.println(e);
            }

            System.out.println("\n=== TOKENS ACEPTADOS ===");
            scanner.imprimirTokens();

            reader.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error del scanner: " + e.getMessage());
        }
    }
}
