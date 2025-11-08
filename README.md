# Compilador ABS - Parser Léxico y Sintáctico

**Proyecto 1 - Compiladores**  
**Lenguaje:** ABS (Abstract Block Structured Language)  
**Herramientas:** JFlex 1.9.1 + CUP 11b  
**Estado:**  100% Funcional

---

## DESCRIPCIÓN

Compilador para el lenguaje ABS que realiza análisis léxico y sintáctico completo con detección y recuperación de errores.

---

## INICIO RÁPIDO

### **1. Compilar el Proyecto**
```powershell
.\compile.ps1
```

### **2. Ejecutar un Programa**
```powershell
java -cp "bin;lib\*" main.Main programa.abs
```

### **3. Ejecutar Pruebas**
```powershell
# Todas las pruebas (26 casos)
.\run_all_tests.ps1
```

---

## ESTRUCTURA DEL PROYECTO

```
Proyecto-1-Compiladores---Escaner/
│
├── src/                          # Código fuente
│   ├── lexer/
│   │   └── Scanner.flex         # Especificación léxica
│   ├── parser/
│   │   └── Parser.cup           # Gramática sintáctica
│   └── main/
│       └── Main.java            # Punto de entrada
│
├── lib/                          # Librerias (JFlex, CUP)
├── bin/                          # Clases compiladas
├── tests/                        # Casos de prueba (26 casos)
├── docs/
│   └── ManualUsuario.md         # Manual de usuario
│
├── compile.ps1                   # Compilar proyecto
├── run_all_tests.ps1            # Ejecutar todas las pruebas
├── programa.abs                 # Programa de ejemplo
└── README.md                    # Este archivo
```

---

## COMPILACIÓN MANUAL (Paso a Paso)

Si no puedes usar `compile.ps1`, compila manualmente:

### **Paso 1: Generar Parser (CUP)**
```powershell
java -jar lib\java-cup-11b.jar -parser Parser -symbols sym -destdir src\parser src\parser\Parser.cup
```

### **Paso 2: Generar Scanner (JFlex)**
```powershell
java -jar lib\jflex-full-1.9.1.jar -d src\lexer src\lexer\Scanner.flex
```

### **Paso 3: Compilar Java**
```powershell
javac -cp "lib\java-cup-11b-runtime.jar" -d bin src\parser\sym.java
javac -cp "lib\java-cup-11b-runtime.jar;bin" -d bin src\lexer\Scanner.java
javac -cp "lib\java-cup-11b-runtime.jar;bin" -d bin src\parser\Parser.java
javac -cp "lib\java-cup-11b-runtime.jar;bin" -d bin src\main\Main.java
```

---

## CARACTERÍSTICAS DEL LENGUAJE ABS

### **Estructura de un Programa**
```pascal
PROGRAM NombrePrograma
VAR
    x, y : INT;
    mensaje : STRING;

FUNCTION Sumar(INT a, INT b) : INT
BEGIN
    Sumar := a + b;
END

BEGIN
    x := 10;
    y := 20;
    WRITE(Sumar(x, y));
END
```

### **Tipos de Datos**
- `INT` - Enteros
- `REAL` - Reales
- `STRING` - Cadenas de texto
- `CHAR` - Caracteres

### **Estructuras de Control**
- `IF condition THEN ... ELSE ... END` - Condicional
- `WHILE condition DO ... END` - Bucle while
- `FOR var := inicio TO fin DO ... END` - Bucle for

### **Operadores**
- Aritméticos: `+`, `-`, `*`, `/`, `DIV`, `MOD`
- Relacionales: `=`, `<>`, `<`, `>`, `<=`, `>=`
- Lógicos: `AND`, `OR`, `NOT`
- Incremento/Decremento: `++`, `--`

### **Entrada/Salida**
- `READ()` o `READ(variable)` - Leer entrada
- `WRITE(expresion1, expresion2, ...)` - Escribir salida

---

## ESTADO DEL PROYECTO

| Componente | Estado |
|-----------|--------|
| Scanner (Léxico) | 100% |
| Parser (Sintáctico) | 100% |
| Manejo de Errores | 100% |
| Casos de Prueba | 26/26 (100%) |

---

## REQUISITOS

- Java JDK 8+
- PowerShell 5.1+
- Windows

---

## SOLUCIÓN DE PROBLEMAS

Ver: `docs/ManualUsuario.md` (Sección 7)

---

**Última actualización:** Noviembre 4, 2025  
**Estado:** Compilador 100% funcional