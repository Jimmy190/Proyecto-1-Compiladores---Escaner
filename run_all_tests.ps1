# Script para ejecutar TODOS los casos de prueba (26 tests)
# Ejecuta: .\run_all_tests.ps1

param(
    [switch]$verbose = $false,
    [switch]$generateReport = $false
)

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "EJECUTANDO TODOS LOS CASOS DE PRUEBA" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$totalTests = 0
$passedTests = 0
$failedTests = 0
$testResults = @()

# Funcion para ejecutar un test
function Run-Test {
    param(
        $testFile, 
        $testNumber, 
        $testName, 
        $expectedResult,
        $category
    )
    
    $script:totalTests++
    Write-Host "Test $testNumber : $testName" -NoNewline
    
    # Capturar salida completa
    $output = java -cp "bin;lib/java-cup-11b-runtime.jar" main.Main "tests/$testFile" 2>&1 | Out-String
    $exitCode = $LASTEXITCODE
    
    # Determinar resultado
    $passed = $false
    $resultDescription = ""
    
    if ($category -eq "valid") {
        # Casos validos: deben compilar exitosamente (exit code 0)
        if ($exitCode -eq 0) {
            Write-Host " [OK]" -ForegroundColor Green
            $passed = $true
            $resultDescription = "Compilacion exitosa sin errores"
        } else {
            Write-Host " [FAIL]" -ForegroundColor Red
            $resultDescription = "ERROR: Deberia compilar pero fallo"
        }
    } else {
        # Casos de error: deben detectar errores (exit code != 0)
        if ($exitCode -ne 0) {
            Write-Host " [OK]" -ForegroundColor Green
            $passed = $true
            # Extraer mensaje de error del output
            $errorLines = $output -split "`n" | Where-Object { $_ -match "Error|error|ERROR" }
            if ($errorLines) {
                $resultDescription = ($errorLines | Select-Object -First 3) -join " | "
            } else {
                $resultDescription = "Error detectado correctamente"
            }
        } else {
            Write-Host " [FAIL]" -ForegroundColor Red
            $resultDescription = "ERROR: Deberia detectar error pero compilo exitosamente"
        }
    }
    
    # Mostrar detalles si es verbose
    if ($verbose -and $output.Trim()) {
        Write-Host "   Output:" -ForegroundColor Gray
        $output -split "`n" | ForEach-Object { 
            if ($_.Trim()) { Write-Host "     $_" -ForegroundColor DarkGray }
        }
    }
    
    # Guardar resultado para el reporte
    $script:testResults += [PSCustomObject]@{
        Number = $testNumber
        File = $testFile
        Name = $testName
        Category = $category
        Expected = $expectedResult
        Passed = $passed
        Output = $output.Trim()
        Result = $resultDescription
    }
    
    if ($passed) {
        $script:passedTests++
    } else {
        $script:failedTests++
    }
    
    return $passed
}

Write-Host "=== CASOS VALIDOS (deben compilar sin errores) ===" -ForegroundColor Yellow
Write-Host ""

# Tests validos (10 tests)
Run-Test "validos/01_programa_minimo.abs" "01" "Programa minimo" "Compilacion exitosa" "valid"
Run-Test "validos/02_variables_globales.abs" "02" "Variables globales" "Compilacion exitosa" "valid"
Run-Test "validos/03_funcion_simple.abs" "03" "Funcion simple" "Compilacion exitosa" "valid"
Run-Test "validos/04_procedure_simple.abs" "04" "Procedimiento simple" "Compilacion exitosa" "valid"
Run-Test "validos/05_expresiones_aritmeticas.abs" "05" "Expresiones aritmeticas" "Compilacion exitosa" "valid"
Run-Test "validos/06_if_simple.abs" "06" "Estructura IF" "Compilacion exitosa" "valid"
Run-Test "validos/07_while_loop.abs" "07" "Bucle WHILE" "Compilacion exitosa" "valid"
Run-Test "validos/08_for_loop.abs" "08" "Bucle FOR" "Compilacion exitosa" "valid"
Run-Test "validos/09_read_write.abs" "09" "Operaciones READ/WRITE" "Compilacion exitosa" "valid"
Run-Test "validos/10_programa_completo.abs" "10" "Programa completo" "Compilacion exitosa" "valid"

