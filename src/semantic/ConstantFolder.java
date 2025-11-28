package semantic;

/**
 * Optimizador de expresiones constantes (Constant Folding).
 * Evalúa expresiones constantes en tiempo de compilación.
 */
public class ConstantFolder {
    
    /**
     * Clase interna para representar un valor constante
     */
    public static class ConstantValue {
        private Object value;
        private Symbol.DataType type;
        private boolean isConstant;
        
        public ConstantValue(Object value, Symbol.DataType type) {
            this.value = value;
            this.type = type;
            this.isConstant = true;
        }
        
        public ConstantValue() {
            this.value = null;
            this.type = Symbol.DataType.UNKNOWN;
            this.isConstant = false;
        }
        
        public Object getValue() {
            return value;
        }
        
        public Symbol.DataType getType() {
            return type;
        }
        
        public boolean isConstant() {
            return isConstant;
        }
        
        public void setNonConstant() {
            this.isConstant = false;
        }
        
        @Override
        public String toString() {
            if (!isConstant) return "non-constant";
            return String.format("%s (%s)", value, Symbol.dataTypeToString(type));
        }
    }
    
    // ========== EVALUACIÓN DE LITERALES ==========
    
    /**
     * Crea un valor constante a partir de un literal entero
     */
    public static ConstantValue fromIntLiteral(String literal) {
        try {
            int value = Integer.parseInt(literal);
            return new ConstantValue(value, Symbol.DataType.INT);
        } catch (NumberFormatException e) {
            return new ConstantValue(); // No constante si hay error
        }
    }
    
    /**
     * Crea un valor constante a partir de un literal real
     */
    public static ConstantValue fromRealLiteral(String literal) {
        try {
            double value = Double.parseDouble(literal);
            return new ConstantValue(value, Symbol.DataType.REAL);
        } catch (NumberFormatException e) {
            return new ConstantValue(); // No constante si hay error
        }
    }
    
    /**
     * Crea un valor constante a partir de un literal octal
     */
    public static ConstantValue fromOctalLiteral(String literal) {
        try {
            // Remover el '0' inicial si existe
            String octalStr = literal.startsWith("0") ? literal.substring(1) : literal;
            if (octalStr.isEmpty()) octalStr = "0";
            int value = Integer.parseInt(octalStr, 8);
            return new ConstantValue(value, Symbol.DataType.INT);
        } catch (NumberFormatException e) {
            return new ConstantValue();
        }
    }
    
    /**
     * Crea un valor constante a partir de un literal hexadecimal
     */
    public static ConstantValue fromHexLiteral(String literal) {
        try {
            // Remover el '0x' o '0X' inicial
            String hexStr = literal.substring(2);
            int value = Integer.parseInt(hexStr, 16);
            return new ConstantValue(value, Symbol.DataType.INT);
        } catch (Exception e) {
            return new ConstantValue();
        }
    }
    
    /**
     * Crea un valor constante a partir de un literal string
     */
    public static ConstantValue fromStringLiteral(String literal) {
        // Remover comillas
        String value = literal.substring(1, literal.length() - 1);
        return new ConstantValue(value, Symbol.DataType.STRING);
    }
    
    /**
     * Crea un valor constante a partir de un literal char
     */
    public static ConstantValue fromCharLiteral(String literal) {
        // Remover comillas simples
        String value = literal.substring(1, literal.length() - 1);
        if (value.length() == 1) {
            return new ConstantValue(value.charAt(0), Symbol.DataType.CHAR);
        }
        return new ConstantValue();
    }
    
    // ========== OPERACIONES BINARIAS ARITMÉTICAS ==========
    
    /**
     * Intenta evaluar una suma de constantes
     */
    public static ConstantValue foldAddition(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue(); // No constante
        }
        
        // Ambos INT
        if (left.getType() == Symbol.DataType.INT && right.getType() == Symbol.DataType.INT) {
            int result = (Integer)left.getValue() + (Integer)right.getValue();
            return new ConstantValue(result, Symbol.DataType.INT);
        }
        
