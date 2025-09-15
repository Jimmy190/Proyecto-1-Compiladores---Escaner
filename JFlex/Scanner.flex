/* ====== 1. Código de usuario ====== */

import java.util.*;   // para ArrayList, HashMap, etc.
import java.io.*;     // para manejar archivos

%%

%class ScannerABS
%unicode
%public
%type String
%ignorecase
%line
%column

// Palabras reservadas ABS
RESERVADA = (ABSOLUTE|AND|ARRAY|ASM|BEGIN|CASE|CONST|CONSTRUCTOR|DESTRUCTOR|EXTERNAL|DIV|DO|DOWNTO|ELSE|END|FILE|FOR|FORWARD|FUNCTION|GOTO|IF|IMPLEMENTATION|IN|INLINE|INTERFACE|INTERRUPT|LABEL|MOD|NIL|NOT|OBJECT|OF|OR|PACKED|PRIVATE|PROCEDURE|RECORD|REPEAT|SET|SHL|SHR|STRING|THEN|TO|TYPE|UNIT|UNTIL|USES|VAR|VIRTUAL|WHILE|WITH|XOR)

// Operadores
OPERADOR = \+|\-|\*|\/|DIV|MOD|NOT|AND|OR|=|<>|<|>|<=|>=|IN|,|;|\+\+|\-\-|\(|\)|\[|\]|:|\.|\^|\*\*

NumeroRealIncorrecto = \.[0-9]+|[0-9]+\.
// Comentarios
Comentario1 = \{[^}]*\}
Comentario2 = \(\*([^*]|\*[^)])*\*\)

// Literales
Octal         = 0[0-7]+
Hexadecimal   = 0[xX][0-9a-fA-F]+
Decimal       = [1-9][0-9]*|0
Exponent      = [eE][+-]?[0-9]+
RealStrict    = [0-9]+\.[0-9]+({Exponent})?
String        = \"([^\"\n])*\" 
StringIncorrecto = \"([^\"\n\r]*[\n\r][^\"]*)\"
StringSinCerrar = \"[^\n\r\"]*
CharInvalido = \'([^\'\n][^\'\n]+)\'|\'\'
CharSinCierre = \'[^\'\n\r]*
Char          = \'([^\'\n]|\\.)\' 

// Identificadores (1-127 letras/dígitos, inicia con letra, no palabra reservada)
// Identificador válido: empieza con letra, sigue con letras/dígitos
Identificador = [a-zA-Z][a-zA-Z0-9]{0,126}
// Identificador inválido que comienza con número
IdentificadorNumero = [0-9][a-zA-Z0-9]*
// Identificador inválido con caracteres no permitidos (pero SIN espacios)
IdentificadorInvalido = [a-zA-Z][a-zA-Z0-9]*[^a-zA-Z0-9 \t\n\r]+[a-zA-Z0-9]+



%{
// Lista para guardar errores léxicos
private ArrayList<String> errores = new ArrayList<>();

public ArrayList<String> getErrores() {
    return errores;
}

// Mapa para tokens aceptados
private HashMap<String, TokenInfo> tokensAceptados = new HashMap<>();

// Clase auxiliar para almacenar info de token
private static class TokenInfo {
    String tipo;
    HashMap<Integer, Integer> lineas = new HashMap<>();

    TokenInfo(String tipo) {
        this.tipo = tipo;
    }

    void agregarLinea(int linea) {
        lineas.put(linea, lineas.getOrDefault(linea, 0) + 1);
    }
}

// Método para registrar tokens válidos
private void registrarToken(String token, String tipo) {
    token = token.toUpperCase(); 
    TokenInfo info = tokensAceptados.get(token);
    if(info == null) {
        info = new TokenInfo(tipo);
        tokensAceptados.put(token, info);
    }
    info.agregarLinea(yyline + 1); 
}

// Método para mostrar tabla al final
public void imprimirTokens() {
    System.out.printf("%-20s %-25s %s\n", "Token", "Tipo de Token", "Líneas");
    System.out.println("----------------------------------------------------------");
    List<String> claves = new ArrayList<>(tokensAceptados.keySet());
    Collections.sort(claves); // orden alfabético
    for(String token : claves) {
        TokenInfo info = tokensAceptados.get(token);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Integer, Integer> e : info.lineas.entrySet()) {
            sb.append(e.getKey());
            if(e.getValue() > 1) sb.append("(").append(e.getValue()).append(")");
            sb.append(", ");
        }
        if(sb.length() >= 2) sb.setLength(sb.length() - 2); // quitar última coma
        System.out.printf("%-20s %-25s %s\n", token, info.tipo, sb.toString());
    }
}
%}

%%

// Omitir comentarios
{Comentario1}        { /* Ignorar comentario tipo { } */ }
{Comentario2}        { /* Ignorar comentario tipo (* *) */ }

// Ignorar espacios y saltos de línea
[ \t\n\r]+           { /* Ignorar */ }

// Palabras reservadas
// Operadores
{OPERADOR} {
    registrarToken(yytext(), "OPERADOR");
}

{RESERVADA} {
    registrarToken(yytext(), "PALABRA RESERVADA");
}

{NumeroRealIncorrecto} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": número real incorrecto. Texto: " + yytext());
}

// Literales
{Octal} {
    registrarToken(yytext(), "LITERAL OCTAL");
}

{Hexadecimal} {
    registrarToken(yytext(), "LITERAL HEXADECIMAL");
}

{Decimal} {
    registrarToken(yytext(), "LITERAL ENTERO");
}

{RealStrict} {
    registrarToken(yytext(), "LITERAL REAL");
}
{StringSinCerrar} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": string sin cerrar. Texto: " + yytext());
}
{StringIncorrecto} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": string incorrecto. Texto: " + yytext());
}
{String} {
    registrarToken(yytext(), "LITERAL STRING");
}
{CharInvalido} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": carácter inválido. Texto: " + yytext());
}
{CharSinCierre} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": carácter sin cerrar. Texto: " + yytext());
}
{Char} {
    registrarToken(yytext(), "LITERAL CARACTER");
}


// Identificadores (que no sean palabra reservada)
{IdentificadorNumero} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": identificador inválido, no puede iniciar con un número. Texto: " + yytext());
}
{IdentificadorInvalido} {
    errores.add("Error en línea " + (yyline+1) +
                ", columna " + (yycolumn+1) +
                ": identificador inválido, solo se permiten letras y dígitos. Texto: " + yytext());
}
{Identificador} {
    registrarToken(yytext(), "IDENTIFICADOR");
}

// Cualquier otro carácter inválido
. {
   errores.add("Error en línea " + (yyline+1) +
               ", columna " + (yycolumn+1) +
               ": " + yytext());
}

