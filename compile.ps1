# ================================================================
# Script de Compilacion - Compilador ABS
# ================================================================
# Este script compila completamente el proyecto:
# 1. Genera el Scanner con JFlex
# 2. Genera el Parser con CUP
# 3. Compila todo el codigo Java
# ================================================================

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "          COMPILANDO PROYECTO - COMPILADOR ABS" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

# Variables de configuracion
$PROJECT_ROOT = $PSScriptRoot
$SRC_DIR = Join-Path $PROJECT_ROOT "src"
$BIN_DIR = Join-Path $PROJECT_ROOT "bin"
$LIB_DIR = Join-Path $PROJECT_ROOT "lib"

# Archivos de entrada
$SCANNER_FLEX = Join-Path $SRC_DIR "lexer\Scanner.flex"
$PARSER_CUP = Join-Path $SRC_DIR "parser\Parser.cup"

# Herramientas
$JFLEX_JAR = Join-Path $LIB_DIR "jflex-full-1.9.1.jar"
$CUP_JAR = Join-Path $LIB_DIR "java-cup-11b.jar"
$CUP_RUNTIME_JAR = Join-Path $LIB_DIR "java-cup-11b-runtime.jar"

# Crear directorio bin si no existe
if (-Not (Test-Path $BIN_DIR)) {
    New-Item -ItemType Directory -Path $BIN_DIR | Out-Null
    Write-Host "[CREADO] Directorio bin/" -ForegroundColor Green
}

# Limpiar compilacion anterior
Write-Host "[LIMPIEZA] Eliminando archivos compilados anteriores..." -ForegroundColor Yellow
Remove-Item -Path "$BIN_DIR\*" -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "[OK] Limpieza completada" -ForegroundColor Green
Write-Host ""

# ================================================================
# PASO 1: Generar Scanner con JFlex
# ================================================================
Write-Host "PASO 1: Generando Scanner con JFlex..." -ForegroundColor Cyan
Write-Host "---------------------------------------------------------------"

if (Test-Path $SCANNER_FLEX) {
    Write-Host "Archivo fuente: $SCANNER_FLEX" -ForegroundColor Gray
    
    try {
        java -jar $JFLEX_JAR -d (Join-Path $SRC_DIR "lexer") $SCANNER_FLEX
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Scanner.java generado exitosamente" -ForegroundColor Green
        } else {
            Write-Host "[ERROR] Fallo al generar Scanner.java" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "[ERROR] Excepcion al ejecutar JFlex: $_" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "[ERROR] No se encontro Scanner.flex en $SCANNER_FLEX" -ForegroundColor Red
    exit 1
}
Write-Host ""

# ================================================================
# PASO 2: Generar Parser con CUP
# ================================================================
Write-Host "PASO 2: Generando Parser con CUP..." -ForegroundColor Cyan
Write-Host "---------------------------------------------------------------"

if (Test-Path $PARSER_CUP) {
    Write-Host "Archivo fuente: $PARSER_CUP" -ForegroundColor Gray
    
    try {
        # CUP genera Parser.java y sym.java en el directorio actual
        # Luego los movemos a src/parser/
        Push-Location (Join-Path $SRC_DIR "parser")
        
        java -jar $CUP_JAR -parser Parser -symbols sym Parser.cup
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Parser.java y sym.java generados exitosamente" -ForegroundColor Green
        } else {
            Write-Host "[ERROR] Fallo al generar Parser con CUP" -ForegroundColor Red
            Pop-Location
            exit 1
        }
        
        Pop-Location
    } catch {
        Write-Host "[ERROR] Excepcion al ejecutar CUP: $_" -ForegroundColor Red
        Pop-Location
        exit 1
    }
} else {
    Write-Host "[ERROR] No se encontro Parser.cup en $PARSER_CUP" -ForegroundColor Red
    exit 1
}
Write-Host ""

# ================================================================
# PASO 3: Compilar codigo Java
# ================================================================
Write-Host "PASO 3: Compilando codigo Java..." -ForegroundColor Cyan
Write-Host "---------------------------------------------------------------"

try {
    # Buscar todos los archivos .java en src/
    $javaFiles = Get-ChildItem -Path $SRC_DIR -Filter "*.java" -Recurse
    
    if ($javaFiles.Count -eq 0) {
        Write-Host "[ERROR] No se encontraron archivos .java para compilar" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "Archivos a compilar:" -ForegroundColor Gray
    foreach ($file in $javaFiles) {
        Write-Host "  - $($file.FullName.Replace($PROJECT_ROOT + '\', ''))" -ForegroundColor Gray
    }
    Write-Host ""
    
    # Compilar todos los archivos Java
    $classpath = "$CUP_RUNTIME_JAR"
    
    javac -encoding UTF-8 -cp $classpath -d $BIN_DIR ($javaFiles | ForEach-Object { $_.FullName })
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Compilacion Java exitosa" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Fallo en la compilacion Java" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "[ERROR] Excepcion durante compilacion: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# ================================================================
# RESUMEN
# ================================================================
Write-Host "================================================================" -ForegroundColor Green
Write-Host "          COMPILACION COMPLETADA EXITOSAMENTE" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Archivos generados:" -ForegroundColor Cyan
Write-Host "  - src/lexer/Scanner.java      (JFlex)" -ForegroundColor White
Write-Host "  - src/parser/Parser.java      (CUP)" -ForegroundColor White
Write-Host "  - src/parser/sym.java         (CUP)" -ForegroundColor White
Write-Host "  - bin/*.class                 (Compilados)" -ForegroundColor White
Write-Host ""
Write-Host "Para ejecutar el compilador:" -ForegroundColor Cyan
Write-Host "  java -cp bin;lib\* main.Main archivo.abs" -ForegroundColor Yellow
Write-Host ""
Write-Host "Para ejecutar casos de prueba:" -ForegroundColor Cyan
Write-Host "  .\run_all_tests.ps1    (Todas las pruebas)" -ForegroundColor Yellow
Write-Host ""
