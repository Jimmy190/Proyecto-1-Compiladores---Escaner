package codegen;

import semantic.*;
import java.util.*;

/**
 * Generador de código Assembly NASM (x86).
 * Traduce el código ABS a Assembly NASM.
 */
public class CodeGenerator {
    private StringBuilder code;
    private StringBuilder dataSection;
    private StringBuilder bssSection;
    private StringBuilder textSection;
    private SymbolTable symbolTable;
    private SemanticStack semanticStack;
    private int labelCounter;
    private Map<String, String> variableAddresses;
    
    /**
     * Constructor
     */
    public CodeGenerator(SymbolTable symbolTable) {
        this.code = new StringBuilder();
        this.dataSection = new StringBuilder();
        this.bssSection = new StringBuilder();
        this.textSection = new StringBuilder();
        this.symbolTable = symbolTable;
        this.semanticStack = new SemanticStack();
        this.labelCounter = 0;
        this.variableAddresses = new HashMap<>();
        
        initializeSections();
    }
    
    public StringBuilder mainBuffer = new StringBuilder();

    public void emitToMain(String code) {
        mainBuffer.append(code).append("\n");
    }

    public void flushMainBuffer() {
        textSection.append(mainBuffer.toString());
        mainBuffer.setLength(0); 
    }

    /**
     * Inicializa las secciones del programa
     */
    private void initializeSections() {
        // Sección de datos (constantes)
        dataSection.append("; ============================================\n");
        dataSection.append("; SECCION DE DATOS\n");
        dataSection.append("; ============================================\n");
        dataSection.append("section .data\n");
        dataSection.append("    newline db 10, 0        ; Salto de linea\n");
        dataSection.append("    format_int db \"%d\", 10, 0  ; Formato para printf\n");
        dataSection.append("    format_str db \"%s\", 10, 0  ; Formato para printf string\n");
        
        // Sección BSS (variables no inicializadas)
        bssSection.append("\n; ============================================\n");
        bssSection.append("; SECCION BSS (Variables)\n");
        bssSection.append("; ============================================\n");
        bssSection.append("section .bss\n");
        
        // Sección de código
        textSection.append("\n; ============================================\n");
        textSection.append("; SECCION DE CODIGO\n");
        textSection.append("; ============================================\n");
        textSection.append("section .text\n");
        textSection.append("    global main\n");
        textSection.append("    extern printf\n");
        textSection.append("    extern scanf\n\n");
    }
    
    /**
     * Declara variables globales en la sección BSS
     */
    public void declareGlobalVariables() {
        Map<String, Symbol> globalSymbols = symbolTable.getGlobalScope().getAllSymbols();
        
        bssSection.append("\n; Variables globales\n");
        
        for (Symbol symbol : globalSymbols.values()) {
            if (symbol.getSymbolType() == Symbol.SymbolType.VARIABLE) {
                String varName = symbol.getName().toLowerCase();
                
                switch (symbol.getDataType()) {
                    case INT:
                        bssSection.append("    ").append(varName).append(" resd 1    ; int (4 bytes)\n");
                        variableAddresses.put(varName, varName);
                        break;
                    case REAL:
                        bssSection.append("    ").append(varName).append(" resq 1    ; float (8 bytes)\n");
                        variableAddresses.put(varName, varName);
                        break;
                    case CHAR:
                        bssSection.append("    ").append(varName).append(" resb 1    ; char (1 byte)\n");
                        variableAddresses.put(varName, varName);
                        break;
                    case STRING:
                        bssSection.append("    ").append(varName).append(" resb 256  ; string (256 bytes)\n");
                        variableAddresses.put(varName, varName);
                        break;
                }
            }
        }
    }
    
    /**
     * Inicia la generación del main
     */
    public void beginMain() {
        textSection.append("main:\n");
        textSection.append("    push ebp\n");
        textSection.append("    mov ebp, esp\n\n");
    }
    
    /**
     * Finaliza la generación del main
     */
    public void endMain() {
        textSection.append("\n; Salir del programa\n");
        textSection.append("    mov esp, ebp\n");
        textSection.append("    pop ebp\n");
        textSection.append("    mov eax, 0\n");
        textSection.append("    ret\n");
    }
    