Write-Host ""
Write-Host "=== ERRORES LEXICOS (deben detectar errores lexicos) ===" -ForegroundColor Yellow
Write-Host ""

# Tests de errores lexicos (6 tests)
Run-Test "errores_lexicos/01_identificadores_invalidos.abs" "11" "Identificadores invalidos" "Error lexico: Identificador invalido" "error_lex"
Run-Test "errores_lexicos/02_strings_invalidos.abs" "12" "Strings invalidos" "Error lexico: String sin cerrar" "error_lex"
Run-Test "errores_lexicos/03_char_invalidos.abs" "13" "Caracteres invalidos" "Error lexico: Caracter invalido" "error_lex"
Run-Test "errores_lexicos/04_numeros_reales_invalidos.abs" "14" "Numeros reales invalidos" "Error lexico: Numero invalido" "error_lex"
Run-Test "errores_lexicos/05_comentarios_sin_cerrar.abs" "15" "Comentarios sin cerrar" "Error lexico: Comentario sin cerrar" "error_lex"
Run-Test "errores_lexicos/06_caracteres_invalidos.abs" "16" "Caracteres no permitidos" "Error lexico: Caracter invalido" "error_lex"

Write-Host ""
Write-Host "=== ERRORES SINTACTICOS (deben detectar errores sintacticos) ===" -ForegroundColor Yellow
Write-Host ""

# Tests de errores sintacticos (10 tests)
Run-Test "errores_sintacticos/01_variables_sin_tipo.abs" "17" "Variables sin tipo" "Error sintactico: Tipo esperado" "error_syn"
Run-Test "errores_sintacticos/02_funcion_sin_coma_parametros.abs" "18" "Funcion sin coma en parametros" "Error sintactico: Coma esperada" "error_syn"
Run-Test "errores_sintacticos/03_procedure_sin_begin.abs" "19" "Procedimiento sin BEGIN" "Error sintactico: BEGIN esperado" "error_syn"
Run-Test "errores_sintacticos/04_expresiones_invalidas.abs" "20" "Expresiones invalidas" "Error sintactico: Expresion invalida" "error_syn"
Run-Test "errores_sintacticos/05_if_sin_then_o_begin.abs" "21" "IF sin THEN" "Error sintactico: THEN esperado" "error_syn"
Run-Test "errores_sintacticos/06_while_sin_do_o_condicion.abs" "22" "WHILE sin DO" "Error sintactico: DO esperado" "error_syn"
Run-Test "errores_sintacticos/07_for_sin_asignacion_o_to.abs" "23" "FOR sin TO" "Error sintactico: TO esperado" "error_syn"
Run-Test "errores_sintacticos/08_read_write_mal_formados.abs" "24" "READ/WRITE mal formados" "Error sintactico: Sintaxis invalida" "error_syn"
Run-Test "errores_sintacticos/09_asignacion_incorrecta.abs" "25" "Asignacion incorrecta" "Error sintactico: := esperado" "error_syn"
Run-Test "errores_sintacticos/10_multiples_errores_recuperacion.abs" "26" "Multiples errores con recuperacion" "Errores sintacticos multiples" "error_syn"

# Resultados finales
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "RESUMEN DE RESULTADOS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Total de tests:  $totalTests" -ForegroundColor White
Write-Host "Tests exitosos:  $passedTests" -ForegroundColor Green
Write-Host "Tests fallidos:  $failedTests" -ForegroundColor $(if ($failedTests -eq 0) { "Green" } else { "Red" })

$percentage = [math]::Round(($passedTests / $totalTests) * 100, 2)
Write-Host "Porcentaje:      $percentage%" -ForegroundColor $(if ($percentage -eq 100) { "Green" } else { "Yellow" })
Write-Host "========================================`n" -ForegroundColor Cyan

