# 📋 CASOS DE PRUEBA - COMPILADOR ABS

**Última actualización:** Noviembre 4, 2025  
**Estado:** ✅ 26/26 casos exitosos (100%)

---

## 📊 RESUMEN EJECUTIVO

| Categoría | Casos | Estado |
|-----------|-------|--------|
| ✅ **Válidos** | 10 | 100% exitosos |
| ❌ **Errores Léxicos** | 6 | 100% detectados |
| ❌ **Errores Sintácticos** | 10 | 100% detectados |
| **TOTAL** | **26** | **100% funcional** |

---

## 📁 ESTRUCTURA DE CARPETAS

```
tests/
├── validos/                    # 10 programas sintácticamente correctos
│   ├── 01_programa_minimo.abs
│   ├── 02_variables_globales.abs
│   ├── 03_funcion_simple.abs
│   ├── 04_procedure_simple.abs
│   ├── 05_expresiones_aritmeticas.abs
│   ├── 06_if_simple.abs
│   ├── 07_while_loop.abs
│   ├── 08_for_loop.abs
│   ├── 09_read_write.abs
│   └── 10_programa_completo.abs
│
├── errores_lexicos/           # 6 programas con errores léxicos
│   ├── 01_identificadores_invalidos.abs
│   ├── 02_strings_invalidos.abs
│   ├── 03_char_invalidos.abs
│   ├── 04_numeros_reales_invalidos.abs
│   ├── 05_comentarios_sin_cerrar.abs
│   └── 06_caracteres_invalidos.abs
│
├── errores_sintacticos/       # 10 programas con errores sintácticos
│   ├── 01_variables_sin_tipo.abs
│   ├── 02_funcion_sin_coma_parametros.abs
│   ├── 03_procedure_sin_begin.abs
│   ├── 04_expresiones_invalidas.abs
│   ├── 05_if_sin_then_o_begin.abs
│   ├── 06_while_sin_do_o_condicion.abs
│   ├── 07_for_sin_asignacion_o_to.abs
│   ├── 08_read_write_mal_formados.abs
│   ├── 09_asignacion_incorrecta.abs
│   └── 10_multiples_errores_recuperacion.abs ⭐ (Caso crítico)
│
└── README_CASOS_DE_PRUEBA.md  # Este archivo
```

---

## 🎯 COBERTURA DE CARACTERÍSTICAS

### ✅ **Casos Válidos** (Funcionalidades Probadas)

| # | Caso | Características |
|---|------|-----------------|
| 01 | Programa mínimo | Estructura básica PROGRAM-BEGIN-END |
| 02 | Variables globales | VAR, tipos de datos, múltiples declaraciones |
| 03 | Función simple | FUNCTION, parámetros, retorno, llamada |
| 04 | Procedure simple | PROCEDURE sin retorno, parámetros |
| 05 | Expresiones aritméticas | +, -, *, /, DIV, MOD, ++, -- |
| 06 | IF simple | IF-THEN-ELSE, condiciones booleanas |
| 07 | WHILE loop | WHILE-DO, condiciones, bloques |
| 08 | FOR loop | FOR-TO-DO, variable de control |
| 09 | READ/WRITE | Entrada/salida con parámetros |
| 10 | Programa completo | Integración de todas las características |

### ❌ **Errores Léxicos** (Detección)

| # | Caso | Errores Detectados |
|---|------|--------------------|
| 01 | Identificadores inválidos | `123var`, `nombre@`, `_invalido` |
| 02 | Strings inválidos | Sin cerrar, con saltos de línea |
| 03 | Chars inválidos | Vacíos, múltiples caracteres, sin cerrar |
| 04 | Números inválidos | `3.`, `.14`, `1.2.3` |
| 05 | Comentarios sin cerrar | `{` sin `}`, `(*` sin `*)` |
| 06 | Caracteres inválidos | `@`, `#`, `$` no permitidos |

### ❌ **Errores Sintácticos** (Detección y Recuperación)

| # | Caso | Errores Sintácticos |
|---|------|---------------------|
| 01 | Variables sin tipo | `x, y : ;` falta tipo |
| 02 | Función sin comas | `FUNCTION F(INT a b)` falta coma |
| 03 | Procedure sin BEGIN | Estructura incompleta |
| 04 | Expresiones inválidas | `10 + ;`, `* 5`, `3 4` |
| 05 | IF incompleto | Sin THEN, sin BEGIN |
| 06 | WHILE incompleto | Sin DO, sin condición |
| 07 | FOR incompleto | `=` en vez de `:=`, sin TO |
| 08 | READ/WRITE mal formados | Paréntesis, argumentos faltantes |
| 09 | Asignación incorrecta | `=` en vez de `:=` |
| 10 | Múltiples errores ⭐ | 8 errores distintos + recuperación |

---

## 🧪 EJECUCIÓN RÁPIDA

### **Desde la raíz del proyecto:**

```powershell
# Pruebas rápidas (3 casos representativos)
.\test_quick.ps1

# Todos los casos válidos (10 casos)
.\run_valid_tests.ps1

# Todos los casos con errores léxicos (6 casos)
.\run_error_tests_lexical.ps1

# Todos los casos con errores sintácticos (10 casos)
.\run_error_tests_syntactic.ps1

# TODAS las pruebas (26 casos)
.\run_all_tests.ps1
```

### **Ejecutar caso individual:**
```powershell
java -cp "bin;lib\*" main.Main tests\validos\01_programa_minimo.abs
```

---

## 📝 DOCUMENTACIÓN COMPLETA

Para ver la descripción detallada de cada caso de prueba (objetivos, código, resultados esperados):

📖 **Ver:** `docs/CasosDePrueba.md`

Este archivo contiene:
- ✅ Descripción detallada de los 26 casos
- ✅ Código fuente de cada caso
- ✅ Resultados esperados vs obtenidos
- ✅ Análisis de cobertura
- ✅ Estrategia de pruebas

---

## ✅ CRITERIOS DE VALIDACIÓN

El compilador se considera funcional si:

- ✅ Detecta TODOS los errores léxicos
- ✅ Detecta TODOS los errores sintácticos  
- ✅ Reporta línea y columna de errores
- ✅ Mensajes descriptivos y específicos
- ✅ Recuperación de errores (continúa analizando)
- ✅ Sin errores en cascada
- ✅ Programas válidos aceptados sin errores

**Estado actual:** ✅ **TODOS los criterios cumplidos**

**Nota:** Para descripciones detalladas de cada caso, consulta `docs/CasosDePrueba.md`

---

**Última actualización:** Noviembre 4, 2025  
**Versión:** 2.0 (Simplificado)  
**Tasa de éxito:** 100% (26/26 casos)
