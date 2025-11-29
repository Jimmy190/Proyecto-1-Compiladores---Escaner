package main;
import java_cup.runtime.*;
import lexer.Scanner;
import parser.Parser;
import semantic.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

            // Crear el scanner (lexer) y parser
            Scanner scanner = new Scanner(reader);
            Parser parser = new Parser(scanner);

            System.out.println("===================================================");
            System.out.println("           COMPILADOR ABS - ANALIZADOR");
            System.out.println("===================================================");
            System.out.println("Archivo: " + rutaArchivo);
            System.out.println();

            // Ejecutar el analisis sintactico con CUP
            System.out.println("EJECUTANDO ANALISIS SINTACTICO Y SEMANTICO...");
            System.out.println("(El analisis continuara a pesar de errores)");
            System.out.println("---------------------------------------------------");
            
            try {
                java_cup.runtime.Symbol resultado = parser.parse();
                System.out.println("ANALISIS SINTACTICO Y SEMANTICO COMPLETADOS");
            } catch (Exception e) {
                System.out.println("EL ANALISIS ENCONTRO ERRORES PERO CONTINUO");
            }

            // Obtener errores lexicos del scanner
            ArrayList<String> erroresLexicos = scanner.getErrores();
            
            // Obtener errores sintacticos del parser
            ArrayList<String> erroresSintacticos = parser.getErroresSintacticos();
            
            // Obtener errores semanticos del analizador semantico
            SemanticAnalyzer semanticAnalyzer = parser.getSemanticAnalyzer();
            List<SemanticError> erroresSemanticos = (semanticAnalyzer != null) ? 
                                                    semanticAnalyzer.getErrors() : 
                                                    new ArrayList<>();

            // ========== REPORTE DE ERRORES LEXICOS ==========
            System.out.println("\n" + "=".repeat(60));
            System.out.println("                ERRORES LEXICOS ENCONTRADOS");
            System.out.println("=".repeat(60));
            
            if (!erroresLexicos.isEmpty()) {
                System.out.println("Se encontraron " + erroresLexicos.size() + " error(es) lexico(s):");
                System.out.println("-".repeat(60));
                for (int i = 0; i < erroresLexicos.size(); i++) {
                    System.out.println((i + 1) + ". " + erroresLexicos.get(i));
                }
            } else {
                System.out.println("No se encontraron errores lexicos");
            }

            // ========== REPORTE DE ERRORES SINTACTICOS ==========
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              ERRORES SINTACTICOS ENCONTRADOS");
            System.out.println("=".repeat(60));
            
            if (!erroresSintacticos.isEmpty()) {
                System.out.println("Se encontraron " + erroresSintacticos.size() + " error(es) sintactico(s):");
                System.out.println("-".repeat(60));
                for (int i = 0; i < erroresSintacticos.size(); i++) {
                    System.out.println((i + 1) + ". " + erroresSintacticos.get(i));
                }
            } else {
                System.out.println("No se encontraron errores sintacticos");
            }

            // ========== REPORTE DE ERRORES SEMANTICOS ==========
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              ERRORES SEMANTICOS ENCONTRADOS");
            System.out.println("=".repeat(60));
            
            if (!erroresSemanticos.isEmpty()) {
                System.out.println("Se encontraron " + erroresSemanticos.size() + " error(es) semantico(s):");
                System.out.println("-".repeat(60));
                for (int i = 0; i < erroresSemanticos.size(); i++) {
                    System.out.println((i + 1) + ". " + erroresSemanticos.get(i).format());
                }
            } else {
                System.out.println("No se encontraron errores semanticos");
            }

            // ========== TABLA DE SIMBOLOS ==========
            if (semanticAnalyzer != null) {
                System.out.println("\n" + "=".repeat(100));
                System.out.println("                                    TABLA DE SÍMBOLOS");
                System.out.println("=".repeat(100));
                semanticAnalyzer.printSymbolTableSummary();
            }

            // ========== TABLA DE TOKENS ACEPTADOS ==========
            System.out.println("\n" + "=".repeat(60));
            System.out.println("                TOKENS ACEPTADOS");
            System.out.println("=".repeat(60));
            scanner.imprimirTokens();

            // ========== RESUMEN FINAL ==========
            System.out.println("\n" + "=".repeat(60));
            System.out.println("                     RESUMEN");
            System.out.println("=".repeat(60));
            
            int totalErrores = erroresLexicos.size() + erroresSintacticos.size() + erroresSemanticos.size();
            
            System.out.println("Total de errores lexicos: " + erroresLexicos.size());
            System.out.println("Total de errores sintacticos: " + erroresSintacticos.size());
            System.out.println("Total de errores semanticos: " + erroresSemanticos.size());
            System.out.println("Total de errores: " + totalErrores);
            
            if (totalErrores == 0) {
                System.out.println("\nANALISIS COMPLETADO EXITOSAMENTE!");
                System.out.println("El codigo fuente es lexica, sintactica y semanticamente correcto");
                System.exit(0);  // Exit code 0: exito
            } else {
                System.out.println("\nSE ENCONTRARON ERRORES - REVISE LOS REPORTES ANTERIORES");
                System.out.println("(El analisis completo el archivo a pesar de los errores)");
                System.exit(1);  // Exit code 1: errores encontrados
            }

            reader.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Archivo no encontrado: " + rutaArchivo);
            System.exit(2);  // Exit code 2: archivo no encontrado
        } catch (IOException e) {
            System.err.println("ERROR: Error leyendo el archivo: " + e.getMessage());
            System.exit(3);  // Exit code 3: error de I/O
        } catch (Exception e) {
            System.err.println("ERROR durante el analisis: " + e.getMessage());
            System.exit(4);  // Exit code 4: error general
        }
    }
}