package semantic;

import java.util.*;

/**
 * Tabla de Símbolos principal para el análisis semántico.
 * Maneja la jerarquía de ámbitos y la gestión de símbolos del programa.
 */
public class SymbolTable {
    private Scope globalScope;                              // Ámbito global
    private Scope currentScope;                             // Ámbito actual
    private Map<String, FunctionSignature> functions;       // Registro de funciones/procedimientos
    private List<SemanticError> errors;                     // Lista de errores semánticos
    private Stack<Scope> scopeStack;                        // Pila de ámbitos para navegación
    
    /**
     * Constructor de la tabla de símbolos
     */
    public SymbolTable() {
        this.globalScope = new Scope("GLOBAL", Scope.ScopeType.GLOBAL);
        this.currentScope = globalScope;
        this.functions = new HashMap<>();
        this.errors = new ArrayList<>();
        this.scopeStack = new Stack<>();
        scopeStack.push(globalScope);
    }
    
    // ========== GESTIÓN DE ÁMBITOS ==========
    
    /**
     * Entra en un nuevo ámbito (función, procedimiento o bloque)
     */
    public void enterScope(String name, Scope.ScopeType type) {
        Scope newScope = new Scope(name, type, currentScope);
        currentScope = newScope;
        scopeStack.push(newScope);
    }
    
    /**
     * Sale del ámbito actual y retorna al padre
     */
    public void exitScope() {
        if (currentScope != globalScope) {
            scopeStack.pop();
            currentScope = currentScope.getParent();
        }
    }
    
    /**
     * Obtiene el ámbito actual
     */
    public Scope getCurrentScope() {
        return currentScope;
    }
    
    /**
     * Obtiene el ámbito global
     */
    public Scope getGlobalScope() {
        return globalScope;
    }
    
    /**
     * Verifica si estamos en el ámbito global
     */
    public boolean isInGlobalScope() {
        return currentScope == globalScope;
    }
    
    /**
     * Obtiene el nombre del ámbito actual
     */
    public String getCurrentScopeName() {
        return currentScope.getName();
    }
    
    // ========== GESTIÓN DE VARIABLES ==========
    
    /**
     * Declara una nueva variable en el ámbito actual
     * @return true si se declaró exitosamente, false si ya existía
     */
    public boolean declareVariable(String name, Symbol.DataType type, int line, int column) {
        // Verificar si ya existe en el ámbito actual
        if (currentScope.existsInCurrentScope(name)) {
            Symbol existing = currentScope.findSymbolLocal(name);
            errors.add(SemanticError.duplicateVariable(name, line, column, existing.getLine()));
            return false;
        }
        
        // Crear y agregar el símbolo
        Symbol symbol = new Symbol(
            name,
            Symbol.SymbolType.VARIABLE,
            type,
            currentScope.getName(),
            line,
            column
        );
        
        return currentScope.addSymbol(symbol);
    }
    
    /**
     * Busca una variable en el ámbito actual o en ámbitos padres
     */
    public Symbol findVariable(String name) {
        return currentScope.findSymbol(name);
    }
    
    /**
     * Verifica si una variable está declarada
     */
    public boolean isVariableDeclared(String name) {
        return currentScope.exists(name);
    }
    
    /**
     * Verifica el uso de una variable y reporta error si no está declarada
     */
    public Symbol checkVariableUsage(String name, int line, int column) {
        Symbol symbol = findVariable(name);
        
        if (symbol == null) {
            errors.add(SemanticError.undefinedVariable(name, line, column));
            return null;
        }
        
        return symbol;
    }
    
    // ========== GESTIÓN DE FUNCIONES/PROCEDIMIENTOS ==========
    
