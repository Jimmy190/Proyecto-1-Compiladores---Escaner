# Manual de Usuario Completo - Compilador ABS
**Proyecto 1 - Compiladores**  
**Fecha:** Noviembre 2025  
**Version:** 1.0

---

## Tabla de Contenidos

1. [Introduccion](#1-introduccion)
2. [Requisitos del Sistema](#2-requisitos-del-sistema)
3. [Compilacion del Proyecto](#3-compilacion-del-proyecto)
4. [Pruebas Automaticas - INICIO AQUI](#4-pruebas-automaticas-recomendado---inicio-aqui)
5. [Ejecucion Manual - Guia Paso a Paso](#5-ejecucion-manual---guia-paso-a-paso)
6. [Casos de Prueba Detallados](#6-casos-de-prueba-detallados)
7. [Estructura del Lenguaje ABS](#7-estructura-del-lenguaje-abs)
8. [Interpretacion de Resultados](#8-interpretacion-de-resultados)
9. [Solucion de Problemas](#9-solucion-de-problemas)

---

## 1. INTRODUCCION

Este manual proporciona instrucciones completas para compilar, ejecutar y probar el Compilador ABS (Abstract Block Structured Language). El compilador realiza analisis lexico y sintactico con deteccion y recuperacion de errores.

**Caracteristicas principales:**
- Analisis lexico completo (tokens, identificadores, numeros, strings, comentarios)
- Analisis sintactico con recuperacion de errores
- Deteccion detallada de errores lexicos y sintacticos
- Tabla de tokens aceptados
- 26 casos de prueba (100% funcional)

---

## 2. REQUISITOS DEL SISTEMA

### Software Necesario

| Componente | Version Minima | Comando para Verificar |
|------------|----------------|------------------------|
| **Java JDK** | 8 o superior | `java -version` y `javac -version` |
| **PowerShell** | 5.1 o superior | `$PSVersionTable.PSVersion` |
| **Sistema Operativo** | Windows 10/11 | N/A |

### Librerias Incluidas (en carpeta `lib/`)

- `jflex-full-1.9.1.jar` - Generador de analizadores lexicos
- `java-cup-11b.jar` - Generador de parsers
- `java-cup-11b-runtime.jar` - Runtime de CUP

### Espacio en Disco

- Minimo: 50 MB
- Recomendado: 100 MB

---

## 3. COMPILACION DEL PROYECTO

### 3.1 Compilacion Automatica (RECOMENDADO)

**Comando:**
```powershell
.\compile.ps1
```

**Proceso:**
1. Limpia archivos compilados anteriores
2. Ejecuta JFlex para generar `Scanner.java`
3. Ejecuta CUP para generar `Parser.java` y `sym.java`
4. Compila todos los archivos Java
5. Verifica que no haya errores

**Salida Esperada:**
```
================================================================
          COMPILACION COMPLETADA EXITOSAMENTE
================================================================

Archivos generados:
  - src/lexer/Scanner.java      (JFlex)
  - src/parser/Parser.java      (CUP)
  - src/parser/sym.java         (CUP)
  - bin/*.class                 (Compilados)
```

**Tiempo estimado:** 5-10 segundos

---

### 3.2 Compilacion Manual (Paso a Paso)

Si necesitas compilar manualmente o el script falla:

#### Paso 1: Generar Parser con CUP
```powershell
java -jar lib\java-cup-11b.jar -parser Parser -symbols sym -destdir src\parser src\parser\Parser.cup
```

**Resultado esperado:**
- Archivo generado: `src/parser/Parser.java`
- Archivo generado: `src/parser/sym.java`
- Mensaje: "Code written to Parser.java, and sym.java"

#### Paso 2: Generar Scanner con JFlex
```powershell
java -jar lib\jflex-full-1.9.1.jar -d src\lexer src\lexer\Scanner.flex
```

**Resultado esperado:**
- Archivo generado: `src/lexer/Scanner.java`
- Mensaje: "Writing code to Scanner.java"

#### Paso 3: Compilar Archivos Java

**3a. Compilar sym.java:**
```powershell
javac -cp "lib\java-cup-11b-runtime.jar" -d bin src\parser\sym.java
```

**3b. Compilar Scanner.java:**
```powershell
javac -cp "lib\java-cup-11b-runtime.jar;bin" -d bin src\lexer\Scanner.java
```

**3c. Compilar Parser.java:**
```powershell
javac -cp "lib\java-cup-11b-runtime.jar;bin" -d bin src\parser\Parser.java
```

**3d. Compilar Main.java:**
```powershell
javac -cp "lib\java-cup-11b-runtime.jar;bin" -d bin src\main\Main.java
```

**Verificacion:** Los archivos `.class` deben aparecer en la carpeta `bin/`

---

## 4. PRUEBAS AUTOMATICAS (RECOMENDADO - INICIO AQUI)

### 4.1 Ejecutar Todos los Tests con Detalles (MODO VERBOSE)

**Este es el metodo recomendado para la revision del proyecto.**

**Comando:**
```powershell
.\run_all_tests.ps1 -verbose
```

**Que hace:**
- Ejecuta los 26 casos de prueba automaticamente
- Muestra el resultado completo de cada test
- Presenta los errores detectados en cada caso
- Muestra los tokens aceptados
- Genera un resumen final con estadisticas

**Salida Esperada:**
```
========================================
EJECUTANDO TODOS LOS CASOS DE PRUEBA
========================================

=== CASOS VALIDOS (deben compilar sin errores) ===

Test 01 : Programa minimo [OK]
OUTPUT:
===================================================
           COMPILADOR ABS - ANALIZADOR
===================================================
...
ANALISIS COMPLETADO EXITOSAMENTE!

Test 02 : Variables globales [OK]
OUTPUT:
...

=== ERRORES LEXICOS (deben detectar errores lexicos) ===

Test 11 : Identificadores invalidos [OK]
OUTPUT:
============================================================
                ERRORES LEXICOS ENCONTRADOS
============================================================
Se encontraron 3 error(es) lexico(s):
...

=== ERRORES SINTACTICOS (deben detectar errores sintacticos) ===

Test 17 : Variables sin tipo [OK]
OUTPUT:
============================================================
              ERRORES SINTACTICOS ENCONTRADOS
============================================================
...

========================================
RESUMEN DE RESULTADOS
========================================
Total de tests:  26
Tests exitosos:  26
Tests fallidos:  0
Porcentaje:      100%
========================================

TODOS LOS TESTS PASARON EXITOSAMENTE!
```

**Tiempo de ejecucion:** ~15-20 segundos

**Ventajas del modo verbose:**
-  Revision completa en un solo comando
-  Ver todos los errores detectados
-  Verificar el comportamiento del compilador
-  Ideal para profesores y revisores

---

### 4.2 Ejecutar Tests (Modo Normal - Resumen)

Si solo necesitas verificar que todo funciona sin ver detalles:

**Comando:**
```powershell
.\run_all_tests.ps1
```

**Salida:**
```
========================================
EJECUTANDO TODOS LOS CASOS DE PRUEBA
========================================

=== CASOS VALIDOS (deben compilar sin errores) ===

Test 01 : Programa minimo [OK]
Test 02 : Variables globales [OK]
Test 03 : Funcion simple [OK]
...

=== ERRORES LEXICOS (deben detectar errores lexicos) ===

Test 11 : Identificadores invalidos [OK]
...

=== ERRORES SINTACTICOS (deben detectar errores sintacticos) ===

Test 17 : Variables sin tipo [OK]
...

========================================
RESUMEN DE RESULTADOS
========================================
Total de tests:  26
Tests exitosos:  26
Tests fallidos:  0
Porcentaje:      100%
========================================
```

**Tiempo de ejecucion:** ~10-15 segundos

---

### 4.3 Generar Reporte Completo en Markdown

Para generar documentacion academica:

**Comando:**
```powershell
.\run_all_tests.ps1 -generateReport
```

**Que hace:**
- Ejecuta todos los tests
- Captura resultados esperados vs obtenidos
- Genera archivo `docs/ResultadosPruebas.md` con tablas completas

**Contenido del reporte:**
- Tablas comparativas (Esperado vs Obtenido)
- Outputs completos de cada test
- Errores encontrados
- Estadisticas finales

**Cuando usar:**
- Entrega academica del proyecto
- Documentacion para el profesor
- Archivo de resultados permanente

---

## 5. EJECUCION MANUAL - GUIA PASO A PASO

**Usa esta seccion si necesitas probar archivos individuales o entender como funciona el compilador internamente.**

### 5.1 Sintaxis Basica

```powershell
java -cp "bin;lib\*" main.Main <ruta_archivo.abs>
```

**Componentes del comando:**
- `java`: Interprete de Java
- `-cp "bin;lib\*"`: Classpath (archivos compilados + librerias)
- `main.Main`: Clase principal
- `<ruta_archivo.abs>`: Archivo a compilar

---

### 5.2 Ejemplo 1: Programa Completo (programa.abs)

**Paso 1: Ver el archivo**
```powershell
cat programa.abs
```

Este programa de ejemplo incluye:
-  Variables globales (INT, STRING, REAL, CHAR)
-  Funciones con parametros y retorno
-  Procedimientos
-  Estructuras IF-THEN-ELSE
-  Operaciones READ y WRITE
-  Expresiones aritmeticas
-  Comentarios

**Paso 2: Ejecutar el compilador**
```powershell
java -cp "bin;lib\*" main.Main programa.abs
```

**Paso 3: Analizar la salida**

```
===================================================
           COMPILADOR ABS - ANALIZADOR
===================================================
Archivo: programa.abs

EJECUTANDO ANALISIS SINTACTICO...
(El analisis continuara a pesar de errores)
---------------------------------------------------
ANALISIS SINTACTICO COMPLETADO

============================================================
                ERRORES LEXICOS ENCONTRADOS
============================================================
No se encontraron errores lexicos

============================================================
              ERRORES SINTACTICOS ENCONTRADOS
============================================================
No se encontraron errores sintacticos

============================================================
                TOKENS ACEPTADOS
============================================================
Token                Tipo de Token             Lineas
----------------------------------------------------------
...
(Mas de 40 tokens reconocidos)
...

============================================================
                     RESUMEN
============================================================
Total de errores lexicos: 0
Total de errores sintacticos: 0
Total de errores: 0

ANALISIS COMPLETADO EXITOSAMENTE!
El codigo fuente es lexica y sintacticamente correcto
```

**Interpretacion:**
-  No hay errores
-  Todos los tokens reconocidos correctamente
-  Programa complejo valido
-  Exit code: 0 (exito)

---

### 5.3 Ejemplo 2: Deteccion de Error Lexico

**Paso 1: Ver el codigo fuente**
```powershell
cat tests\errores_lexicos\01_identificadores_invalidos.abs
```

**Contenido:**
```pascal
PROGRAM ErrorIdentificador
VAR
    123variable : INT;
    nombre@invalido : STRING;
    _invalido : CHAR;
BEGIN
END
```

**Paso 2: Ejecutar**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\01_identificadores_invalidos.abs
```

**Paso 3: Analizar errores**

```
============================================================
                ERRORES LEXICOS ENCONTRADOS
============================================================
Se encontraron 3 error(es) lexico(s):
------------------------------------------------------------
1. Error en linea 3, columna 5: identificador invalido, no puede 
   iniciar con un numero. Texto: 123variable

2. Error en linea 4, columna 11: identificador invalido, no puede 
   iniciar con simbolo. Texto: @

3. Error en linea 5, columna 5: identificador invalido, no puede 
   iniciar con simbolo. Texto: _invalido

============================================================
                     RESUMEN
============================================================
Total de errores lexicos: 3
Total de errores sintacticos: 2
Total de errores: 5

SE ENCONTRARON ERRORES - REVISE LOS REPORTES ANTERIORES
(El analisis completo el archivo a pesar de los errores)
```

**Interpretacion:**
-  3 errores lexicos detectados
-  El compilador continuo el analisis
-  Exit code: 1 (errores)

**Reglas de identificadores:**
-  Deben empezar con letra
-  No pueden empezar con numero
-  No pueden empezar con `_` o simbolos especiales
-  Pueden contener letras, numeros despues del primer caracter

---

### 5.4 Ejemplo 3: Deteccion de Error Sintactico

**Paso 1: Ver el codigo fuente**
```powershell
cat tests\errores_sintacticos\01_variables_sin_tipo.abs
```

**Contenido:**
```pascal
PROGRAM ErrorVariables
VAR
    x, y;            // Error: falta tipo
    nombre : STRING;
    edad;            // Error: falta tipo
    contador : INT;
BEGIN
END
```

**Paso 2: Ejecutar**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\01_variables_sin_tipo.abs
```

**Paso 3: Analizar errores**

```
============================================================
              ERRORES SINTACTICOS ENCONTRADOS
============================================================
Se encontraron 2 error(es) sintactico(s):
------------------------------------------------------------
1. Linea 3, Columna 12: Token inesperado ';'. Verifique la 
   sintaxis del programa

2. Error sintactico: Error en la estructura del programa. 
   Se esperaba 'PROGRAM nombre'

============================================================
                     RESUMEN
============================================================
Total de errores lexicos: 0
Total de errores sintacticos: 2
Total de errores: 2

SE ENCONTRARON ERRORES - REVISE LOS REPORTES ANTERIORES
```

**Interpretacion:**
-  2 errores sintacticos detectados
-  Variables sin tipo declarado
-  Exit code: 1

**Sintaxis correcta de variables:**
```pascal
VAR
    variable : TIPO;
    var1, var2, var3 : TIPO;
```

---

## 6. CASOS DE PRUEBA DETALLADOS

### 5.1 Ejecutar Todos los Tests (Modo Normal)

**Comando:**
```powershell
.\run_all_tests.ps1
```

**Que hace:**
- Ejecuta los 26 casos de prueba
- Muestra resultado de cada test (OK/FAIL)
- Presenta resumen final con estadisticas

**Salida:**
```
========================================
EJECUTANDO TODOS LOS CASOS DE PRUEBA
========================================

=== CASOS VALIDOS (deben compilar sin errores) ===

Test 01 : Programa minimo [OK]
Test 02 : Variables globales [OK]
...
Test 10 : Programa completo [OK]

=== ERRORES LEXICOS (deben detectar errores lexicos) ===

Test 11 : Identificadores invalidos [OK]
...

=== ERRORES SINTACTICOS (deben detectar errores sintacticos) ===

Test 17 : Variables sin tipo [OK]
...

========================================
RESUMEN DE RESULTADOS
========================================
Total de tests:  26
Tests exitosos:  26
Tests fallidos:  0
Porcentaje:      100%
========================================

TODOS LOS TESTS PASARON EXITOSAMENTE!
```

**Tiempo de ejecucion:** ~10-15 segundos

---

### 5.2 Modo Verbose (Con Detalles)

**Comando:**
```powershell
.\run_all_tests.ps1 -verbose
```

**Que muestra:**
- Resultado de cada test
- Output completo del compilador
- Errores detectados (lexicos y sintacticos)
- Tokens aceptados
- Tabla completa de simbolos

**Cuando usar:**
- **RECOMENDADO para revision del profesor**
- Depuracion de errores
- Verificar que errores detecta el compilador
- Revisar tokens reconocidos

---

### 5.3 Generar Reporte Completo

**Comando:**
```powershell
.\run_all_tests.ps1 -generateReport
```

**Que hace:**
- Ejecuta todos los tests
- Captura resultados esperados vs obtenidos
- Genera archivo `docs/ResultadosPruebas.md`

**Contenido del reporte:**
- Tablas comparativas
- Detalles de cada test
- Outputs completos
- Errores encontrados
- Estadisticas

**Cuando usar:**
- Entrega academica
- Documentacion del proyecto
- Revision del profesor

---

## 6. CASOS DE PRUEBA DETALLADOS

### 6.1 Casos Validos (10 tests)

Estos programas deben compilar sin errores.

#### Test 01: Programa Minimo
**Archivo:** `tests/validos/01_programa_minimo.abs`  
**Que prueba:** Estructura minima de un programa

**Codigo:**
```pascal
PROGRAM ProgramaMinimo
BEGIN
END
```

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\01_programa_minimo.abs
```

**Resultado esperado:** 0 errores, 4 tokens

---

#### Test 02: Variables Globales
**Archivo:** `tests/validos/02_variables_globales.abs`  
**Que prueba:** Declaracion de variables de diferentes tipos

**Caracteristicas:**
- Multiples variables del mismo tipo
- Tipos: INT, STRING, REAL, CHAR
- Uso de comas para declaraciones multiples

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\02_variables_globales.abs
```

**Resultado esperado:** 0 errores, ~20 tokens

---

#### Test 03: Funcion Simple
**Archivo:** `tests/validos/03_funcion_simple.abs`  
**Que prueba:** Declaracion y uso de funciones

**Caracteristicas:**
- Parametros tipados
- Tipo de retorno
- Asignacion al nombre de la funcion
- Llamada con argumentos

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\03_funcion_simple.abs
```

**Resultado esperado:** 0 errores, funcion reconocida

---

#### Test 04: Procedimiento Simple
**Archivo:** `tests/validos/04_procedure_simple.abs`  
**Que prueba:** Declaracion y uso de procedimientos

**Caracteristicas:**
- PROCEDURE sin tipo de retorno
- Parametros
- Llamada al procedimiento

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\04_procedure_simple.abs
```

**Resultado esperado:** 0 errores

---

#### Test 05: Expresiones Aritmeticas
**Archivo:** `tests/validos/05_expresiones_aritmeticas.abs`  
**Que prueba:** Operaciones aritmeticas

**Operadores probados:**
- `+` Suma
- `-` Resta
- `*` Multiplicacion
- `/` Division
- `DIV` Division entera
- `MOD` Modulo

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\05_expresiones_aritmeticas.abs
```

**Resultado esperado:** 0 errores, todos los operadores reconocidos

---

#### Test 06: Estructura IF
**Archivo:** `tests/validos/06_if_simple.abs`  
**Que prueba:** Condicionales IF-THEN-ELSE

**Caracteristicas:**
- Condicion booleana
- Bloque THEN
- Bloque ELSE (opcional)
- Operadores relacionales

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\06_if_simple.abs
```

**Resultado esperado:** 0 errores

---

#### Test 07: Bucle WHILE
**Archivo:** `tests/validos/07_while_loop.abs`  
**Que prueba:** Bucle WHILE-DO

**Caracteristicas:**
- Condicion de ciclo
- Bloque DO
- Instrucciones dentro del ciclo

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\07_while_loop.abs
```

**Resultado esperado:** 0 errores

---

#### Test 08: Bucle FOR
**Archivo:** `tests/validos/08_for_loop.abs`  
**Que prueba:** Bucle FOR-TO-DO

**Caracteristicas:**
- Variable de control
- Valor inicial
- Valor final (TO)
- Bloque DO

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\08_for_loop.abs
```

**Resultado esperado:** 0 errores

---

#### Test 09: Operaciones READ/WRITE
**Archivo:** `tests/validos/09_read_write.abs`  
**Que prueba:** Entrada y salida

**Caracteristicas:**
- `READ()` sin parametros
- `READ(variable)` con variable
- `WRITE(expresion)` con expresion
- `WRITE(exp1, exp2, ...)` multiple

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\09_read_write.abs
```

**Resultado esperado:** 0 errores

---

#### Test 10: Programa Completo
**Archivo:** `tests/validos/10_programa_completo.abs`  
**Que prueba:** Integracion de todas las caracteristicas

**Incluye:**
- Variables globales
- Funciones
- Procedimientos
- IF-THEN-ELSE
- WHILE
- FOR
- READ/WRITE
- Expresiones complejas

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\10_programa_completo.abs
```

**Resultado esperado:** 0 errores, programa complejo valido

---

### 6.2 Errores Lexicos (6 tests)

Estos programas contienen errores en el nivel lexico.

#### Test 11: Identificadores Invalidos
**Archivo:** `tests/errores_lexicos/01_identificadores_invalidos.abs`  
**Que prueba:** Deteccion de identificadores mal formados

**Errores incluidos:**
- `123variable` - Empieza con numero
- `@simbolo` - Empieza con @
- `_invalido` - Empieza con guion bajo

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\01_identificadores_invalidos.abs
```

**Resultado esperado:** 3 errores lexicos detectados, exit code 1

---

#### Test 12: Strings Invalidos
**Archivo:** `tests/errores_lexicos/02_strings_invalidos.abs`  
**Que prueba:** Deteccion de strings mal formados

**Errores incluidos:**
- String sin cerrar: `"texto sin comilla final`
- String con salto de linea sin cerrar

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\02_strings_invalidos.abs
```

**Resultado esperado:** Errores lexicos detectados, exit code 1

---

#### Test 13: Caracteres Invalidos
**Archivo:** `tests/errores_lexicos/03_char_invalidos.abs`  
**Que prueba:** Deteccion de caracteres mal formados

**Errores incluidos:**
- `''` - Caracter vacio
- `'ab'` - Mas de un caracter
- `'sin cerrar` - Caracter sin cerrar

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\03_char_invalidos.abs
```

**Resultado esperado:** 3 errores lexicos detectados, exit code 1

---

#### Test 14: Numeros Reales Invalidos
**Archivo:** `tests/errores_lexicos/04_numeros_reales_invalidos.abs`  
**Que prueba:** Deteccion de numeros mal formados

**Errores incluidos:**
- `3.` - Punto sin decimales
- `.14` - Empieza con punto
- `12..34` - Doble punto

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\04_numeros_reales_invalidos.abs
```

**Resultado esperado:** 3 errores lexicos detectados, exit code 1

---

#### Test 15: Comentarios Sin Cerrar
**Archivo:** `tests/errores_lexicos/05_comentarios_sin_cerrar.abs`  
**Que prueba:** Deteccion de comentarios sin cerrar

**Errores incluidos:**
- `{ comentario sin cerrar`
- `(* comentario sin cerrar`

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\05_comentarios_sin_cerrar.abs
```

**Resultado esperado:** Error lexico detectado, exit code 1

---

#### Test 16: Caracteres No Permitidos
**Archivo:** `tests/errores_lexicos/06_caracteres_invalidos.abs`  
**Que prueba:** Deteccion de caracteres no validos en el lenguaje

**Errores incluidos:**
- `$` - Signo de dolar
- `#` - Numeral
- `@` - Arroba

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_lexicos\06_caracteres_invalidos.abs
```

**Resultado esperado:** 3 errores lexicos detectados, exit code 1

---

### 6.3 Errores Sintacticos (10 tests)

Estos programas contienen errores en la estructura sintactica.

#### Test 17: Variables Sin Tipo
**Archivo:** `tests/errores_sintacticos/01_variables_sin_tipo.abs`  
**Que prueba:** Deteccion de variables sin tipo declarado

**Error:** Declaraciones como `x, y;` sin especificar tipo

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\01_variables_sin_tipo.abs
```

**Resultado esperado:** 2 errores sintacticos, exit code 1

---

#### Test 18: Funcion Sin Coma en Parametros
**Archivo:** `tests/errores_sintacticos/02_funcion_sin_coma_parametros.abs`  
**Que prueba:** Deteccion de parametros mal separados

**Error:** `FUNCTION Sumar(INT a INT b)` - Falta coma entre parametros

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\02_funcion_sin_coma_parametros.abs
```

**Resultado esperado:** Error sintactico detectado, exit code 1

---

#### Test 19: Procedimiento Sin BEGIN
**Archivo:** `tests/errores_sintacticos/03_procedure_sin_begin.abs`  
**Que prueba:** Deteccion de bloque sin BEGIN

**Error:** Procedimiento sin palabra clave BEGIN

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\03_procedure_sin_begin.abs
```

**Resultado esperado:** Error sintactico detectado, exit code 1

---

#### Test 20: Expresiones Invalidas
**Archivo:** `tests/errores_sintacticos/04_expresiones_invalidas.abs`  
**Que prueba:** Deteccion de expresiones mal formadas

**Errores incluidos:**
- `x := 10 +` - Expresion incompleta
- `y := * 5` - Operador sin operando izquierdo
- `z := 3 4` - Falta operador

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\04_expresiones_invalidas.abs
```

**Resultado esperado:** 4 errores sintacticos, exit code 1

---

#### Test 21: IF Sin THEN
**Archivo:** `tests/errores_sintacticos/05_if_sin_then_o_begin.abs`  
**Que prueba:** Deteccion de IF mal formado

**Error:** `IF condicion BEGIN` - Falta THEN

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\05_if_sin_then_o_begin.abs
```

**Resultado esperado:** Error sintactico detectado, exit code 1

---

#### Test 22: WHILE Sin DO
**Archivo:** `tests/errores_sintacticos/06_while_sin_do_o_condicion.abs`  
**Que prueba:** Deteccion de WHILE mal formado

**Error:** `WHILE condicion BEGIN` - Falta DO

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\06_while_sin_do_o_condicion.abs
```

**Resultado esperado:** Error sintactico detectado, exit code 1

---

#### Test 23: FOR Sin TO
**Archivo:** `tests/errores_sintacticos/07_for_sin_asignacion_o_to.abs`  
**Que prueba:** Deteccion de FOR mal formado

**Errores incluidos:**
- `FOR i = 1 TO 10` - Uso de `=` en vez de `:=`
- `FOR i := 1 10` - Falta TO

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\07_for_sin_asignacion_o_to.abs
```

**Resultado esperado:** 4 errores sintacticos, exit code 1

---

#### Test 24: READ/WRITE Mal Formados
**Archivo:** `tests/errores_sintacticos/08_read_write_mal_formados.abs`  
**Que prueba:** Deteccion de I/O mal formado

**Errores incluidos:**
- `READ x` - Sin parentesis
- `READ(())` - Parentesis dobles
- `WRITE;` - Sin argumentos ni parentesis

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\08_read_write_mal_formados.abs
```

**Resultado esperado:** 5 errores sintacticos, exit code 1

---

#### Test 25: Asignacion Incorrecta
**Archivo:** `tests/errores_sintacticos/09_asignacion_incorrecta.abs`  
**Que prueba:** Deteccion de asignaciones mal formadas

**Errores incluidos:**
- `x = 10` - Uso de `=` en vez de `:=`
- `y < 20` - Operador relacional en vez de asignacion

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\09_asignacion_incorrecta.abs
```

**Resultado esperado:** 2 errores sintacticos, exit code 1

---

#### Test 26: Multiples Errores con Recuperacion
**Archivo:** `tests/errores_sintacticos/10_multiples_errores_recuperacion.abs`  
**Que prueba:** Capacidad de recuperacion ante multiples errores

**Caracteristicas:**
- Contiene varios errores sintacticos
- El compilador debe continuar el analisis
- Debe reportar todos los errores encontrados

**Ejecucion manual:**
```powershell
java -cp "bin;lib\*" main.Main tests\errores_sintacticos\10_multiples_errores_recuperacion.abs
```

**Resultado esperado:** 6 errores sintacticos, exit code 1, analisis completo

---

## 7. ESTRUCTURA DEL LENGUAJE ABS

### 7.1 Estructura General

```pascal
PROGRAM NombrePrograma

// Seccion de variables globales (opcional)
VAR
    variable : TIPO;

// Declaracion de funciones (opcional)
FUNCTION NombreFuncion(parametros) : TIPO
BEGIN
    // Cuerpo
END

// Declaracion de procedimientos (opcional)
PROCEDURE NombreProcedure(parametros)
BEGIN
    // Cuerpo
END

// Bloque principal (obligatorio)
BEGIN
    // Instrucciones
END
```

---

### 7.2 Tipos de Datos

| Tipo | Descripcion | Ejemplo | Rango/Longitud |
|------|-------------|---------|----------------|
| `INT` | Numeros enteros | `42`, `-15`, `0` | -2147483648 a 2147483647 |
| `REAL` | Numeros reales | `3.14`, `-2.5` | Punto flotante |
| `STRING` | Cadenas de texto | `"Hola"` | Variable |
| `CHAR` | Caracteres | `'a'`, `'Z'` | 1 caracter |

---

### 7.3 Declaracion de Variables

**Sintaxis:**
```pascal
VAR
    identificador : TIPO;
    id1, id2, id3 : TIPO;
```

**Ejemplos:**
```pascal
VAR
    x : INT;
    a, b, c : REAL;
    mensaje : STRING;
    letra : CHAR;
```

**Reglas para identificadores:**
- Empiezan con letra (a-z, A-Z)
- Pueden contener letras y numeros
- Case-insensitive (x = X)
- No pueden ser palabras reservadas

---

### 7.4 Operadores

#### Aritmeticos
| Operador | Descripcion | Ejemplo | Resultado |
|----------|-------------|---------|-----------|
| `+` | Suma | `5 + 3` | `8` |
| `-` | Resta | `5 - 3` | `2` |
| `*` | Multiplicacion | `5 * 3` | `15` |
| `/` | Division real | `5 / 2` | `2.5` |
| `DIV` | Division entera | `5 DIV 2` | `2` |
| `MOD` | Modulo (resto) | `5 MOD 2` | `1` |
| `++` | Incremento | `x++` | `x = x + 1` |
| `--` | Decremento | `x--` | `x = x - 1` |

#### Relacionales
| Operador | Descripcion | Ejemplo |
|----------|-------------|---------|
| `=` | Igual a | `x = 5` |
| `<>` | Diferente de | `x <> 5` |
| `<` | Menor que | `x < 5` |
| `>` | Mayor que | `x > 5` |
| `<=` | Menor o igual | `x <= 5` |
| `>=` | Mayor o igual | `x >= 5` |

#### Logicos
| Operador | Descripcion | Ejemplo |
|----------|-------------|---------|
| `AND` | Y logico | `(x > 0) AND (x < 10)` |
| `OR` | O logico | `(x < 0) OR (x > 10)` |
| `NOT` | Negacion | `NOT (x = 0)` |

---

### 7.5 Estructuras de Control

#### IF-THEN-ELSE
```pascal
IF condicion THEN
    instruccion;
END

IF condicion THEN
    instruccion1;
ELSE
    instruccion2;
END

// Con bloques BEGIN-END
IF condicion THEN
BEGIN
    instruccion1;
    instruccion2;
END
ELSE
BEGIN
    instruccion3;
END
```

#### WHILE
```pascal
WHILE condicion DO
    instruccion;
END

WHILE condicion DO
BEGIN
    instruccion1;
    instruccion2;
END
```

#### FOR
```pascal
FOR variable := inicio TO fin DO
    instruccion;
END

FOR i := 1 TO 10 DO
BEGIN
    WRITE(i);
END
```

---

### 7.6 Funciones y Procedimientos

#### Funciones (con retorno)
```pascal
FUNCTION NombreFuncion(parametros) : TIPO_RETORNO
VAR
    // Variables locales
BEGIN
    // Cuerpo
    NombreFuncion := valor_retorno;
END
```

**Ejemplo:**
```pascal
FUNCTION Sumar(INT a, INT b) : INT
BEGIN
    Sumar := a + b;
END
```

#### Procedimientos (sin retorno)
```pascal
PROCEDURE NombreProcedure(parametros)
VAR
    // Variables locales
BEGIN
    // Cuerpo
END
```

**Ejemplo:**
```pascal
PROCEDURE Imprimir(STRING texto)
BEGIN
    WRITE(texto);
END
```

---

### 7.7 Entrada/Salida

#### READ
```pascal
READ();              // Lee sin guardar
READ(variable);      // Lee y guarda en variable
```

#### WRITE
```pascal
WRITE(expresion);              // Escribe una expresion
WRITE(exp1, exp2, exp3);       // Escribe multiples valores
WRITE("texto", variable);      // Mezcla texto y variables
```

---

### 7.8 Comentarios

```pascal
// Comentario de una linea

{ Comentario de
  multiples
  lineas }

(* Comentario alternativo
   de multiples lineas *)
```

---

## 8. INTERPRETACION DE RESULTADOS

### 8.1 Salida de Compilacion Exitosa

```
===================================================
           COMPILADOR ABS - ANALIZADOR
===================================================
Archivo: programa.abs

EJECUTANDO ANALISIS SINTACTICO...
(El analisis continuara a pesar de errores)
---------------------------------------------------
ANALISIS SINTACTICO COMPLETADO

============================================================
                ERRORES LEXICOS ENCONTRADOS
============================================================
No se encontraron errores lexicos

============================================================
              ERRORES SINTACTICOS ENCONTRADOS
============================================================
No se encontraron errores sintacticos

============================================================
                TOKENS ACEPTADOS
============================================================
Token                Tipo de Token             Lineas
----------------------------------------------------------
BEGIN                PALABRA RESERVADA         2
END                  PALABRA RESERVADA         3
PROGRAM              PALABRA RESERVADA         1
...

============================================================
                     RESUMEN
============================================================
Total de errores lexicos: 0
Total de errores sintacticos: 0
Total de errores: 0

ANALISIS COMPLETADO EXITOSAMENTE!
El codigo fuente es lexica y sintacticamente correcto
```

**Interpretacion:**
- Exit code: 0 (exito)
- Programa sintacticamente correcto
- Todos los tokens reconocidos

---

### 8.2 Salida con Errores Lexicos

```
============================================================
                ERRORES LEXICOS ENCONTRADOS
============================================================
Se encontraron 3 error(es) lexico(s):
------------------------------------------------------------
1. Error en linea 3, columna 5: identificador invalido, no 
   puede iniciar con un numero. Texto: 123variable

2. Error en linea 4, columna 11: identificador invalido, no 
   puede iniciar con simbolo. Texto: @

3. Error en linea 5, columna 5: identificador invalido, no 
   puede iniciar con simbolo. Texto: _invalido

============================================================
                     RESUMEN
============================================================
Total de errores lexicos: 3
Total de errores sintacticos: 0
Total de errores: 3

SE ENCONTRARON ERRORES - REVISE LOS REPORTES ANTERIORES
(El analisis completo el archivo a pesar de los errores)
```

**Interpretacion:**
- Exit code: 1 (error)
- 3 errores lexicos detectados
- Cada error muestra: linea, columna, descripcion, texto
- El analisis continuo hasta el final

---

### 8.3 Salida con Errores Sintacticos

```
============================================================
              ERRORES SINTACTICOS ENCONTRADOS
============================================================
Se encontraron 2 error(es) sintactico(s):
------------------------------------------------------------
1. Linea 3, Columna 12: Token inesperado ';'. Verifique la 
   sintaxis del programa

2. Error sintactico: Error en la estructura del programa. 
   Se esperaba 'PROGRAM nombre'

============================================================
                     RESUMEN
============================================================
Total de errores lexicos: 0
Total de errores sintacticos: 2
Total de errores: 2

SE ENCONTRARON ERRORES - REVISE LOS REPORTES ANTERIORES
```

**Interpretacion:**
- Exit code: 1 (error)
- 2 errores sintacticos detectados
- Cada error muestra ubicacion y descripcion
- Recuperacion de errores activada

---

### 8.4 Exit Codes

| Exit Code | Significado | Cuando Ocurre |
|-----------|-------------|---------------|
| 0 | Exito | No hay errores, compilacion correcta |
| 1 | Errores encontrados | Errores lexicos o sintacticos |
| 2 | Archivo no encontrado | El archivo .abs no existe |
| 3 | Error de I/O | Problema leyendo el archivo |
| 4 | Error general | Error inesperado del sistema |

**Uso en scripts:**
```powershell
java -cp "bin;lib\*" main.Main archivo.abs
if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilacion exitosa"
} else {
    Write-Host "Se encontraron errores"
}
```

---

## 9. SOLUCION DE PROBLEMAS

### 9.1 Error: "JFlex no encontrado"

**Sintoma:**
```
Error: No se encuentra lib\jflex-full-1.9.1.jar
```

**Solucion:**
1. Verificar que el archivo existe:
   ```powershell
   Test-Path lib\jflex-full-1.9.1.jar
   ```
2. Si no existe, descargar JFlex 1.9.1 desde: https://jflex.de/
3. Colocar el JAR en la carpeta `lib/`

---

### 9.2 Error: "CUP no encontrado"

**Sintoma:**
```
Error: No se encuentra lib\java-cup-11b.jar
```

**Solucion:**
1. Verificar que existen ambos archivos:
   ```powershell
   Test-Path lib\java-cup-11b.jar
   Test-Path lib\java-cup-11b-runtime.jar
   ```
2. Si no existen, descargar CUP 11b
3. Colocar ambos JARs en `lib/`

---

### 9.3 Error: "javac no reconocido"

**Sintoma:**
```
javac : No se reconoce como un comando interno o externo
```

**Solucion:**
1. Verificar instalacion de Java:
   ```powershell
   java -version
   javac -version
   ```
2. Si falla, instalar Java JDK (no solo JRE)
3. Agregar Java al PATH del sistema
4. Reiniciar PowerShell

---

### 9.4 Error: "NoClassDefFoundError"

**Sintoma:**
```
Error: Could not find or load main class main.Main
```

**Solucion:**
1. Verificar que el proyecto esta compilado:
   ```powershell
   Test-Path bin\main\Main.class
   ```
2. Si no existe, compilar:
   ```powershell
   .\compile.ps1
   ```
3. Verificar el classpath en el comando de ejecucion

---

### 9.5 Tests fallan pero compilador funciona

**Sintoma:**
```
Test 01 : Programa minimo [FAIL]
```

**Solucion:**
1. Ejecutar el test manualmente:
   ```powershell
   java -cp "bin;lib\*" main.Main tests\validos\01_programa_minimo.abs
   ```
2. Revisar el output para identificar el error
3. Verificar que el archivo de test existe
4. Verificar que el archivo esta en la ruta correcta

---

### 9.6 Caracteres extraños en la salida

**Sintoma:**
```
Analisis completado
Errores encontrados
```

**Solucion:**
Cambiar la codificacion de la consola a UTF-8:
```powershell
chcp 65001
```

Ejecutar nuevamente el comando.

---

### 9.7 Script de PowerShell no se ejecuta

**Sintoma:**
```
No se puede cargar el archivo porque la ejecucion de scripts esta deshabilitada
```

**Solucion:**
1. Abrir PowerShell como Administrador
2. Ejecutar:
   ```powershell
   Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```
3. Confirmar con 'S'
4. Cerrar y reabrir PowerShell normal

---

### 9.8 Error de memoria (OutOfMemoryError)

**Sintoma:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solucion:**
Aumentar memoria asignada a Java:
```powershell
java -Xmx512m -cp "bin;lib\*" main.Main archivo.abs
```

---

### 9.9 Recompilar todo desde cero

Si tienes problemas persistentes, recompila todo:

```powershell
# 1. Limpiar archivos generados
Remove-Item bin\*.class -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item src\lexer\Scanner.java -Force -ErrorAction SilentlyContinue
Remove-Item src\parser\Parser.java -Force -ErrorAction SilentlyContinue
Remove-Item src\parser\sym.java -Force -ErrorAction SilentlyContinue

# 2. Recompilar
.\compile.ps1

# 3. Probar
.\run_all_tests.ps1
```

---

## APENDICE A: Comandos Rapidos

### Compilacion
```powershell
.\compile.ps1
```

### Prueba Manual
```powershell
java -cp "bin;lib\*" main.Main archivo.abs
```

### Tests Automaticos
```powershell
.\run_all_tests.ps1                # Normal
.\run_all_tests.ps1 -verbose       # Con detalles
.\run_all_tests.ps1 -generateReport # Generar reporte
```

### Verificar Instalacion
```powershell
java -version
javac -version
$PSVersionTable.PSVersion
```

---

## APENDICE B: Estructura de Archivos

```
Proyecto-1-Compiladores---Escaner/
│
├── compile.ps1                 # Script de compilacion
├── run_all_tests.ps1          # Script de tests
├── programa.abs               # Programa de ejemplo
├── README.md                  # Guia rapida
├── .gitignore                 # Control de versiones
│
├── lib/                       # Librerias
│   ├── jflex-full-1.9.1.jar
│   ├── java-cup-11b.jar
│   └── java-cup-11b-runtime.jar
│
├── src/                       # Codigo fuente
│   ├── lexer/
│   │   └── Scanner.flex       # Especificacion lexica
│   ├── parser/
│   │   └── Parser.cup         # Gramatica sintactica
│   └── main/
│       └── Main.java          # Punto de entrada
│
├── bin/                       # Clases compiladas
│
├── tests/                     # Casos de prueba
│   ├── validos/              # 10 tests validos
│   ├── errores_lexicos/      # 6 tests lexicos
│   └── errores_sintacticos/  # 10 tests sintacticos
│
└── docs/                      # Documentacion
    ├── ManualUsuario.md
    └── ResultadosPruebas.md
```
---

**Fin del Manual de Usuario Completo**  
**Version:** 1.0  
**Fecha:** Noviembre 4, 2025  
**Estado:** Compilador funcional - 26/26 tests exitosos
