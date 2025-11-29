package semantic;

import java.util.ArrayList;
import java.util.List;

/**
 * Verificador de tipos para el análisis semántico.
 * Realiza verificaciones de compatibilidad de tipos en operaciones y asignaciones.
 */
public class TypeChecker {
    private SymbolTable symbolTable;
    private List<SemanticError> errors;
    
    /**
     * Constructor
     */
    public TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.errors = new ArrayList<>();
    }
    
    // ========== VERIFICACIÓN DE ASIGNACIONES ==========
    
    /**
     * Verifica si una asignación es válida
     * @param varName Nombre de la variable
     * @param exprType Tipo de la expresión a asignar
     * @param line Línea de la asignación
     * @param column Columna de la asignación
     * @return true si la asignación es válida
     */
    public boolean checkAssignment(String varName, Symbol.DataType exprType, int line, int column) {
        Symbol symbol = symbolTable.findVariable(varName);
        
        if (symbol == null) {
            // Variable no declarada (ya reportado por SymbolTable)
            return false;
        }
        
        Symbol.DataType varType = symbol.getDataType();
        
        // Verificar compatibilidad de tipos
        if (!Symbol.areTypesCompatible(varType, exprType)) {
            SemanticError error = SemanticError.invalidAssignment(varName, varType, exprType, line, column);
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        return true;
    }
    
    // ========== VERIFICACIÓN DE OPERACIONES BINARIAS ==========
    
    /**
     * Verifica una operación binaria aritmética y retorna el tipo resultante
     * @param leftType Tipo del operando izquierdo
     * @param rightType Tipo del operando derecho
     * @param operator Operador (+, -, *, /, DIV, MOD)
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return Tipo resultante de la operación, o UNKNOWN si hay error
     */
    public Symbol.DataType checkArithmeticOperation(Symbol.DataType leftType, Symbol.DataType rightType, 
                                                    String operator, int line, int column) {
        // Verificar que ambos operandos sean numéricos
        if (!isNumericType(leftType)) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador '%s': el operando izquierdo debe ser de tipo numérico, se encontró '%s'", 
                            operator, Symbol.dataTypeToString(leftType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return Symbol.DataType.UNKNOWN;
        }
        
        if (!isNumericType(rightType)) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador '%s': el operando derecho debe ser de tipo numérico, se encontró '%s'", 
                            operator, Symbol.dataTypeToString(rightType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return Symbol.DataType.UNKNOWN;
        }
        
        // Determinar tipo resultante
        // Si alguno es REAL, el resultado es REAL
        if (leftType == Symbol.DataType.REAL || rightType == Symbol.DataType.REAL) {
            return Symbol.DataType.REAL;
        }
        
        // Si ambos son INT, el resultado es INT
        return Symbol.DataType.INT;
    }
    
    /**
     * Verifica una operación relacional (comparación)
     * @param leftType Tipo del operando izquierdo
     * @param rightType Tipo del operando derecho
     * @param operator Operador (=, <>, <, >, <=, >=)
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return true si la comparación es válida
     */
    public boolean checkRelationalOperation(Symbol.DataType leftType, Symbol.DataType rightType,
                                           String operator, int line, int column) {
        // Para igualdad y desigualdad, los tipos deben ser compatibles
        if (operator.equals("=") || operator.equals("<>")) {
            if (leftType == rightType || Symbol.areTypesCompatible(leftType, rightType)) {
                return true;
            }
        }
        
        // Para comparaciones de orden (<, >, <=, >=), ambos deben ser numéricos
        if (operator.equals("<") || operator.equals(">") || 
            operator.equals("<=") || operator.equals(">=")) {
            
            if (isNumericType(leftType) && isNumericType(rightType)) {
                return true;
            }
            
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador '%s' requiere operandos numéricos, se encontró '%s' y '%s'", 
                            operator, Symbol.dataTypeToString(leftType), Symbol.dataTypeToString(rightType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        // Tipos incompatibles
        SemanticError error = new SemanticError(
            SemanticError.ErrorType.INVALID_OPERATION,
            String.format("No se puede comparar '%s' con '%s' usando el operador '%s'", 
                        Symbol.dataTypeToString(leftType), Symbol.dataTypeToString(rightType), operator),
            line,
            column
        );
        errors.add(error);
        symbolTable.addError(error);
        return false;
    }
    
    /**
     * Verifica una operación lógica (AND, OR, NOT)
     * @param leftType Tipo del operando izquierdo (null para NOT unario)
     * @param rightType Tipo del operando derecho
     * @param operator Operador (AND, OR, NOT)
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return true si la operación es válida
     */
    public boolean checkLogicalOperation(Symbol.DataType leftType, Symbol.DataType rightType,
                                        String operator, int line, int column) {
        // Para NOT unario, solo verificar el operando derecho
        if (operator.equalsIgnoreCase("NOT")) {
            // En Pascal, NOT puede aplicarse a booleanos o enteros (bitwise)
            if (rightType == Symbol.DataType.INT) {
                return true; // Operación bitwise válida
            }
            
            // Para otros tipos, reportar error
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador NOT requiere un operando entero, se encontró '%s'", 
                            Symbol.dataTypeToString(rightType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        // AND y OR - en Pascal pueden ser lógicos o bitwise
        // Ambos operandos deben ser del mismo tipo compatible
        if (leftType != rightType) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador '%s' requiere operandos del mismo tipo, se encontró '%s' y '%s'", 
                            operator, Symbol.dataTypeToString(leftType), Symbol.dataTypeToString(rightType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        return true;
    }
    
    // ========== VERIFICACIÓN DE EXPRESIONES UNARIAS ==========
    
    /**
     * Verifica una operación unaria (negación, signo positivo)
     * @param operandType Tipo del operando
     * @param operator Operador (-, +)
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return Tipo resultante, o UNKNOWN si hay error
     */
    public Symbol.DataType checkUnaryOperation(Symbol.DataType operandType, String operator, 
                                               int line, int column) {
        if (!isNumericType(operandType)) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador unario '%s' requiere un operando numérico, se encontró '%s'", 
                            operator, Symbol.dataTypeToString(operandType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return Symbol.DataType.UNKNOWN;
        }
        
        return operandType; // El tipo se mantiene
    }
    
    /**
     * Verifica operaciones de incremento/decremento (++, --)
     * @param varName Nombre de la variable
     * @param operator Operador (++, --)
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return true si la operación es válida
     */
    public boolean checkIncrementDecrement(String varName, String operator, int line, int column) {
        Symbol symbol = symbolTable.findVariable(varName);
        
        if (symbol == null) {
            // Variable no declarada (ya reportado)
            return false;
        }
        
        Symbol.DataType varType = symbol.getDataType();
        
        // Solo INT y REAL pueden ser incrementados/decrementados
        if (!isNumericType(varType)) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.INVALID_OPERATION,
                String.format("Operador '%s' requiere una variable numérica, '%s' es de tipo '%s'", 
                            operator, varName, Symbol.dataTypeToString(varType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        return true;
    }
    
    // ========== VERIFICACIÓN DE CONDICIONES ==========
    
    /**
     * Verifica que una expresión sea válida como condición (IF, WHILE)
     * En Pascal, las condiciones son expresiones booleanas (comparaciones o expresiones lógicas)
     * @param conditionType Tipo de la expresión de condición
     * @param line Línea de la condición
     * @param column Columna de la condición
     * @return true si la condición es válida
     */
    public boolean checkCondition(Symbol.DataType conditionType, int line, int column) {
        // Las condiciones se evalúan mediante operaciones relacionales o lógicas
        // Por lo tanto, cualquier tipo es técnicamente válido si se usa en comparación
        // Esta validación es más relevante en el contexto del parser
        return true;
    }
    
    // ========== VERIFICACIÓN DE READ/WRITE ==========
    
    /**
     * Verifica que una variable pueda ser leída con READ
     * @param varName Nombre de la variable
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return true si la operación es válida
     */
    public boolean checkReadOperation(String varName, int line, int column) {
        Symbol symbol = symbolTable.findVariable(varName);
        
        if (symbol == null) {
            // Variable no declarada (ya reportado)
            return false;
        }
        
        // READ puede leer cualquier tipo de variable
        return true;
    }
    
    /**
     * Verifica que una expresión pueda ser escrita con WRITE
     * @param exprType Tipo de la expresión
     * @param line Línea de la operación
     * @param column Columna de la operación
     * @return true si la operación es válida
     */
    public boolean checkWriteOperation(Symbol.DataType exprType, int line, int column) {
        // WRITE puede escribir cualquier tipo
        return true;
    }
    
    // ========== VERIFICACIÓN DE FUNCIONES ==========
    
    /**
     * Verifica el tipo de retorno de una función
     * @param functionName Nombre de la función
     * @param returnType Tipo de la expresión de retorno
     * @param line Línea del return
     * @param column Columna del return
     * @return true si el tipo es compatible
     */
    public boolean checkFunctionReturn(String functionName, Symbol.DataType returnType, 
                                      int line, int column) {
        FunctionSignature signature = symbolTable.getFunctionSignature(functionName);
        
        if (signature == null) {
            return false; // Función no encontrada
        }
        
        Symbol.DataType expectedType = signature.getReturnType();
        
        // Verificar si es procedimiento
        if (signature.isProcedure()) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.PROCEDURE_WITH_RETURN,
                String.format("El procedimiento '%s' no debe tener valor de retorno", functionName),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        // Verificar compatibilidad de tipos
        if (!Symbol.areTypesCompatible(expectedType, returnType)) {
            SemanticError error = new SemanticError(
                SemanticError.ErrorType.RETURN_TYPE_MISMATCH,
                String.format("La función '%s' debe retornar tipo '%s', pero se encontró '%s'", 
                            functionName, Symbol.dataTypeToString(expectedType), Symbol.dataTypeToString(returnType)),
                line,
                column
            );
            errors.add(error);
            symbolTable.addError(error);
            return false;
        }
        
        return true;
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Verifica si un tipo es numérico (INT o REAL)
     */
    public static boolean isNumericType(Symbol.DataType type) {
        return type == Symbol.DataType.INT || type == Symbol.DataType.REAL;
    }
    
    /**
     * Verifica si un tipo es entero
     */
    public static boolean isIntegerType(Symbol.DataType type) {
        return type == Symbol.DataType.INT;
    }
    
    /**
     * Verifica si un tipo es real
     */
    public static boolean isRealType(Symbol.DataType type) {
        return type == Symbol.DataType.REAL;
    }
    
    /**
     * Verifica si un tipo es caracter o string
     */
    public static boolean isStringType(Symbol.DataType type) {
        return type == Symbol.DataType.STRING || type == Symbol.DataType.CHAR;
    }
    
    /**
     * Obtiene todos los errores
     */
    public List<SemanticError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Limpia los errores
     */
    public void clearErrors() {
        errors.clear();
    }
    
    /**
     * Verifica si hay errores
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    // ========== MÉTODOS ADICIONALES PARA EXPRESIONES ==========
    
    /**
     * Verifica operación binaria con tipos String y devuelve tipo resultante como String
     */
    public String checkBinaryOperation(String operator, String leftType, String rightType, int line, int column) {
        Symbol.DataType left = Symbol.stringToDataType(leftType);
        Symbol.DataType right = Symbol.stringToDataType(rightType);
        
        // Para operaciones aritméticas
        if (operator.equals("+") || operator.equals("-") || operator.equals("*") || 
            operator.equals("/") || operator.equals("DIV") || operator.equals("MOD")) {
            
            // Verificar que ambos sean numéricos
            if (!isNumericType(left) || !isNumericType(right)) {
                SemanticError error = new SemanticError(
                    SemanticError.ErrorType.TYPE_MISMATCH,
                    String.format("Operación '%s' requiere operandos numéricos, pero se encontró %s y %s", 
                                  operator, leftType, rightType),
                    line, column
                );
                errors.add(error);
                symbolTable.addError(error);
                return "UNKNOWN";
            }
            
            // Si uno es REAL, el resultado es REAL
            if (left == Symbol.DataType.REAL || right == Symbol.DataType.REAL) {
                return "REAL";
            }
            
            return "INT";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Verifica operación unaria y devuelve tipo resultante como String
     */
    public String checkUnaryOperation(String operator, String operandType, int line, int column) {
        Symbol.DataType type = Symbol.stringToDataType(operandType);
        
        if (operator.equals("+") || operator.equals("-")) {
            if (!isNumericType(type)) {
                SemanticError error = new SemanticError(
                    SemanticError.ErrorType.TYPE_MISMATCH,
                    String.format("Operación unaria '%s' requiere operando numérico, pero se encontró %s", 
                                  operator, operandType),
                    line, column
                );
                errors.add(error);
                symbolTable.addError(error);
                return "UNKNOWN";
            }
            
            return operandType;
        }
        
        return operandType;
    }
}