    /**
     * Declara una nueva función
     */
    public boolean declareFunction(String name, Symbol.DataType returnType, int line, int column) {
        String key = name.toLowerCase();
        
        // Verificar si ya existe
        if (functions.containsKey(key)) {
            FunctionSignature existing = functions.get(key);
            errors.add(SemanticError.duplicateFunction(name, line, column, existing.getDeclarationLine()));
            return false;
        }
        
        // Crear firma de función
        FunctionSignature signature = new FunctionSignature(name, returnType, line);
        functions.put(key, signature);
        
        // Agregar a la tabla de símbolos global
        Symbol symbol = new Symbol(
            name,
            Symbol.SymbolType.FUNCTION,
            returnType,
            "GLOBAL",
            line,
            column
        );
        globalScope.addSymbol(symbol);
        
        return true;
    }
    
    /**
     * Declara un nuevo procedimiento
     */
    public boolean declareProcedure(String name, int line, int column) {
        String key = name.toLowerCase();
        
        // Verificar si ya existe
        if (functions.containsKey(key)) {
            FunctionSignature existing = functions.get(key);
            errors.add(SemanticError.duplicateFunction(name, line, column, existing.getDeclarationLine()));
            return false;
        }
        
        // Crear firma de procedimiento
        FunctionSignature signature = new FunctionSignature(name, line);
        functions.put(key, signature);
        
        // Agregar a la tabla de símbolos global
        Symbol symbol = new Symbol(
            name,
            Symbol.SymbolType.PROCEDURE,
            Symbol.DataType.VOID,
            "GLOBAL",
            line,
            column
        );
        globalScope.addSymbol(symbol);
        
        return true;
    }
    
    /**
     * Agrega un parámetro a la función/procedimiento actual
     */
    public void addParameter(String functionName, String paramName, Symbol.DataType paramType) {
        String key = functionName.toLowerCase();
        FunctionSignature signature = functions.get(key);
        
        if (signature != null) {
            signature.addParameter(paramName, paramType);
        }
    }
    
    /**
     * Obtiene la firma de una función/procedimiento
     */
    public FunctionSignature getFunctionSignature(String name) {
        return functions.get(name.toLowerCase());
    }
    
    /**
     * Verifica si una función/procedimiento está declarada
     */
    public boolean isFunctionDeclared(String name) {
        return functions.containsKey(name.toLowerCase());
    }
    
