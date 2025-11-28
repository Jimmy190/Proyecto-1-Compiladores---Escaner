# I.T.C.R. Compiladores & Intérpretes^

### Escuela de Computación Análisis Semántico y Generador de Código

### Prof: Ing. Erika Marín Schumann. II Semestre del 20 25

## Definición general

Esta última etapa del proyecto conocida formalmente como Generador de código es, si se quiere la más significativa del
proyecto, pues reúne las etapas anteriores y termina el compilador
Para esta etapa del proyecto se debe entregar un programa que reciba un código fuente escrito en ABS y realice el análisis
léxico, sintáctico, semántico y genere código ensamblador. Para esto se debe utilizar la definición del lenguaje aceptado
realizado en la primera etapa del proyecto junto con la gramática diseñada en la segunda parte del proyecto
Se espera que la traducción del código se lleve a cabo mediante la Traducción dirigida por Sintaxis, por lo que se
recomienda seguir los ejemplos vistos en clases con lo relacionado a las acciones semánticas y los registros semánticos
correspondientes.
Al finalizar el análisis el programa deberá desplegarle al usuario el resultado del Análisis léxico, sintáctico y semántico que
se efectúo. Se espera que despliegue:

**1. Listado de errores léxicos encontrados:** El programa debe desplegar una lista de todos los errores léxicos
que se encontraron en el código fuente. Debe desplegar la línea en la que se encontró el error. Es importante
que el programa deba poder recuperarse del error y no desplegar los errores en cascada ni terminar de hacer el
scaneo al encontrar el primer error.
**2. Listado de errores sintácticos encontrados:** El programa debe desplegar una lista de todos los errores
sintácticos que se encontraron en el código fuente. Debe desplegar la línea en la que se encontró el error.
Además, el mensaje de error debe ser lo más específico posible, con el fin de que el programador pueda llegar al
error y corregirlo de forma eficiente. Es importante que el programa deba recuperarse del error y evitar no
desplegar errores en cascada ni terminar de hacer el parseo al encontrar el primer error.
**3. Listado de errores semánticos encontrados:** El programa debe desplegar una lista de todos los errores
semánticos que se encontraron en el código fuente. Debe desplegar la línea en la que se encontró el error.
Además el mensaje de error debe ser lo más especifico posible, con el fin de que el programador pueda llegar al
error y corregirlo de forma eficiente. Recuerde que en el momento en que ocurren errores no es necesario que
continúe traduciendo pero si debe continuar analizando el código tanto en su léxico, como en la sintaxis y en la
semántica.
**4. Contenido de Tabla de Símbolos:** Desplegar el contenido de la tabla de símbolos una vez concluido el
análisis. Debe especificar el nombre, tipo y ámbito de variables globales, funciones y variables locales y
parámetros.

```
5. Archivo con Código Ensamblador: Si no ocurrieron errores durante la compilación el programa debe
generar un archivo con el mismo nombre del que se compile (cambie la extensión!!). Este archivo contendrá la
traducción de ciertas partes del programa solo que en lenguaje ensamblador. Puede generar Ensamblador para
Windows o para Linux.
```
## Descripción Detallada

Se les sugiere tomar en cuenta los siguientes aspectos para asegurar la completítud del programa.


#### ANALISIS SEMANTICO

- Deben realizar análisis Semántico de los siguientes casos.
    o Variables no definidas
    o Variables doblemente definidas
    o Funciones no definidas
    o Corroborar cantidad y tipo de parámetros.
    o Implementar el Constant Folding

#### TRADUCCION DE CODIGO

- Deben realizar la traducción de código de los siguientes casos.

```
o Declaración de variables globales
o Solo código del Main.
o Asignación
o Expresiones binarias aritméticas: Suma, resta, incremento, decremento
o Expresiones binarias booleana: Igual
o If-else
o Write(x) -> traducirlo al código para imprimir x en pantalla
```
- Debe implementar la Tabla de Símbolos (no se complique en la estructura de datos a utilizar) y la Pila semántica

## Aspectos Administrativos

- Los grupos deben permanecer iguales a las otras etapas del proyecto.
- El trabajo se debe de entregar el día 8 de Diciembre de 2025 antes de 8am. Deben subirlo al tec digital.
- Recuerde que oficialmente no se recibirán trabajos con entrega tardía.
- Las revisiones serán el mismo día


