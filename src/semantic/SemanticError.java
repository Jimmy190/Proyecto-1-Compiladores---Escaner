package semantic;

/**
 * Clase que representa un error semántico encontrado durante el análisis.
 * Almacena información detallada sobre el error para reportarlo al usuario.
 */
public class SemanticError {
    private ErrorType type;         // Tipo de error semántico
    private String message;         // Mensaje descriptivo del error
    private int line;               // Línea donde ocurrió el error
    private int column;             // Columna donde ocurrió el error
    private String context;         // Contexto adicional (nombre de variable, función, etc.)
    
    /**
     * Tipos de errores semánticos
     */
    public enum ErrorType {
        UNDEFINED_VARIABLE,          // Variable no definida
        DUPLICATE_VARIABLE,          // Variable ya definida
        UNDEFINED_FUNCTION,          // Función no definida
        DUPLICATE_FUNCTION,          // Función ya definida
        TYPE_MISMATCH,              // Tipos incompatibles
        WRONG_PARAMETER_COUNT,      // Cantidad incorrecta de parámetros
        WRONG_PARAMETER_TYPE,       // Tipo incorrecto de parámetro
        INVALID_ASSIGNMENT,         // Asignación inválida
        RETURN_TYPE_MISMATCH,       // Tipo de retorno incorrecto
        PROCEDURE_WITH_RETURN,      // Procedimiento con return
        FUNCTION_WITHOUT_RETURN,    // Función sin return
        RETURN_OUTSIDE_FUNCTION,    // Return fuera de función
        INVALID_RETURN,             // Return con nombre incorrecto
        INVALID_OPERATION,          // Operación inválida entre tipos
        SCOPE_ERROR,                // Error relacionado con ámbitos
        OTHER                       // Otros errores
    }
    