    /**
     * Genera código para una asignación
     */
    public void generateAssignment(String varName, String exprType) {
        String value = semanticStack.pop();
        String var = varName.toLowerCase();
        
        emitToMain("\n; Asignacion: " + varName + " := " + value);

        // Cargar valor en eax
        if (value.startsWith("[") || variableAddresses.containsKey(value)) {
            emitToMain("    mov eax, " + value);
        } else if (value.matches("-?\\d+")) {
            emitToMain("    mov eax, " + value);
        } else {
            emitToMain("    mov eax, " + value);
        }

        emitToMain("    mov [" + var + "], eax");
    }

    /**
     * Genera código para cargar una variable en la pila semántica
     */
    public void loadVariable(String varName) {
        String var = varName.toLowerCase();
        
        textSection.append("\n; Cargar variable: ").append(varName).append("\n");
        textSection.append("    mov eax, [").append(var).append("]\n");
        
        semanticStack.push("eax");
    }
    
    /**
     * Genera código para cargar un literal entero en la pila semántica
     */
    public void loadIntLiteral(String value) {
        // emitToMain("\n; Cargar literal: " + value);
        // emitToMain("    mov eax, " + value);
        semanticStack.push(value);
    }

    
    /**
     * Genera código para una suma
     */
    public void generateAddition() {
        String right = semanticStack.pop();
        String left = semanticStack.pop();

        emitToMain("\n; Suma");

        // cargar left en eax:
        if (left.matches("-?\\d+")) {
            emitToMain("    mov eax, " + left);
        } else {
            emitToMain("    mov eax, [" + left + "]");
        }

        // sumar right
        if (right.matches("-?\\d+")) {
            emitToMain("    add eax, " + right);
        } else {
            emitToMain("    add eax, [" + right + "]");
        }

        // Resultado siempre queda en eax
        semanticStack.push("eax");
    }


    
    /**
     * Genera código para una resta
     */
    public void generateSubtraction() {
        String right = semanticStack.pop();  // Operando derecho
        String left = semanticStack.pop();   // Operando izquierdo

        emitToMain("\n; Resta");

        // === 1. Cargar operando izquierdo en eax ===
        if (left.equals("eax")) {
            // Ya está en eax -> no hacer nada
        } else if (left.matches("-?\\d+")) {
            emitToMain("    mov eax, " + left);  // Literal
        } else {
            emitToMain("    mov eax, [" + left + "]");  // Variable
        }

        // === 2. Restar operando derecho ===
        if (right.equals("eax")) {
            emitToMain("    sub eax, eax");   // Caso raro: X - X
        } else if (right.matches("-?\\d+")) {
            emitToMain("    sub eax, " + right); // Literal
        } else if (right.equals("ebx")) {
            emitToMain("    sub eax, ebx");   // Registro ebx
        } else {
            emitToMain("    sub eax, [" + right + "]"); // Variable
        }

        // Resultado queda en eax
        semanticStack.push("eax");
    }

    /**
     * Genera código para incremento (++)
     */
    public void generateIncrement(String varName) {
        String var = varName.toLowerCase();
        System.out.println("hola desde generateIncrement con varName: " + varName);
        emitToMain("\n; Incremento: " + varName + "++");
        emitToMain("    inc dword [" + var + "]");
    }
    
    /**
     * Genera código para decremento (--)
     */
    public void generateDecrement(String varName) {
        String var = varName.toLowerCase();
        
        emitToMain("\n; Decremento: " + varName + "--");
        emitToMain("    dec dword [" + var + "]");
    }
    
    /**
     * Genera código para comparación de igualdad
     */
    public void generateEqual() {
        String right = semanticStack.pop();
        String left = semanticStack.pop();
        
        textSection.append("\n; Comparacion igual (==)\n");
        
        // Cargar operando izquierdo
        if (left.equals("eax")) {
            // left ya está en eax
        } else if (left.matches("-?\\d+")) {
            textSection.append("    mov eax, ").append(left).append("\n");
        } else {
            textSection.append("    mov eax, [").append(left).append("]\n");
        }
        
        // Comparar
        if (right.equals("ebx")) {
            textSection.append("    cmp eax, ebx\n");
        } else if (right.matches("-?\\d+")) {
            textSection.append("    cmp eax, ").append(right).append("\n");
        } else if (right.equals("eax")) {
            textSection.append("    cmp eax, eax\n");
        } else {
            textSection.append("    cmp eax, [").append(right).append("]\n");
        }
        
        // Resultado en eax (1 si igual, 0 si no)
        textSection.append("    sete al\n");
        textSection.append("    movzx eax, al\n");
        
        semanticStack.push("eax");
    }
    
