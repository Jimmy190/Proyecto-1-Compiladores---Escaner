package semantic;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador Semántico principal.
 * Coordina todas las verificaciones semánticas del programa.
 */
public class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private TypeChecker typeChecker;
    private String currentFunction;  // Función/procedimiento actual siendo analizada
    private boolean analysisComplete;
    
    /**
     * Constructor
     */
    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.typeChecker = new TypeChecker(symbolTable);
        this.currentFunction = null;
        this.analysisComplete = false;
    }
    
    // ========== GESTIÓN DE PROGRAMA ==========
    
    /**
     * Inicia el análisis de un programa
     */
    public void beginProgram(String programName) {
        symbolTable.clear();
        currentFunction = null;
        analysisComplete = false;
    }
    
    /**
     * Finaliza el análisis del programa
     */
    public void endProgram() {
        analysisComplete = true;
    }
    
    // ========== GESTIÓN DE VARIABLES ==========
    
    /**
     * Declara una variable global o local
     */
    public boolean declareVariable(String name, String typeStr, int line, int column) {
        Symbol.DataType type = Symbol.stringToDataType(typeStr);
        return symbolTable.declareVariable(name, type, line, column);
    }
    
    /**
     * Verifica el uso de una variable
     */
    public Symbol checkVariableUsage(String name, int line, int column) {
        return symbolTable.checkVariableUsage(name, line, column);
    }
    
    /**
     * Verifica una asignación
     */
    public boolean checkAssignment(String varName, Symbol.DataType exprType, int line, int column) {
        // Caso especial: si estamos en una función y el nombre coincide con la función actual,
        // es una sentencia de retorno, no una asignación
        if (currentFunction != null && varName.equalsIgnoreCase(currentFunction)) {
            // Es un retorno implícito (estilo Pascal: nombreFuncion := valor)
            String exprTypeStr = Symbol.dataTypeToString(exprType);
            checkReturnStatement(currentFunction, exprTypeStr, line, column);
            return true;
        }
        
        return typeChecker.checkAssignment(varName, exprType, line, column);
    }
    
    /**
     * Verifica una asignación (versión con tipos como String)
     */
    public boolean checkAssignment(String varName, String exprTypeStr, int line, int column) {
        // Caso especial: si estamos en una función y el nombre coincide con la función actual,
        // es una sentencia de retorno, no una asignación
        if (currentFunction != null && varName.equalsIgnoreCase(currentFunction)) {
            // Es un retorno implícito (estilo Pascal: nombreFuncion := valor)
            checkReturnStatement(currentFunction, exprTypeStr, line, column);
            return true;
        }
        
        Symbol.DataType exprType = Symbol.stringToDataType(exprTypeStr);
        return typeChecker.checkAssignment(varName, exprType, line, column);
    }
    
    // ========== GESTIÓN DE FUNCIONES/PROCEDIMIENTOS ==========
    
    /**
     * Declara una función
     */
    public boolean declareFunction(String name, String returnTypeStr, int line, int column) {
        Symbol.DataType returnType = Symbol.stringToDataType(returnTypeStr);
        boolean success = symbolTable.declareFunction(name, returnType, line, column);
        
        if (success) {
            currentFunction = name;
            symbolTable.enterScope(name, Scope.ScopeType.FUNCTION);
        }
        
        return success;
    }
    
    /**
     * Declara un procedimiento
     */
    public boolean declareProcedure(String name, int line, int column) {
        boolean success = symbolTable.declareProcedure(name, line, column);
        
        if (success) {
            currentFunction = name;
            symbolTable.enterScope(name, Scope.ScopeType.PROCEDURE);
        }
        
        return success;
    }
    
    /**
     * Agrega un parámetro a la función/procedimiento actual
     */
    public void addParameter(String paramName, String typeStr, int line, int column) {
        if (currentFunction == null) {
            return;
        }
        
        Symbol.DataType type = Symbol.stringToDataType(typeStr);
        
        // Agregar a la firma de la función
        symbolTable.addParameter(currentFunction, paramName, type);
        
        // Declarar como variable local
        symbolTable.declareVariable(paramName, type, line, column);
    }
    
    /**
     * Finaliza la declaración de una función/procedimiento
     */
    public void endFunctionDeclaration() {
        symbolTable.exitScope();
        currentFunction = null;
    }
    
    /**
     * Verifica una llamada a función (versión con List<Symbol.DataType>)
     */
    public boolean checkFunctionCall(String name, List<Symbol.DataType> argumentTypes, int line, int column) {
        return symbolTable.checkFunctionCall(name, argumentTypes, line, column);
    }
    
    /**
     * Verifica una llamada a función y devuelve el tipo de retorno (versión con tipos String)
     */
    public String checkFunctionCallWithStringTypes(String name, List<String> argumentTypesStr, int line, int column) {
        // Convertir tipos String a DataType
        List<Symbol.DataType> argumentTypes = new java.util.ArrayList<>();
        for (String typeStr : argumentTypesStr) {
            argumentTypes.add(Symbol.stringToDataType(typeStr));
        }
        
        // Verificar la llamada
        boolean valid = symbolTable.checkFunctionCall(name, argumentTypes, line, column);
        
        if (!valid) {
            return "UNKNOWN";
        }
        
        // Obtener tipo de retorno de la función
        FunctionSignature signature = symbolTable.getFunctionSignature(name);
        if (signature == null) {
            return "UNKNOWN";
        }
        
        return Symbol.dataTypeToString(signature.getReturnType());
    }
    
    /**
     * Obtiene el tipo de retorno de una función
     */
    public Symbol.DataType getFunctionReturnType(String name) {
        FunctionSignature signature = symbolTable.getFunctionSignature(name);
        if (signature != null) {
            return signature.getReturnType();
        }
        return Symbol.DataType.UNKNOWN;
    }
    
    /**
     * Verifica el retorno de una función
     */
    public boolean checkFunctionReturn(String functionName, Symbol.DataType returnType, int line, int column) {
        return typeChecker.checkFunctionReturn(functionName, returnType, line, column);
    }
    
    // ========== VERIFICACIÓN DE TIPOS ==========
    
    /**
     * Verifica una operación aritmética binaria
     */
    public Symbol.DataType checkArithmeticOperation(Symbol.DataType left, Symbol.DataType right,
                                                    String operator, int line, int column) {
        return typeChecker.checkArithmeticOperation(left, right, operator, line, column);
    }
    
    /**
     * Verifica una operación relacional
     */
    public boolean checkRelationalOperation(Symbol.DataType left, Symbol.DataType right,
                                           String operator, int line, int column) {
        return typeChecker.checkRelationalOperation(left, right, operator, line, column);
    }
    
    /**
     * Verifica una operación lógica
     */
    public boolean checkLogicalOperation(Symbol.DataType left, Symbol.DataType right,
                                        String operator, int line, int column) {
        return typeChecker.checkLogicalOperation(left, right, operator, line, column);
    }
    
    /**
     * Verifica una operación unaria
     */
    public Symbol.DataType checkUnaryOperation(Symbol.DataType operand, String operator, int line, int column) {
        return typeChecker.checkUnaryOperation(operand, operator, line, column);
    }
    
    /**
     * Verifica incremento/decremento
     */
    public boolean checkIncrementDecrement(String varName, String operator, int line, int column) {
        return typeChecker.checkIncrementDecrement(varName, operator, line, column);
    }
    
    // ========== VERIFICACIÓN DE READ/WRITE ==========
    
    /**
     * Verifica operación READ
     */
    public boolean checkReadOperation(String varName, int line, int column) {
        // Primero verificar que la variable exista
        Symbol symbol = symbolTable.checkVariableUsage(varName, line, column);
        if (symbol == null) {
            return false;
        }
        return typeChecker.checkReadOperation(varName, line, column);
    }
    
    /**
     * Verifica operación WRITE
     */
    public boolean checkWriteOperation(Symbol.DataType exprType, int line, int column) {
        return typeChecker.checkWriteOperation(exprType, line, column);
    }
    
    // ========== CONSTANT FOLDING ==========
    
    /**
     * Intenta evaluar una expresión constante (suma)
     */
    public ConstantFolder.ConstantValue foldAddition(ConstantFolder.ConstantValue left,
                                                     ConstantFolder.ConstantValue right) {
        return ConstantFolder.foldAddition(left, right);
    }
    
    /**
     * Intenta evaluar una expresión constante (resta)
     */
    public ConstantFolder.ConstantValue foldSubtraction(ConstantFolder.ConstantValue left,
                                                        ConstantFolder.ConstantValue right) {
        return ConstantFolder.foldSubtraction(left, right);
    }
    
    /**
     * Intenta evaluar una expresión constante (multiplicación)
     */
    public ConstantFolder.ConstantValue foldMultiplication(ConstantFolder.ConstantValue left,
                                                           ConstantFolder.ConstantValue right) {
        return ConstantFolder.foldMultiplication(left, right);
    }
    
    /**
     * Intenta evaluar una expresión constante (división)
     */
    public ConstantFolder.ConstantValue foldDivision(ConstantFolder.ConstantValue left,
                                                     ConstantFolder.ConstantValue right) {
        return ConstantFolder.foldDivision(left, right);
    }
    
    /**
     * Crea un valor constante desde un literal
     */
    public ConstantFolder.ConstantValue createConstantFromLiteral(String literal, String literalType) {
        switch (literalType.toUpperCase()) {
            case "INT":
                return ConstantFolder.fromIntLiteral(literal);
            case "REAL":
                return ConstantFolder.fromRealLiteral(literal);
            case "OCTAL":
                return ConstantFolder.fromOctalLiteral(literal);
            case "HEX":
                return ConstantFolder.fromHexLiteral(literal);
            case "STRING":
                return ConstantFolder.fromStringLiteral(literal);
            case "CHAR":
                return ConstantFolder.fromCharLiteral(literal);
            default:
                return new ConstantFolder.ConstantValue();
        }
    }
    
    // ========== GESTIÓN DE ERRORES ==========
    
    /**
     * Obtiene todos los errores semánticos
     */
    public List<SemanticError> getErrors() {
        return symbolTable.getErrors();
    }
    
    /**
     * Verifica si hay errores
     */
    public boolean hasErrors() {
        return symbolTable.hasErrors();
    }
    
    /**
     * Obtiene la cantidad de errores
     */
    public int getErrorCount() {
        return symbolTable.getErrorCount();
    }
    
    /**
     * Agrega un error personalizado
     */
    public void addError(SemanticError error) {
        symbolTable.addError(error);
    }
    
    // ========== TABLA DE SÍMBOLOS ==========
    
    /**
     * Obtiene la tabla de símbolos
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
    
    /**
     * Imprime la tabla de símbolos
     */
    public void printSymbolTable() {
        symbolTable.print();
    }
    
    /**
     * Imprime resumen de la tabla de símbolos
     */
    public void printSymbolTableSummary() {
        symbolTable.printSummary();
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Verifica si el análisis está completo
     */
    public boolean isAnalysisComplete() {
        return analysisComplete;
    }
    
    /**
     * Obtiene el nombre de la función actual
     */
    public String getCurrentFunction() {
        return currentFunction;
    }
    
    /**
     * Verifica si estamos dentro de una función
     */
    public boolean isInFunction() {
        return currentFunction != null;
    }
    
    /**
     * Obtiene estadísticas del análisis
     */
    public String getStatistics() {
        return symbolTable.getStatistics();
    }
    
    /**
     * Reinicia el analizador
     */
    public void reset() {
        symbolTable.clear();
        typeChecker.clearErrors();
        currentFunction = null;
        analysisComplete = false;
    }
    
    // ========== MÉTODOS PARA VERIFICACIÓN DE TIPOS EN EXPRESIONES ==========
    
    /**
     * Verifica operación binaria y devuelve tipo resultante
     */
    public String checkBinaryOperation(String operator, String leftType, String rightType, int line, int column) {
        if (leftType == null || rightType == null) {
            return "UNKNOWN";
        }
        
        // Delegar al TypeChecker
        return typeChecker.checkBinaryOperation(operator, leftType, rightType, line, column);
    }
    
    /**
     * Verifica operación unaria y devuelve tipo resultante
     */
    public String checkUnaryOperation(String operator, String operandType, int line, int column) {
        if (operandType == null) {
            return "UNKNOWN";
        }
        
        return typeChecker.checkUnaryOperation(operator, operandType, line, column);
    }
    
    /**
     * Obtiene el tipo de una variable
     */
    public String getVariableType(String varName, int line, int column) {
        Symbol symbol = symbolTable.findVariable(varName);
        
        if (symbol == null) {
            symbolTable.addError(SemanticError.undefinedVariable(varName, line, column));
            return "UNKNOWN";
        }
        
        return Symbol.dataTypeToString(symbol.getDataType());
    }
    
    /**
     * Verifica operación relacional (comparación)
     */
    public void checkRelationalOperation(String operator, String leftType, String rightType, int line, int column) {
        if (leftType == null || rightType == null) {
            return;
        }
        
        Symbol.DataType left = Symbol.stringToDataType(leftType);
        Symbol.DataType right = Symbol.stringToDataType(rightType);
        
        typeChecker.checkRelationalOperation(left, right, operator, line, column);
    }
    
    /**
     * Verifica sentencia de retorno (nombre_funcion := expresion)
     */
    public void checkReturnStatement(String functionName, String returnType, int line, int column) {
        if (!isInFunction()) {
            symbolTable.addError(SemanticError.returnOutsideFunction(line, column));
            return;
        }
        
        // Verificar que el nombre coincida con la función actual
        if (!currentFunction.equals(functionName)) {
            symbolTable.addError(SemanticError.returnNameMismatch(
                functionName, currentFunction, line, column
            ));
        }
        
        // Verificar tipo de retorno
        symbolTable.checkReturnType(currentFunction, returnType, line, column);
    }
}