    /**
     * Constructor principal
     */
    public SemanticError(ErrorType type, String message, int line, int column, String context) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
        this.context = context;
    }
    
    /**
     * Constructor sin contexto
     */
    public SemanticError(ErrorType type, String message, int line, int column) {
        this(type, message, line, column, "");
    }
    
    /**
     * Constructor simplificado (sin columna)
     */
    public SemanticError(ErrorType type, String message, int line) {
        this(type, message, line, -1, "");
    }
    
    // ========== GETTERS ==========
    
    public ErrorType getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public String getContext() {
        return context;
    }
    
    // ========== MÉTODOS ESTÁTICOS DE CREACIÓN ==========
    
    /**
     * Crea un error de variable no definida
     */
    public static SemanticError undefinedVariable(String varName, int line, int column) {
        return new SemanticError(
            ErrorType.UNDEFINED_VARIABLE,
            String.format("Variable '%s' no ha sido declarada", varName),
            line,
            column,
            varName
        );
    }
    
    /**
     * Crea un error de variable duplicada
     */
    public static SemanticError duplicateVariable(String varName, int line, int column, int originalLine) {
        return new SemanticError(
            ErrorType.DUPLICATE_VARIABLE,
            String.format("Variable '%s' ya fue declarada anteriormente en la línea %d", varName, originalLine),
            line,
            column,
            varName
        );
    }
    
    /**
     * Crea un error de función no definida
     */
    public static SemanticError undefinedFunction(String funcName, int line, int column) {
        return new SemanticError(
            ErrorType.UNDEFINED_FUNCTION,
            String.format("Función o procedimiento '%s' no ha sido declarado", funcName),
            line,
            column,
            funcName
        );
    }
    
    /**
     * Crea un error de función duplicada
     */
    public static SemanticError duplicateFunction(String funcName, int line, int column, int originalLine) {
        return new SemanticError(
            ErrorType.DUPLICATE_FUNCTION,
            String.format("Función o procedimiento '%s' ya fue declarado en la línea %d", funcName, originalLine),
            line,
            column,
            funcName
        );
    }
    
    /**
     * Crea un error de incompatibilidad de tipos
     */
    public static SemanticError typeMismatch(Symbol.DataType expected, Symbol.DataType found, int line, int column) {
        return new SemanticError(
            ErrorType.TYPE_MISMATCH,
            String.format("Incompatibilidad de tipos: se esperaba '%s' pero se encontró '%s'", 
                        Symbol.dataTypeToString(expected), 
                        Symbol.dataTypeToString(found)),
            line,
            column
        );
    }
    
    /**
     * Crea un error de cantidad incorrecta de parámetros
     */
    public static SemanticError wrongParameterCount(String funcName, int expected, int found, int line, int column) {
        return new SemanticError(
            ErrorType.WRONG_PARAMETER_COUNT,
            String.format("La función '%s' espera %d parámetro(s) pero se proporcionaron %d", 
                        funcName, expected, found),
            line,
            column,
            funcName
        );
    }
    
    /**
     * Crea un error de tipo incorrecto de parámetro
     */
    public static SemanticError wrongParameterType(String funcName, int paramIndex, 
                                                   Symbol.DataType expected, Symbol.DataType found, 
                                                   int line, int column) {
        return new SemanticError(
            ErrorType.WRONG_PARAMETER_TYPE,
            String.format("Parámetro %d de la función '%s': se esperaba tipo '%s' pero se recibió '%s'", 
                        paramIndex, funcName, 
                        Symbol.dataTypeToString(expected), 
                        Symbol.dataTypeToString(found)),
            line,
            column,
            funcName
        );
    }
    
    /**
     * Crea un error de asignación inválida
     */
    public static SemanticError invalidAssignment(String varName, Symbol.DataType varType, 
                                                  Symbol.DataType exprType, int line, int column) {
        return new SemanticError(
            ErrorType.INVALID_ASSIGNMENT,
            String.format("No se puede asignar tipo '%s' a la variable '%s' de tipo '%s'", 
                        Symbol.dataTypeToString(exprType), varName, Symbol.dataTypeToString(varType)),
            line,
            column,
            varName
        );
    }
    
    // ========== MÉTODOS DE FORMATO ==========
    
    /**
     * Obtiene el nombre descriptivo del tipo de error
     */
    public String getErrorTypeName() {
        switch (type) {
            case UNDEFINED_VARIABLE:
                return "Variable no definida";
            case DUPLICATE_VARIABLE:
                return "Variable duplicada";
            case UNDEFINED_FUNCTION:
                return "Función no definida";
            case DUPLICATE_FUNCTION:
                return "Función duplicada";
            case TYPE_MISMATCH:
                return "Incompatibilidad de tipos";
            case WRONG_PARAMETER_COUNT:
                return "Cantidad incorrecta de parámetros";
            case WRONG_PARAMETER_TYPE:
                return "Tipo incorrecto de parámetro";
            case INVALID_ASSIGNMENT:
                return "Asignación inválida";
            case RETURN_TYPE_MISMATCH:
                return "Tipo de retorno incorrecto";
            case PROCEDURE_WITH_RETURN:
                return "Procedimiento con return";
            case FUNCTION_WITHOUT_RETURN:
                return "Función sin return";
            case INVALID_OPERATION:
                return "Operación inválida";
            case SCOPE_ERROR:
                return "Error de ámbito";
            default:
                return "Error semántico";
        }
    }
    
    /**
     * Formato para mostrar al usuario
     */
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("Línea ").append(line);
        
        if (column >= 0) {
            sb.append(", Columna ").append(column);
        }
        
        sb.append(": [").append(getErrorTypeName()).append("] ");
        sb.append(message);
        
        return sb.toString();
    }
    
    /**
     * Representación en string del error
     */
    @Override
    public String toString() {
        return format();
    }
    
    /**
     * Representación detallada para debugging
     */
    public String toDetailedString() {
        return String.format(
            "SemanticError{type=%s, message='%s', line=%d, column=%d, context='%s'}",
            type, message, line, column, context
        );
    }
    
    // ========== MÉTODOS ADICIONALES PARA ERRORES ESPECÍFICOS ==========
    
    /**
     * Error: Return fuera de función
     */
    public static SemanticError returnOutsideFunction(int line, int column) {
        return new SemanticError(
            ErrorType.RETURN_OUTSIDE_FUNCTION,
            "Sentencia de retorno fuera de una función",
            line,
            column
        );
    }
    
    /**
     * Error: Nombre de función en return no coincide
     */
    public static SemanticError returnNameMismatch(String returnName, String funcName, int line, int column) {
        return new SemanticError(
            ErrorType.INVALID_RETURN,
            String.format("El nombre en el retorno '%s' no coincide con la función actual '%s'", 
                         returnName, funcName),
            line,
            column
        );
    }
}
