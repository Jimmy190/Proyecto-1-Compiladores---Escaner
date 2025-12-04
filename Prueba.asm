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
    _strlit_0 db "No existe", 0

; ============================================
; SECCION BSS (Variables)
; ============================================
section .bss

; Variables globales
    x resd 1    ; int (4 bytes)
    y resd 1    ; int (4 bytes)
    soystring resb 256  ; string (256 bytes)

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


; Asignacion: x := 5
    mov eax, 5
    mov [x], eax

; Asignacion: y := 5
    mov eax, 5
    mov [y], eax

; Asignacion: x := 15
    mov eax, 15
    mov [x], eax

; Comparacion igual (==)
    mov eax, [x]
    cmp eax, 15
    sete al
    movzx eax, al

; --- IF ---
    test eax, eax
    je else_0

; WRITE([soystring])
    push dword [soystring]
    push dword format_str
    call printf
    add esp, 8

; --- ELSE ---
    jmp endif_1
else_0:

; WRITE("No existe")
    push dword _strlit_0
    push dword format_str
    call printf
    add esp, 8

; --- END IF/ELSE ---
endif_1:

; Salir del programa
    mov esp, ebp
    pop ebp
    mov eax, 0
    ret

