grammar Directives;

// Parser Rules
directive
    : IDENTIFIER args? EOF
    ;

args
    : value+
    ;

value
    : IDENTIFIER
    | STRING
    | NUMBER
    | BYTE_SIZE
    | TIME_DURATION
    ;

// Lexer Rules
BYTE_SIZE
    : DIGIT+ ('.' DIGIT+)? BYTE_UNIT
    ;

TIME_DURATION
    : DIGIT+ ('.' DIGIT+)? TIME_UNIT
    ;

fragment BYTE_UNIT
    : [Kk]'B'
    | [Mm]'B'
    | [Gg]'B'
    | [Tt]'B'
    | [Pp]'B'
    | 'B'
    ;

fragment TIME_UNIT
    : 'ns'
    | 'ms'
    | 's'
    | 'm'
    | 'h'
    ;

IDENTIFIER
    : [a-zA-Z_] [a-zA-Z0-9_]*
    ;

STRING
    : '\'' (~[''])* '\''
    | '"' (~[""])* '"'
    ;

NUMBER
    : DIGIT+ ('.' DIGIT+)?
    ;

fragment DIGIT
    : [0-9]
    ;

WS
    : [ \t\r\n]+ -> skip
    ; 