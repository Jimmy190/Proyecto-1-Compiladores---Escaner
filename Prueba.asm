; ============================================
; Codigo generado por el Compilador ABS
; Archivo fuente: Prueba.abs
; ============================================

; ============================================
; Codigo generado por el Compilador ABS
; Arquitectura: x86 (32-bit)
; Ensamblador: NASM
; ============================================

; ============================================
; SECCION DE DATOS
; ============================================
section .data
    newline db 10, 0        ; Salto de linea
    format_int db "%d", 10, 0  ; Formato para printf
    format_str db "%s", 10, 0  ; Formato para printf string

; ============================================
; SECCION BSS (Variables)
; ============================================
section .bss

; Variables globales
    x resd 1    ; int (4 bytes)
    i resd 1    ; int (4 bytes)
    z resd 1    ; int (4 bytes)
    j resb 256  ; string (256 bytes)

; ============================================
; SECCION DE CODIGO
; ============================================
section .text
    global main
    extern printf
    extern scanf


; Suma
    add eax, eax
main:
    push ebp
    mov ebp, esp


; Cargar literal: 10
    mov eax, 10

; Asignacion: x := eax
    mov eax, eax
    mov [x], eax

; Cargar literal: 10
    mov eax, 10

; Cargar literal: 2
    mov eax, 2

; WRITE(INT)
    push dword INT
    push format_int
    call printf
    add esp, 8

; WRITE(INT)
    push dword INT
    push format_int
    call printf
    add esp, 8

; WRITE(INT)
    push dword INT
    push format_int
    call printf
    add esp, 8

; Salir del programa
    mov esp, ebp
    pop ebp
    mov eax, 0
    ret

