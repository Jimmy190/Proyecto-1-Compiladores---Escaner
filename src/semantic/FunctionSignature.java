package semantic;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la firma de una función o procedimiento.
 * Almacena información sobre parámetros y tipo de retorno.
 */
public class FunctionSignature {
    private String name;                           // Nombre de la función/procedimiento
    private Symbol.DataType returnType;            // Tipo de retorno (VOID para procedures)
    private List<Parameter> parameters;            // Lista de parámetros
    private int declarationLine;                   // Línea donde se declaró
    private boolean isProcedure;                   // true si es procedimiento, false si es función
    
    /**
     * Clase interna que representa un parámetro
     */
    public static class Parameter {
        private String name;
        private Symbol.DataType type;
        private int position;  // Posición del parámetro (0-based)
        
        public Parameter(String name, Symbol.DataType type, int position) {
            this.name = name;
            this.type = type;
            this.position = position;
        }
        
        public String getName() {
            return name;
        }
        
        public Symbol.DataType getType() {
            return type;
        }
        
        public int getPosition() {
            return position;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s", name, Symbol.dataTypeToString(type));
        }
    }
    
    /**
     * Constructor para función
     */
    public FunctionSignature(String name, Symbol.DataType returnType, int declarationLine) {
        this.name = name;
        this.returnType = returnType;
        this.declarationLine = declarationLine;
        this.parameters = new ArrayList<>();
        this.isProcedure = (returnType == Symbol.DataType.VOID);
    }
    
    /**
     * Constructor para procedimiento (sin tipo de retorno)
     */
    public FunctionSignature(String name, int declarationLine) {
        this(name, Symbol.DataType.VOID, declarationLine);
        this.isProcedure = true;
    }
    
    // ========== GESTIÓN DE PARÁMETROS ==========
    
    /**
     * Agrega un parámetro a la firma
     */
    public void addParameter(String name, Symbol.DataType type) {
        int position = parameters.size();
        parameters.add(new Parameter(name, type, position));
    }
    
    /**
     * Agrega un parámetro a la firma
     */
    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }
    
    /**
     * Obtiene un parámetro por posición
     */
    public Parameter getParameter(int index) {
        if (index >= 0 && index < parameters.size()) {
            return parameters.get(index);
        }
        return null;
    }
    
    /**
     * Obtiene un parámetro por nombre
     */
    public Parameter getParameterByName(String paramName) {
        for (Parameter param : parameters) {
            if (param.getName().equalsIgnoreCase(paramName)) {
                return param;
            }
        }
        return null;
    }
    
    /**
     * Obtiene todos los parámetros
     */
    public List<Parameter> getParameters() {
        return new ArrayList<>(parameters); // Retorna copia
    }
    
    /**
     * Obtiene la cantidad de parámetros
     */
    public int getParameterCount() {
        return parameters.size();
    }
    
    /**
     * Verifica si tiene parámetros
     */
    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
    
    // ========== VERIFICACIÓN DE TIPOS ==========
    
    /**
     * Verifica si la cantidad de argumentos coincide con la cantidad de parámetros
     */
    public boolean matchesParameterCount(int argumentCount) {
        return parameters.size() == argumentCount;
    }
    
    /**
     * Verifica si un argumento en una posición es compatible con el parámetro esperado
     */
    public boolean isArgumentTypeValid(int position, Symbol.DataType argumentType) {
        if (position >= parameters.size()) {
            return false;
        }
        
        Parameter param = parameters.get(position);
        return Symbol.areTypesCompatible(param.getType(), argumentType);
    }
    
    /**
     * Verifica si una lista de tipos de argumentos es válida
     */
    public boolean areArgumentTypesValid(List<Symbol.DataType> argumentTypes) {
        if (argumentTypes.size() != parameters.size()) {
            return false;
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            if (!isArgumentTypeValid(i, argumentTypes.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Obtiene el primer error de tipo en los argumentos, o null si todos son válidos
     */
    public String getArgumentTypeError(List<Symbol.DataType> argumentTypes) {
        if (argumentTypes.size() != parameters.size()) {
            return String.format("Se esperaban %d argumentos pero se recibieron %d", 
                               parameters.size(), argumentTypes.size());
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            Symbol.DataType argType = argumentTypes.get(i);
            
            if (!Symbol.areTypesCompatible(param.getType(), argType)) {
                return String.format("Argumento %d: se esperaba tipo '%s' pero se recibió '%s'",
                                   i + 1,
                                   Symbol.dataTypeToString(param.getType()),
                                   Symbol.dataTypeToString(argType));
            }
        }
        
        return null; // Todos los tipos son válidos
    }
    
    // ========== GETTERS ==========
    
    public String getName() {
        return name;
    }
    
    public Symbol.DataType getReturnType() {
        return returnType;
    }
    
    public int getDeclarationLine() {
        return declarationLine;
    }
    
    public boolean isProcedure() {
        return isProcedure;
    }
    
    public boolean isFunction() {
        return !isProcedure;
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Obtiene la firma completa como string (ej: "suma(INT, INT): INT")
     */
    public String getSignatureString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(Symbol.dataTypeToString(parameters.get(i).getType()));
        }
        
        sb.append(")");
        
        if (!isProcedure) {
            sb.append(": ");
            sb.append(Symbol.dataTypeToString(returnType));
        }
        
        return sb.toString();
    }
    
    /**
     * Obtiene la firma con nombres de parámetros (ej: "suma(a: INT, b: INT): INT")
     */
    public String getDetailedSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameters.get(i).toString());
        }
        
        sb.append(")");
        
        if (!isProcedure) {
            sb.append(": ");
            sb.append(Symbol.dataTypeToString(returnType));
        }
        
        return sb.toString();
    }
    
    /**
     * Representación en string
     */
    @Override
    public String toString() {
        return getSignatureString();
    }
    
    /**
     * Representación detallada para debugging
     */
    public String toDetailedString() {
        return String.format(
            "FunctionSignature{name='%s', returnType=%s, parameters=%d, isProcedure=%s, line=%d}",
            name, returnType, parameters.size(), isProcedure, declarationLine
        );
    }
    
    /**
     * Compara si dos firmas son equivalentes (mismo nombre y tipos de parámetros)
     */
    public boolean isEquivalent(FunctionSignature other) {
        if (!this.name.equalsIgnoreCase(other.name)) {
            return false;
        }
        
        if (this.parameters.size() != other.parameters.size()) {
            return false;
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            if (this.parameters.get(i).getType() != other.parameters.get(i).getType()) {
                return false;
            }
        }
        
        return this.returnType == other.returnType;
    }
}
