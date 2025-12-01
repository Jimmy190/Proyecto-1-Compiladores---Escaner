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
    _strlit_0 db "Hola Mundo", 0
    _strlit_1 db "hola", 0

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


; Suma
    mov eax, 10
    add eax, 12

; Asignacion: x := eax
    mov eax, eax
    mov [x], eax

; Resta
    mov eax, 50
    sub eax, 30

; Asignacion: z := eax
    mov eax, eax
    mov [z], eax

; Asignacion: i := 120
    mov eax, 120
    mov [i], eax

; Asignacion: j := "Hola Mundo"
    mov dword [j], _strlit_0

; Incremento: i++
    inc dword [i]

; Decremento: x--
    dec dword [x]

; WRITE(12)
    push dword 12
    push dword format_int
    call printf
    add esp, 8

; WRITE(123)
    push dword 123
    push dword format_int
    call printf
    add esp, 8

; WRITE("hola")
    push dword _strlit_1
    push dword format_str
    call printf
    add esp, 8

; WRITE([x])
    push dword [x]
    push dword format_int
    call printf
    add esp, 8

; WRITE([j])
    push dword [j]
    push dword format_str
    call printf
    add esp, 8

; Salir del programa
    mov esp, ebp
    pop ebp
    mov eax, 0
    ret

