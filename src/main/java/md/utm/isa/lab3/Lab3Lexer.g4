lexer grammar lab3Lexer;

// Basic keywords
IF                     : 'if';
ELSE                   : 'else';
FOR                    : 'for';
WHILE                  : 'while';
RETURN                 : 'return';
LET                    : 'let';
BOOL                   : 'true' | 'false';
DEF                    : 'def';

// Literals
STRING_LITERAL:     '"' (~["\\\r\n])* '"';

// Logical
OR                      : '||';
AND                     : '&&';

// Separators
LPAREN                  : '(';
RPAREN                  : ')';
LBRACE                  : '{';
RBRACE                  : '}';
LBRACK                  : '[';
RBRACK                  : ']';
SEMI                    : ';';
COMMA                   : ',';
DOT                     : '.';

// Operations
PLUS                    : '+';
MINUS                   : '-';
MULT                    : '*';
DIV                     : '/';
INC_PLUS                : '++';
INC_MINUS               : '--';
EQUAL                   : '=';

// Relation operators
EQUALS                  : '==';
NOT_EQUALS              : '!=';
LESS                    : '<';
LESS_OR_EQUALS          : '<=';
GREATER                 : '>';
GREATER_OR_EQUALS       : '>=';

IDENTIFIER              : ALPHA ALPHA_NUM*;

fragment DIGITS         : [0-9] ([0-9_]* [0-9])?;
fragment ALPHA          : [a-zA-Z$_] ;
fragment ALPHA_NUM      : ALPHA | [0-9];

WS                      :  [ \t\r\n\u000C]+ -> skip;