    /**
     * Verifica una llamada a función y sus parámetros
     */
    public boolean checkFunctionCall(String name, List<Symbol.DataType> argumentTypes, int line, int column) {
        FunctionSignature signature = getFunctionSignature(name);
        
        if (signature == null) {
            errors.add(SemanticError.undefinedFunction(name, line, column));
            return false;
        }
        
        // Verificar cantidad de parámetros
        if (!signature.matchesParameterCount(argumentTypes.size())) {
            errors.add(SemanticError.wrongParameterCount(
                name,
                signature.getParameterCount(),
                argumentTypes.size(),
                line,
                column
            ));
            return false;
        }
        
        // Verificar tipos de parámetros
        for (int i = 0; i < argumentTypes.size(); i++) {
            if (!signature.isArgumentTypeValid(i, argumentTypes.get(i))) {
                FunctionSignature.Parameter param = signature.getParameter(i);
                errors.add(SemanticError.wrongParameterType(
                    name,
                    i + 1,
                    param.getType(),
                    argumentTypes.get(i),
                    line,
                    column
                ));
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Obtiene todas las funciones declaradas
     */
    public Map<String, FunctionSignature> getAllFunctions() {
        return new HashMap<>(functions);
    }
    
    // ========== GESTIÓN DE ERRORES ==========
    
    /**
     * Agrega un error semántico
     */
    public void addError(SemanticError error) {
        errors.add(error);
    }
    
    /**
     * Obtiene todos los errores semánticos
     */
    public List<SemanticError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Verifica si hay errores
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Obtiene la cantidad de errores
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Limpia todos los errores
     */
    public void clearErrors() {
        errors.clear();
    }
    
    /**
     * Verifica el tipo de retorno de una función
     */
    public void checkReturnType(String functionName, String returnType, int line, int column) {
        FunctionSignature signature = functions.get(functionName.toLowerCase());
        
        if (signature == null) {
            errors.add(SemanticError.undefinedFunction(functionName, line, column));
            return;
        }
        
        Symbol.DataType expected = signature.getReturnType();
        Symbol.DataType actual = Symbol.stringToDataType(returnType);
        
        if (!Symbol.areTypesCompatible(expected, actual)) {
            errors.add(SemanticError.typeMismatch(expected, actual, line, column));
        }
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Limpia toda la tabla de símbolos
     */
    public void clear() {
        globalScope.clear();
        currentScope = globalScope;
        functions.clear();
        errors.clear();
        scopeStack.clear();
        scopeStack.push(globalScope);
    }
    
    /**
     * Obtiene estadísticas de la tabla
     */
    public String getStatistics() {
        int totalSymbols = globalScope.getTotalSymbolCount();
        int totalFunctions = functions.size();
        int totalErrors = errors.size();
        
        return String.format(
            "Simbolos: %d | Funciones/Procedimientos: %d | Errores: %d",
            totalSymbols, totalFunctions, totalErrors
        );
    }
    
    /**
     * Imprime el contenido de la tabla de símbolos
     */
    public void print() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    TABLA DE SIMBOLOS");
        System.out.println("=".repeat(80));
        
        // Imprimir árbol de ámbitos
        System.out.println("\n--- JERARQUIA DE AMBITOS ---");
        globalScope.printTree(0);
        
        // Imprimir funciones/procedimientos
        System.out.println("\n--- FUNCIONES Y PROCEDIMIENTOS ---");
        if (functions.isEmpty()) {
            System.out.println("  (ninguna función declarada)");
        } else {
            for (FunctionSignature sig : functions.values()) {
                System.out.println("  " + sig.getDetailedSignature());
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
    }
    
    /**
     * Imprime solo las variables globales y funciones (formato simplificado)
     */
    public void printSummary() {
        System.out.println("\nTABLA DE SIMBOLOS - Contenido completo");
        System.out.println("-".repeat(100));
        System.out.printf("%-25s %-15s %-20s %-15s %-10s\n", "NOMBRE", "TIPO", "AMBITO", "CATEGORIA", "LINEA");
        System.out.println("-".repeat(100));
        
        // 1. Variables globales
        Map<String, Symbol> globalSymbols = globalScope.getAllSymbols();
        for (Symbol symbol : globalSymbols.values()) {
            if (symbol.getSymbolType() == Symbol.SymbolType.VARIABLE) {
                System.out.printf("%-25s %-15s %-20s %-15s %-10d\n",
                    symbol.getName(),
                    Symbol.dataTypeToString(symbol.getDataType()),
                    "GLOBAL",
                    "Variable",
                    symbol.getLine()
                );
            }
        }
        
        // 2. Funciones y procedimientos con sus parámetros y variables locales
        if (!functions.isEmpty()) {
            for (FunctionSignature sig : functions.values()) {
                // Mostrar la función/procedimiento
                String tipo = sig.isProcedure() ? "PROCEDURE" : "FUNCTION";
                String tipoCompleto = sig.isProcedure() ? 
                    "VOID" : 
                    Symbol.dataTypeToString(sig.getReturnType());
                
                System.out.printf("%-25s %-15s %-20s %-15s %-10s\n",
                    sig.getName(),
                    tipoCompleto,
                    sig.getName(),
                    tipo,
                    "-"
                );
                
                // Mostrar parámetros de la función/procedimiento
                for (FunctionSignature.Parameter param : sig.getParameters()) {
                    System.out.printf("%-25s %-15s %-20s %-15s %-10s\n",
                        "  " + param.getName(),
                        Symbol.dataTypeToString(param.getType()),
                        sig.getName(),
                        "Parametro",
                        "-"
                    );
                }
                
                // Mostrar variables locales de la función/procedimiento
                Scope funcScope = findScopeByName(sig.getName());
                if (funcScope != null) {
                    Map<String, Symbol> localSymbols = funcScope.getAllSymbols();
                    for (Symbol symbol : localSymbols.values()) {
                        if (symbol.getSymbolType() == Symbol.SymbolType.VARIABLE) {
                            // No mostrar parámetros de nuevo (ya se mostraron arriba)
                            boolean esParametro = false;
                            for (FunctionSignature.Parameter param : sig.getParameters()) {
                                if (param.getName().equalsIgnoreCase(symbol.getName())) {
                                    esParametro = true;
                                    break;
                                }
                            }
                            
                            if (!esParametro) {
                                System.out.printf("%-25s %-15s %-20s %-15s %-10d\n",
                                    "  " + symbol.getName(),
                                    Symbol.dataTypeToString(symbol.getDataType()),
                                    sig.getName(),
                                    "Variable local",
                                    symbol.getLine()
                                );
                            }
                        }
                    }
                }
            }
        }
        
        System.out.println("-".repeat(100));
        
        // Resumen estadístico
        int totalVariablesGlobales = 0;
        int totalFunciones = 0;
        int totalProcedimientos = 0;
        int totalParametros = 0;
        int totalVariablesLocales = 0;
        
        for (Symbol symbol : globalSymbols.values()) {
            if (symbol.getSymbolType() == Symbol.SymbolType.VARIABLE) {
                totalVariablesGlobales++;
            }
        }
        
        for (FunctionSignature sig : functions.values()) {
            if (sig.isProcedure()) {
                totalProcedimientos++;
            } else {
                totalFunciones++;
            }
            totalParametros += sig.getParameters().size();
            
            Scope funcScope = findScopeByName(sig.getName());
            if (funcScope != null) {
                Map<String, Symbol> localSymbols = funcScope.getAllSymbols();
                for (Symbol symbol : localSymbols.values()) {
                    if (symbol.getSymbolType() == Symbol.SymbolType.VARIABLE) {
                        // No contar parámetros como variables locales
                        boolean esParametro = false;
                        for (FunctionSignature.Parameter param : sig.getParameters()) {
                            if (param.getName().equalsIgnoreCase(symbol.getName())) {
                                esParametro = true;
                                break;
                            }
                        }
                        if (!esParametro) {
                            totalVariablesLocales++;
                        }
                    }
                }
            }
        }
        
        System.out.println("\nRESUMEN:");
        System.out.println("  Variables globales:     " + totalVariablesGlobales);
        System.out.println("  Funciones:              " + totalFunciones);
        System.out.println("  Procedimientos:         " + totalProcedimientos);
        System.out.println("  Parámetros:             " + totalParametros);
        System.out.println("  Variables locales:      " + totalVariablesLocales);
        System.out.println("  TOTAL DE SÍMBOLOS:      " + 
            (totalVariablesGlobales + totalFunciones + totalProcedimientos + totalParametros + totalVariablesLocales));
    }
    
    /**
     * Busca un scope por nombre en el árbol de scopes
     */
    private Scope findScopeByName(String name) {
        if (globalScope.getName().equalsIgnoreCase(name)) {
            return globalScope;
        }
        return findScopeByNameRecursive(globalScope, name);
    }
    
    /**
     * Búsqueda recursiva de scope por nombre
     */
    private Scope findScopeByNameRecursive(Scope scope, String name) {
        for (Scope child : scope.getChildren()) {
            if (child.getName().equalsIgnoreCase(name)) {
                return child;
            }
            Scope found = findScopeByNameRecursive(child, name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
    /**
     * Representación en string
     */
    @Override
    public String toString() {
        return String.format("SymbolTable{scope='%s', symbols=%d, functions=%d, errors=%d}",
            currentScope.getName(),
            currentScope.getSymbolCount(),
            functions.size(),
            errors.size()
        );
    }
}