    /**
     * Inicia un IF
     */
    public void beginIf() {
        String condition = semanticStack.pop();
        String elseLabel = getNewLabel("else");
        String endLabel = getNewLabel("endif");
        
        textSection.append("\n; IF\n");
        
        // Verificar condición
        if (condition.equals("eax")) {
            textSection.append("    test eax, eax\n");
        } else {
            textSection.append("    cmp ").append(condition).append(", 0\n");
        }
        
        textSection.append("    je ").append(elseLabel).append("\n\n");
        
        // Guardar labels
        semanticStack.pushLabel(elseLabel);
        semanticStack.pushLabel(endLabel);
    }
    
    /**
     * Genera la parte ELSE del IF
     */
    public void generateElse() {
        String endLabel = semanticStack.peekLabel();
        String elseLabel = semanticStack.popLabel(1);
        
        textSection.append("    jmp ").append(endLabel).append("\n\n");
        textSection.append(elseLabel).append(":\n");
        
        semanticStack.pushLabel(elseLabel);
    }
    
    /**
     * Finaliza el IF
     */
    public void endIf() {
        String endLabel = semanticStack.popLabel();
        textSection.append("\n").append(endLabel).append(":\n");
    }
    
    /**
     * Finaliza el IF sin ELSE
     */
    public void endIfNoElse() {
        String endLabel = semanticStack.popLabel();
        String elseLabel = semanticStack.popLabel();
        
        textSection.append("\n").append(elseLabel).append(":\n");
        textSection.append(endLabel).append(":\n");
    }
    
    /**
     * Genera código para WRITE de una variable
     */
    public void generateWrite(String varName) {
        String var = varName.toLowerCase();

        emitToMain("\n; WRITE(" + varName + ")");
        emitToMain("    push dword [" + var + "]");
        emitToMain("    push format_int");
        emitToMain("    call printf");
        emitToMain("    add esp, 8");
    }

    public void generateWriteList(ArrayList<String> args) {

        for (String value : args) {

            emitToMain("\n; WRITE(" + value + ")");

            if (value.startsWith("[") || variableAddresses.containsKey(value)) {
                // variable
                emitToMain("    push dword " + value);
            } 
            else if (value.matches("-?\\d+")) {
                // literal entero
                emitToMain("    push dword " + value);
            } 
            else {
                // registro o temporal (ej: eax)
                emitToMain("    push dword " + value);
            }

            emitToMain("    push format_int");
            emitToMain("    call printf");
            emitToMain("    add esp, 8");
        }
    }

    /**
     * Genera código para WRITE de una expresión
     */
    public void generateWriteExpression() {
        String value = semanticStack.pop();
        
        textSection.append("\n; WRITE(expresion)\n");
        
        if (value.equals("eax")) {
            textSection.append("    push eax\n");
        } else if (value.matches("-?\\d+")) {
            textSection.append("    push dword ").append(value).append("\n");
        } else {
            textSection.append("    push dword [").append(value).append("]\n");
        }
        
        textSection.append("    push format_int\n");
        textSection.append("    call printf\n");
        textSection.append("    add esp, 8\n");
    }
    
    /**
     * Obtiene el código completo generado
     */
    public String getCode() {
        code.setLength(0);
        code.append("; ============================================\n");
        code.append("; Codigo generado por el Compilador ABS\n");
        code.append("; Arquitectura: x86 (32-bit)\n");
        code.append("; Ensamblador: NASM\n");
        code.append("; ============================================\n\n");
        code.append(dataSection);
        code.append(bssSection);
        code.append(textSection);
        return code.toString();
    }
    
    /**
     * Obtiene una nueva etiqueta
     */
    private String getNewLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }
    
    /**
     * Obtiene la pila semántica
     */
    public SemanticStack getSemanticStack() {
        return semanticStack;
    }
    
    /**
     * Reinicia el generador
     */
    public void reset() {
        code.setLength(0);
        dataSection.setLength(0);
        bssSection.setLength(0);
        textSection.setLength(0);
        semanticStack.clear();
        labelCounter = 0;
        variableAddresses.clear();
        
        initializeSections();
    }
}