package semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un ámbito (scope) en el programa.
 * Maneja variables locales y parámetros dentro de funciones/procedimientos.
 */
public class Scope {
    private String name;                        // Nombre del ámbito (GLOBAL, nombre de función)
    private Scope parent;                       // Ámbito padre (para ámbitos anidados)
    private Map<String, Symbol> symbols;        // Símbolos en este ámbito
    private List<Scope> children;               // Ámbitos hijos
    private ScopeType type;                     // Tipo de ámbito
    
    /**
     * Tipos de ámbito
     */
    public enum ScopeType {
        GLOBAL,        // Ámbito global del programa
        FUNCTION,      // Dentro de una función
        PROCEDURE,     // Dentro de un procedimiento
        BLOCK          // Bloque anidado (if, while, for)
    }
    
    /**
     * Constructor para ámbito
     */
    public Scope(String name, ScopeType type, Scope parent) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.children = new ArrayList<>();
        
        // Agregar este scope como hijo del padre
        if (parent != null) {
            parent.addChild(this);
        }
    }
    
    /**
     * Constructor para ámbito global
     */
    public Scope(String name, ScopeType type) {
        this(name, type, null);
    }
    
    // ========== GESTIÓN DE SÍMBOLOS ==========
    
    /**
     * Agrega un símbolo al ámbito actual
     * @return true si se agregó correctamente, false si ya existía
     */
    public boolean addSymbol(Symbol symbol) {
        String key = symbol.getName().toLowerCase();
        
        if (symbols.containsKey(key)) {
            return false; // Ya existe en este ámbito
        }
        
        symbols.put(key, symbol);
        return true;
    }
    
    /**
     * Busca un símbolo SOLO en este ámbito (no busca en padres)
     */
    public Symbol findSymbolLocal(String name) {
        return symbols.get(name.toLowerCase());
    }
    
    /**
     * Busca un símbolo en este ámbito y en los ámbitos padres
     */
    public Symbol findSymbol(String name) {
        String key = name.toLowerCase();
        
        // Buscar en este ámbito
        Symbol symbol = symbols.get(key);
        if (symbol != null) {
            return symbol;
        }
        
        // Buscar en el ámbito padre
        if (parent != null) {
            return parent.findSymbol(name);
        }
        
        return null; // No encontrado
    }
    
    /**
     * Verifica si un símbolo existe en este ámbito (sin buscar en padres)
     */
    public boolean existsInCurrentScope(String name) {
        return symbols.containsKey(name.toLowerCase());
    }
    
    /**
     * Verifica si un símbolo existe en este ámbito o en padres
     */
    public boolean exists(String name) {
        return findSymbol(name) != null;
    }
    
    /**
     * Obtiene todos los símbolos del ámbito actual
     */
    public Map<String, Symbol> getAllSymbols() {
        return new HashMap<>(symbols); // Retorna copia para evitar modificaciones externas
    }
    
    /**
     * Obtiene la cantidad de símbolos en este ámbito
     */
    public int getSymbolCount() {
        return symbols.size();
    }
    
    // ========== GESTIÓN DE ÁMBITOS HIJOS ==========
    
    /**
     * Agrega un ámbito hijo
     */
    public void addChild(Scope child) {
        children.add(child);
    }
    
    /**
     * Obtiene los ámbitos hijos
     */
    public List<Scope> getChildren() {
        return new ArrayList<>(children);
    }
    
    // ========== GETTERS ==========
    
    public String getName() {
        return name;
    }
    
    public ScopeType getType() {
        return type;
    }
    
    public Scope getParent() {
        return parent;
    }
    
    /**
     * Obtiene el nombre completo del ámbito (incluyendo padres)
     */
    public String getFullName() {
        if (parent == null || parent.getType() == ScopeType.GLOBAL) {
            return name;
        }
        return parent.getFullName() + "." + name;
    }
    
    /**
     * Verifica si este es el ámbito global
     */
    public boolean isGlobal() {
        return type == ScopeType.GLOBAL;
    }
    
    /**
     * Verifica si este ámbito es una función
     */
    public boolean isFunction() {
        return type == ScopeType.FUNCTION;
    }
    
    /**
     * Verifica si este ámbito es un procedimiento
     */
    public boolean isProcedure() {
        return type == ScopeType.PROCEDURE;
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * Limpia todos los símbolos del ámbito
     */
    public void clear() {
        symbols.clear();
        children.clear();
    }
    
    /**
     * Cuenta la cantidad total de símbolos (incluyendo hijos)
     */
    public int getTotalSymbolCount() {
        int count = symbols.size();
        for (Scope child : children) {
            count += child.getTotalSymbolCount();
        }
        return count;
    }
    
    /**
     * Representación en string del ámbito
     */
    @Override
    public String toString() {
        return String.format("Scope{name='%s', type=%s, symbols=%d, children=%d}", 
                           name, type, symbols.size(), children.size());
    }
    
    /**
     * Imprime el árbol de ámbitos de forma jerárquica
     */
    public void printTree(int indent) {
        String indentation = "  ".repeat(indent);
        System.out.println(indentation + "├─ " + name + " [" + type + "] (" + symbols.size() + " symbols)");
        
        // Imprimir símbolos
        for (Symbol symbol : symbols.values()) {
            System.out.println(indentation + "│  └─ " + symbol.getName() + " : " + 
                             Symbol.dataTypeToString(symbol.getDataType()));
        }
        
        // Imprimir ámbitos hijos
        for (Scope child : children) {
            child.printTree(indent + 1);
        }
    }
}