        // Al menos uno REAL
        if (TypeChecker.isNumericType(left.getType()) && TypeChecker.isNumericType(right.getType())) {
            double leftVal = left.getType() == Symbol.DataType.INT ? 
                           ((Integer)left.getValue()).doubleValue() : (Double)left.getValue();
            double rightVal = right.getType() == Symbol.DataType.INT ? 
                            ((Integer)right.getValue()).doubleValue() : (Double)right.getValue();
            double result = leftVal + rightVal;
            return new ConstantValue(result, Symbol.DataType.REAL);
        }
        
        return new ConstantValue(); // Tipos incompatibles
    }
    
    /**
     * Intenta evaluar una resta de constantes
     */
    public static ConstantValue foldSubtraction(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue();
        }
        
        // Ambos INT
        if (left.getType() == Symbol.DataType.INT && right.getType() == Symbol.DataType.INT) {
            int result = (Integer)left.getValue() - (Integer)right.getValue();
            return new ConstantValue(result, Symbol.DataType.INT);
        }
        
        // Al menos uno REAL
        if (TypeChecker.isNumericType(left.getType()) && TypeChecker.isNumericType(right.getType())) {
            double leftVal = left.getType() == Symbol.DataType.INT ? 
                           ((Integer)left.getValue()).doubleValue() : (Double)left.getValue();
            double rightVal = right.getType() == Symbol.DataType.INT ? 
                            ((Integer)right.getValue()).doubleValue() : (Double)right.getValue();
            double result = leftVal - rightVal;
            return new ConstantValue(result, Symbol.DataType.REAL);
        }
        
        return new ConstantValue();
    }
    
    /**
     * Intenta evaluar una multiplicación de constantes
     */
    public static ConstantValue foldMultiplication(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue();
        }
        
        // Ambos INT
        if (left.getType() == Symbol.DataType.INT && right.getType() == Symbol.DataType.INT) {
            int result = (Integer)left.getValue() * (Integer)right.getValue();
            return new ConstantValue(result, Symbol.DataType.INT);
        }
        
        // Al menos uno REAL
        if (TypeChecker.isNumericType(left.getType()) && TypeChecker.isNumericType(right.getType())) {
            double leftVal = left.getType() == Symbol.DataType.INT ? 
                           ((Integer)left.getValue()).doubleValue() : (Double)left.getValue();
            double rightVal = right.getType() == Symbol.DataType.INT ? 
                            ((Integer)right.getValue()).doubleValue() : (Double)right.getValue();
            double result = leftVal * rightVal;
            return new ConstantValue(result, Symbol.DataType.REAL);
        }
        
        return new ConstantValue();
    }
    
    /**
     * Intenta evaluar una división de constantes
     */
    public static ConstantValue foldDivision(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue();
        }
        
        if (!TypeChecker.isNumericType(left.getType()) || !TypeChecker.isNumericType(right.getType())) {
            return new ConstantValue();
        }
        
        double leftVal = left.getType() == Symbol.DataType.INT ? 
                       ((Integer)left.getValue()).doubleValue() : (Double)left.getValue();
        double rightVal = right.getType() == Symbol.DataType.INT ? 
                        ((Integer)right.getValue()).doubleValue() : (Double)right.getValue();
        
        // Verificar división por cero
        if (rightVal == 0.0) {
            return new ConstantValue(); // No evaluar división por cero
        }
        
        double result = leftVal / rightVal;
        return new ConstantValue(result, Symbol.DataType.REAL);
    }
    
    /**
     * Intenta evaluar una división entera (DIV) de constantes
     */
    public static ConstantValue foldIntegerDivision(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue();
        }
        
        if (left.getType() != Symbol.DataType.INT || right.getType() != Symbol.DataType.INT) {
            return new ConstantValue(); // DIV solo para enteros
        }
        
        int leftVal = (Integer)left.getValue();
        int rightVal = (Integer)right.getValue();
        
        // Verificar división por cero
        if (rightVal == 0) {
            return new ConstantValue();
        }
        
        int result = leftVal / rightVal;
        return new ConstantValue(result, Symbol.DataType.INT);
    }
    
    /**
     * Intenta evaluar un módulo (MOD) de constantes
     */
    public static ConstantValue foldModulo(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue();
        }
        
        if (left.getType() != Symbol.DataType.INT || right.getType() != Symbol.DataType.INT) {
            return new ConstantValue(); // MOD solo para enteros
        }
        
        int leftVal = (Integer)left.getValue();
        int rightVal = (Integer)right.getValue();
        
        // Verificar división por cero
        if (rightVal == 0) {
            return new ConstantValue();
        }
        
        int result = leftVal % rightVal;
        return new ConstantValue(result, Symbol.DataType.INT);
    }
    
    // ========== OPERACIONES UNARIAS ==========
    
    /**
     * Intenta evaluar una negación unaria
     */
    public static ConstantValue foldUnaryMinus(ConstantValue operand) {
        if (!operand.isConstant()) {
            return new ConstantValue();
        }
        
        if (operand.getType() == Symbol.DataType.INT) {
            int result = -(Integer)operand.getValue();
            return new ConstantValue(result, Symbol.DataType.INT);
        }
        
        if (operand.getType() == Symbol.DataType.REAL) {
            double result = -(Double)operand.getValue();
            return new ConstantValue(result, Symbol.DataType.REAL);
        }
        
        return new ConstantValue();
    }
    
    /**
     * Intenta evaluar un signo positivo unario (no hace nada, retorna el mismo valor)
     */
    public static ConstantValue foldUnaryPlus(ConstantValue operand) {
        if (!operand.isConstant()) {
            return new ConstantValue();
        }
        
        if (TypeChecker.isNumericType(operand.getType())) {
            return operand; // El signo positivo no cambia el valor
        }
        
        return new ConstantValue();
    }
    
    // ========== OPERACIONES RELACIONALES ==========
    
    /**
     * Intenta evaluar una comparación de igualdad
     */
    public static ConstantValue foldEqual(ConstantValue left, ConstantValue right) {
        if (!left.isConstant() || !right.isConstant()) {
            return new ConstantValue();
        }
        
        // Comparar valores del mismo tipo
        if (left.getType() == right.getType()) {
            boolean result = left.getValue().equals(right.getValue());
            return new ConstantValue(result ? 1 : 0, Symbol.DataType.INT); // Pascal usa 1/0 para bool
        }
        
        // Comparación entre INT y REAL
        if (TypeChecker.isNumericType(left.getType()) && TypeChecker.isNumericType(right.getType())) {
            double leftVal = left.getType() == Symbol.DataType.INT ? 
                           ((Integer)left.getValue()).doubleValue() : (Double)left.getValue();
            double rightVal = right.getType() == Symbol.DataType.INT ? 
                            ((Integer)right.getValue()).doubleValue() : (Double)right.getValue();
            boolean result = leftVal == rightVal;
            return new ConstantValue(result ? 1 : 0, Symbol.DataType.INT);
        }
        
        return new ConstantValue();
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Verifica si un valor constante es verdadero (para optimización de condicionales)
     */
    public static boolean isConstantTrue(ConstantValue value) {
        if (!value.isConstant()) {
            return false;
        }
        
        if (value.getType() == Symbol.DataType.INT) {
            return (Integer)value.getValue() != 0;
        }
        
        return false;
    }
    
    /**
     * Verifica si un valor constante es falso (para optimización de condicionales)
     */
    public static boolean isConstantFalse(ConstantValue value) {
        if (!value.isConstant()) {
            return false;
        }
        
        if (value.getType() == Symbol.DataType.INT) {
            return (Integer)value.getValue() == 0;
        }
        
        return false;
    }
}