# Generar reporte si se solicita
if ($generateReport) {
    Write-Host "Generando reporte de resultados..." -ForegroundColor Cyan
    
    $reportPath = "docs/ResultadosPruebas.md"
    $reportContent = @"
# Resultados de Casos de Prueba - Compilador ABS
**Fecha de ejecucion:** $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")  
**Total de tests:** $totalTests  
**Tests exitosos:** $passedTests  
**Tests fallidos:** $failedTests  
**Tasa de exito:** $percentage%

---

## 1. CASOS VALIDOS (Programas Correctos)

Estos casos deben compilar exitosamente sin errores.

| # | Archivo | Descripcion | Esperado | Obtenido | Estado |
|---|---------|-------------|----------|----------|--------|
"@
    
    $testResults | Where-Object { $_.Category -eq "valid" } | ForEach-Object {
        $status = if ($_.Passed) { "✅ OK" } else { "❌ FAIL" }
        $reportContent += "| $($_.Number) | ``$($_.File)`` | $($_.Name) | $($_.Expected) | $($_.Result) | $status |`n"
    }
    
    $reportContent += @"

## 2. ERRORES LEXICOS

Estos casos deben detectar errores en el analisis lexico.

| # | Archivo | Descripcion | Esperado | Obtenido | Estado |
|---|---------|-------------|----------|----------|--------|
"@
    
    $testResults | Where-Object { $_.Category -eq "error_lex" } | ForEach-Object {
        $status = if ($_.Passed) { "✅ OK" } else { "❌ FAIL" }
        $reportContent += "| $($_.Number) | ``$($_.File)`` | $($_.Name) | $($_.Expected) | $($_.Result) | $status |`n"
    }
    
    $reportContent += @"

## 3. ERRORES SINTACTICOS

Estos casos deben detectar errores en el analisis sintactico.

| # | Archivo | Descripcion | Esperado | Obtenido | Estado |
|---|---------|-------------|----------|----------|--------|
"@
    
    $testResults | Where-Object { $_.Category -eq "error_syn" } | ForEach-Object {
        $status = if ($_.Passed) { "✅ OK" } else { "❌ FAIL" }
        $reportContent += "| $($_.Number) | ``$($_.File)`` | $($_.Name) | $($_.Expected) | $($_.Result) | $status |`n"
    }
    
    $reportContent += @"

---

## DETALLES DE OUTPUTS

### Casos Validos
"@
    
    $testResults | Where-Object { $_.Category -eq "valid" } | ForEach-Object {
        $reportContent += @"

#### Test $($_.Number): $($_.Name)
**Archivo:** ``$($_.File)``  
**Resultado esperado:** $($_.Expected)  
**Resultado obtenido:** $($_.Result)  
``````
$($_.Output)
``````

"@
    }
    
    $reportContent += @"

### Errores Lexicos
"@
    
    $testResults | Where-Object { $_.Category -eq "error_lex" } | ForEach-Object {
        $reportContent += @"

#### Test $($_.Number): $($_.Name)
**Archivo:** ``$($_.File)``  
**Resultado esperado:** $($_.Expected)  
**Resultado obtenido:** $($_.Result)  
``````
$($_.Output)
``````

"@
    }
    
    $reportContent += @"

### Errores Sintacticos
"@
    
    $testResults | Where-Object { $_.Category -eq "error_syn" } | ForEach-Object {
        $reportContent += @"

#### Test $($_.Number): $($_.Name)
**Archivo:** ``$($_.File)``  
**Resultado esperado:** $($_.Expected)  
**Resultado obtenido:** $($_.Result)  
``````
$($_.Output)
``````

"@
    }
    
    # Guardar reporte
    $reportContent | Out-File -FilePath $reportPath -Encoding UTF8
    Write-Host "Reporte generado: $reportPath" -ForegroundColor Green
}

if ($failedTests -eq 0) {
    Write-Host "TODOS LOS TESTS PASARON EXITOSAMENTE!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Para ver resultados detallados, ejecuta:" -ForegroundColor Cyan
    Write-Host "  .\run_all_tests.ps1 -verbose" -ForegroundColor Yellow
    Write-Host "Para generar reporte completo, ejecuta:" -ForegroundColor Cyan
    Write-Host "  .\run_all_tests.ps1 -generateReport" -ForegroundColor Yellow
    Write-Host ""
    exit 0
} else {
    Write-Host "ALGUNOS TESTS FALLARON" -ForegroundColor Red
    exit 1
}
