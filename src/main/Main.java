package main;
import java_cup.runtime.*;
import lexer.Scanner;
import parser.Parser;
import semantic.*;
import codegen.*;

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
            System.out.println("\n" + "=".repeat(110));
            System.out.println("                                         TOKENS ACEPTADOS");
            System.out.println("=".repeat(110));
            scanner.imprimirTokens();

            // ========== RESUMEN FINAL ==========
            System.out.println("\n" + "=".repeat(100));
            System.out.println("                                    RESUMEN");
            System.out.println("=".repeat(100));
            
            int totalErrores = erroresLexicos.size() + erroresSintacticos.size() + erroresSemanticos.size();
            
            System.out.println("Total de errores lexicos: " + erroresLexicos.size());
            System.out.println("Total de errores sintacticos: " + erroresSintacticos.size());
            System.out.println("Total de errores semanticos: " + erroresSemanticos.size());
            System.out.println("Total de errores: " + totalErrores);
            
            // ========== GENERACION DE CODIGO ==========
            if (totalErrores == 0) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("         GENERACION DE CODIGO ASSEMBLY NASM");
                System.out.println("=".repeat(60));
                System.out.println("Iniciando traduccion a Assembly NASM...\n");
                CodeGenerator genCode = parser.getCodeGenerator(); 
                System.out.println(genCode.getCode());
                String asmCode = genCode.getCode();
                System.out.println("\nTraduccion a Assembly NASM completada.");
                // try {
                //     // Crear generador de código
                //     CodeGenerator codeGen = new CodeGenerator(semanticAnalyzer.getSymbolTable());
                    
                //     // NOTA: La generación de código debería hacerse durante el parsing
                //     // Para este ejemplo, mostramos la estructura básica
                    
                //     // Declarar variables globales
                //     // codeGen.declareGlobalVariables();
                    
                //     // Generar código del main
                //     codeGen.beginMain();
                    
                //     // AQUÍ SE GENERARÍA EL CÓDIGO DURANTE EL PARSING
                //     // Por ahora, mostramos un ejemplo básico
                    
                //     codeGen.endMain();
                    
                //     // Obtener código generado
                //     String asmCode = codeGen.getCode();
                    
                //     // Mostrar código generado
                //     System.out.println("CODIGO ASSEMBLY MIPS GENERADO:");
                //     System.out.println("=".repeat(60));
                //     System.out.println(asmCode);
                //     System.out.println("=".repeat(60));
                    
                //     // Guardar código en archivo .asm
                String asmFileName = rutaArchivo.replace(".abs", ".asm");
                try (PrintWriter writer = new PrintWriter(new FileWriter(asmFileName))) {
                    writer.println("; ============================================");
                    writer.println("; Codigo generado por el Compilador ABS");
                    writer.println("; Archivo fuente: " + rutaArchivo);
                    writer.println("; ============================================\n");
                    writer.println(asmCode);
                }
                    
                //     System.out.println("\nCodigo Assembly guardado en: " + asmFileName);
                    
                // } catch (Exception e) {
                //     System.err.println("ERROR durante la generacion de codigo: " + e.getMessage());
                //     e.printStackTrace();
                // }
                
                System.out.println("\n" + "=".repeat(60));
                System.out.println("COMPILACION COMPLETADA EXITOSAMENTE!");
                System.out.println("El codigo fuente es lexica, sintactica y semanticamente correcto");
                System.out.println("Codigo Assembly MIPS generado correctamente");
                System.out.println("=".repeat(60));
                System.exit(0);  // Exit code 0: exito
            } else {
                System.out.println("\nSE ENCONTRARON ERRORES - REVISE LOS REPORTES ANTERIORES");
                System.out.println("(El analisis completo el archivo a pesar de los errores)");
                System.out.println("\nNO SE GENERARA CODIGO ASSEMBLY debido a los errores encontrados");
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
            e.printStackTrace();
            System.exit(4);  // Exit code 4: error general
        }
    }
}