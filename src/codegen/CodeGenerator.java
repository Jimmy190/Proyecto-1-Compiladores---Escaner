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
    
    private int stringLiteralCounter = 0;

    /**
     * Registra un literal string en la sección .data con un label único.
     */
    public String createStringLiteral(String literal) {

        String clean = literal.substring(1, literal.length() - 1);

        String label = "_strlit_" + stringLiteralCounter++;

        dataSection.append("    " + label + " db \"" + clean + "\", 0\n");

        return label;
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
     * Genera código para una asignación (compatible con INT y STRING)
     */
    public void generateAssignment(String varName, String exprType) {

        String value = semanticStack.pop();
        String var = varName.toLowerCase();

        emitToMain("\n; Asignacion: " + varName + " := " + value);

        switch (exprType.toUpperCase()) {

            /* ============================
            *          ENTERO
            * ============================ */
            case "INT": {

                if (value.matches("-?\\d+")) {
                    emitToMain("    mov eax, " + value);               // literal
                }
                else if (variableAddresses.containsKey(value)) {
                    emitToMain("    mov eax, [" + value + "]");       // variable int
                }
                else {
                    emitToMain("    mov eax, " + value);              // registro
                }

                emitToMain("    mov [" + var + "], eax");
                break;
            }

            /* ============================
            *          STRING
            * ============================ */
            case "STRING": {

                String label;

                if (value.startsWith("\"") && value.endsWith("\"")) {

                    // Crear label en .data
                    label = createStringLiteral(value);

                    // Guardar puntero
                    emitToMain("    mov dword [" + var + "], " + label);
                }
                else if (variableAddresses.containsKey(value)) {

                    // Copiar puntero desde otra variable string
                    emitToMain("    mov eax, [" + value + "]");
                    emitToMain("    mov [" + var + "], eax");
                }
                else {

                    // Copiar temporal o registro
                    emitToMain("    mov [" + var + "], " + value);
                }

                break;
            }

            default:
                emitToMain("; ERROR: tipo no soportado en asignación");
                break;
        }
    }


    /**
     * Genera código para cargar una variable en la pila semántica
     */
    public void loadVariable(String varName) {
        String var = varName.toLowerCase(); 
        semanticStack.push("[" + var + "]");
    }
    
    /**
     * Genera código para cargar un literal entero en la pila semántica
     */
    public void loadIntLiteral(String value) {
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
        String left  = semanticStack.pop();

        emitToMain("\n; Comparacion igual (==)");

        // Detectar si ya traen corchetes (por ejemplo: "[x]")
        boolean leftHasBrackets  = left.startsWith("[") && left.endsWith("]");
        boolean rightHasBrackets = right.startsWith("[") && right.endsWith("]");

        // --- Cargar operando izquierdo ---
        if (left.equals("eax")) {
            
        } 
        else if (left.matches("-?\\d+")) {
            emitToMain("    mov eax, " + left);
        } 
        else if (leftHasBrackets) {
            emitToMain("    mov eax, " + left);      // ya viene "[x]"
        } 
        else {
            emitToMain("    mov eax, [" + left + "]"); // variable normal
        }

        // --- Comparar con operando derecho ---
        if (right.equals("ebx")) {
            emitToMain("    cmp eax, ebx");
        } 
        else if (right.matches("-?\\d+")) {
            emitToMain("    cmp eax, " + right);
        } 
        else if (right.equals("eax")) {
            emitToMain("    cmp eax, eax");
        } 
        else if (rightHasBrackets) {
            emitToMain("    cmp eax, " + right);      // ya viene "[z]"
        } 
        else {
            emitToMain("    cmp eax, [" + right + "]"); // variable normal
        }

        // --- Resultado booleano ---
        emitToMain("    sete al");
        emitToMain("    movzx eax, al");

        semanticStack.push("eax");
    }

    /**
     * Inicia un IF
     */
    public void beginIf() {
        String condition = semanticStack.pop();

        String elseLabel = getNewLabel("else");
        String endLabel  = getNewLabel("endif");

        emitToMain("\n; --- IF ---");

        // Si la condición está en eax (por ejemplo resultado de generateEqual)
        if (condition.equals("eax")) {
            emitToMain("    test eax, eax");
        } else {
            emitToMain("    cmp " + condition + ", 0");
        }

        // Si condición es falsa → saltar al ELSE
        emitToMain("    je " + elseLabel);

        // Guardar en pila semántica
        semanticStack.pushLabel(elseLabel);
        semanticStack.pushLabel(endLabel);
    }

    /**
     * Genera la parte ELSE del IF
     */
    public void generateElse() {
        emitToMain("\n; --- ELSE ---");

        String endLabel  = semanticStack.popLabel(); // arriba
        String elseLabel = semanticStack.popLabel(); // abajo

        // Saltar al final del IF
        emitToMain("    jmp " + endLabel);

        // Colocar etiqueta ELSE
        emitToMain(elseLabel + ":");

        // Reinsertar endLabel para terminar al final
        semanticStack.pushLabel(endLabel);
    }

    
    /**
     * Finaliza el IF
     */
    public void endIf() {
        emitToMain("\n; --- END IF/ELSE ---");

        String endLabel = semanticStack.popLabel();
        emitToMain(endLabel + ":");
    }

    
    /**
     * Finaliza el IF sin ELSE
     */
    public void endIfNoElse() {
        emitToMain("\n; --- END IF (no ELSE) ---");

        String endLabel  = semanticStack.popLabel();
        String elseLabel = semanticStack.popLabel();

        // ELSE vacío
        emitToMain(elseLabel + ":");
        emitToMain(endLabel + ":");
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

    public void generateWriteList(int count, List<String> argumentTypesStr) {

        // 1. Recuperar los valores desde la pila semántica
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            temp.add(semanticStack.pop());
        }
        Collections.reverse(temp);

        // 2. Procesar cada elemento a imprimir
        for (int i = 0; i < count; i++) {

            String value = temp.get(i);
            String type  = argumentTypesStr.get(i);

            emitToMain("\n; WRITE(" + value + ")");

            switch (type.toLowerCase()) {

                /* ====================================================
                ===============       ENTERO        =================
                ==================================================== */
                case "int":

                    if (value.matches("-?\\d+")) {
                        emitToMain("    push dword " + value);
                    }
                    else if (variableAddresses.containsKey(value)) {
                        emitToMain("    push dword [" + value + "]");
                    }
                    else {
                        emitToMain("    push dword " + value);
                    }

                    emitToMain("    push dword format_int");
                    emitToMain("    call printf");
                    emitToMain("    add esp, 8");
                    break;


                /* ====================================================
                ===============       STRING        =================
                ==================================================== */
                case "string":

                    // Caso 1: variable string -> push dirección de la variable
                    if (variableAddresses.containsKey(value)) {
                        emitToMain("    push dword [" + value + "]");
                    }

                    // Caso 2: literal string -> crear label con createStringLiteral()
                    else if (value.startsWith("\"") && value.endsWith("\"")) {

                        // SOLO AQUÍ LLAMAMOS TU FUNCIÓN EXACTA
                        String label = createStringLiteral(value);

                        emitToMain("    push dword " + label);
                    }

                    // Caso 3: cualquier otro valor (raro, pero lo dejamos seguro)
                    else {
                        emitToMain("    push dword " + value);
                    }

                    emitToMain("    push dword format_str");
                    emitToMain("    call printf");
                    emitToMain("    add esp, 8");
                    break;


                default:
                    emitToMain("; ERROR: tipo no soportado en WRITE: " + type);
                    break;
            }
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