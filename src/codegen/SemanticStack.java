package codegen;

import java.util.Stack;

/**
 * Pila semántica para la generación de código.
 * Almacena registros temporales, direcciones de variables y etiquetas.
 */
public class SemanticStack {
    private Stack<String> stack;
    private Stack<String> labelStack;
    
    /**
     * Constructor
     */
    public SemanticStack() {
        this.stack = new Stack<>();
        this.labelStack = new Stack<>();
    }
    
    /**
     * Agrega un elemento a la pila
     */
    public void push(String element) {
        stack.push(element);
    }
    
    /**
     * Extrae el elemento superior de la pila
     */
    public String pop() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.pop();
    }
    
    /**
     * Observa el elemento superior sin extraerlo
     */
    public String peek() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }
    
    /**
     * Verifica si la pila está vacía
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    /**
     * Obtiene el tamaño de la pila
     */
    public int size() {
        return stack.size();
    }
    
    /**
     * Limpia la pila
     */
    public void clear() {
        stack.clear();
        labelStack.clear();
    }
    
    // ========== GESTIÓN DE ETIQUETAS ==========
    
    /**
     * Agrega una etiqueta a la pila de etiquetas
     */
    public void pushLabel(String label) {
        labelStack.push(label);
    }
    
    /**
     * Extrae la etiqueta superior de la pila de etiquetas
     */
    public String popLabel() {
        if (labelStack.isEmpty()) {
            return null;
        }
        return labelStack.pop();
    }
    
    /**
     * Extrae una etiqueta en una posición específica (0 = top)
     */
    public String popLabel(int offset) {
        if (offset >= labelStack.size()) {
            return null;
        }
        
        // Guardar elementos temporalmente
        Stack<String> temp = new Stack<>();
        for (int i = 0; i < offset; i++) {
            temp.push(labelStack.pop());
        }
        
        String result = labelStack.pop();
        
        // Restaurar elementos
        while (!temp.isEmpty()) {
            labelStack.push(temp.pop());
        }
        
        return result;
    }
    
    /**
     * Observa la etiqueta superior sin extraerla
     */
    public String peekLabel() {
        if (labelStack.isEmpty()) {
            return null;
        }
        return labelStack.peek();
    }
    
    /**
     * Observa una etiqueta en una posición específica (0 = top)
     */
    public String peekLabel(int offset) {
        if (offset >= labelStack.size()) {
            return null;
        }
        return labelStack.get(labelStack.size() - 1 - offset);
    }
    
    /**
     * Verifica si la pila de etiquetas está vacía
     */
    public boolean isLabelStackEmpty() {
        return labelStack.isEmpty();
    }
    
    /**
     * Obtiene el tamaño de la pila de etiquetas
     */
    public int labelStackSize() {
        return labelStack.size();
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Obtiene una representación en string de la pila
     */
    @Override
    public String toString() {
        return "SemanticStack{" +
               "stack=" + stack +
               ", labelStack=" + labelStack +
               '}';
    }
    
    /**
     * Imprime el contenido de la pila (para debugging)
     */
    public void print() {
        System.out.println("=== SEMANTIC STACK ===");
        System.out.println("Values: " + stack);
        System.out.println("Labels: " + labelStack);
        System.out.println("======================");
    }
}