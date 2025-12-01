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

main:
    push ebp
    mov ebp, esp


; Comparacion igual (==)
    mov eax, [x]
    cmp eax, 10
    sete al
    movzx eax, al

; Suma
    mov eax, 12
    add eax, 12

; Asignacion: x := eax
    mov eax, eax
    mov [x], eax

; --- IF ---
    test eax, eax
    je else_0

; --- END IF (no ELSE) ---
else_0:
endif_1:

; Salir del programa
    mov esp, ebp
    pop ebp
    mov eax, 0
    ret

