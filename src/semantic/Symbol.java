package semantic;

/**
 * Clase que representa un símbolo en la tabla de símbolos.
 * Un símbolo puede ser una variable, función o procedimiento.
 */
public class Symbol {
    private String name;           // Nombre del símbolo
    private SymbolType symbolType; // Tipo de símbolo (VARIABLE, FUNCTION, PROCEDURE)
    private DataType dataType;     // Tipo de dato (INT, REAL, CHAR, STRING)
    private String scope;          // Ámbito (GLOBAL, nombre de función/procedimiento)
    private int line;              // Línea donde se declaró
    private int column;            // Columna donde se declaró
    private Object value;          // Valor (para constant folding)
    private boolean isConstant;    // Si es una constante evaluada
    
    /**
     * Tipos de símbolos en el lenguaje ABS
     */
    public enum SymbolType {
        VARIABLE,
        FUNCTION,
        PROCEDURE,
        PARAMETER
    }
    
    /**
     * Tipos de datos soportados en ABS
     */
    public enum DataType {
        INT,
        REAL,
        CHAR,
        STRING,
        VOID,      // Para procedures
        UNKNOWN    // Para errores o tipos no definidos
    }
    
    /**
     * Constructor principal para símbolos
     */
    public Symbol(String name, SymbolType symbolType, DataType dataType, String scope, int line, int column) {
        this.name = name;
        this.symbolType = symbolType;
        this.dataType = dataType;
        this.scope = scope;
        this.line = line;
        this.column = column;
        this.isConstant = false;
        this.value = null;
    }
    
    /**
     * Constructor simplificado para variables globales
     */
    public Symbol(String name, DataType dataType, int line, int column) {
        this(name, SymbolType.VARIABLE, dataType, "GLOBAL", line, column);
    }
    
    // ========== GETTERS ==========
    
    public String getName() {
        return name;
    }
    
    public SymbolType getSymbolType() {
        return symbolType;
    }
    
    public DataType getDataType() {
        return dataType;
    }
    
    public String getScope() {
        return scope;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public Object getValue() {
        return value;
    }
    
    public boolean isConstant() {
        return isConstant;
    }
    
    // ========== SETTERS ==========
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Convierte un string a DataType
     */
    public static DataType stringToDataType(String type) {
        if (type == null) return DataType.UNKNOWN;
        
        switch (type.toUpperCase()) {
            case "INT":
                return DataType.INT;
            case "REAL":
                return DataType.REAL;
            case "CHAR":
                return DataType.CHAR;
            case "STRING":
                return DataType.STRING;
            case "VOID":
                return DataType.VOID;
            default:
                return DataType.UNKNOWN;
        }
    }
    
    /**
     * Convierte DataType a string legible
     */
    public static String dataTypeToString(DataType type) {
        if (type == null) return "UNKNOWN";
        return type.name();
    }
    
    /**
     * Verifica si dos tipos son compatibles para asignación
     */
    public static boolean areTypesCompatible(DataType type1, DataType type2) {
        if (type1 == type2) {
            return true;
        }
        
        // INT puede asignarse a REAL (promoción implícita)
        if (type1 == DataType.REAL && type2 == DataType.INT) {
            return true;
        }
        
        // CHAR puede asignarse a STRING
        if (type1 == DataType.STRING && type2 == DataType.CHAR) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Representación en string del símbolo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s", name));
        sb.append(String.format("%-15s", dataTypeToString(dataType)));
        sb.append(String.format("%-15s", scope));
        sb.append(String.format("Línea: %d", line));
        
        if (symbolType == SymbolType.FUNCTION || symbolType == SymbolType.PROCEDURE) {
            sb.append(String.format(" [%s]", symbolType.name()));
        }
        
        return sb.toString();
    }
    
    /**
     * Representación detallada para debugging
     */
    public String toDetailedString() {
        return String.format(
            "Symbol{name='%s', symbolType=%s, dataType=%s, scope='%s', line=%d, column=%d, isConstant=%s, value=%s}",
            name, symbolType, dataType, scope, line, column, isConstant, value
        );
    }
}
