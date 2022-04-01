grammar Lab3Grammar;

program         : declaration* EOF;

declaration     : funDecl | varDecl | statement;
funDecl         : 'def' IDENTIFIER '(' params? ')'
                    '{'
                        declaration*
                    '}';
varDecl         : 'let' IDENTIFIER ('=' expression)? ';';

statement       : block | exprStmt | for | while | if | return ;
exprStmt        : expression ';';
expression      :  call | rightEql| assignment | logic_or | arrDecl | incr;

assignment      : IDENTIFIER '=' assignment | logic_or | rightEql  ;

logic_or        : logic_and ( '||' logic_and )* ;
logic_and       : equality ( 'and' equality )* ;
equality        : comparison ( ( '!=' | '==' ) comparison )* ;
comparison      : term ( ( '>' | '>=' | '<' | '<=' ) term )* ;
term            : factor ( ( '-' | '+' ) factor )* ;
factor          : unary ( ( '/' | '*' ) unary )* ;
arrDecl         : '['rightEql*']';

unary           : ( '!' | '-' ) unary | call | IDENTIFIER ;

block           : '{' declaration* '}';

incr            : IDENTIFIER ('++' | '--');

// Statements
for             : 'for' '(' ( varDecl | exprStmt | ';' )
                           expression? ';'
                           expression? ')' statement ;
while           : 'while' '(' expression? ')' statement ;
if              : 'if' '(' expression ')' statement
                 ( 'else' statement )? ;
return          : 'return' expression? ';' ;



IF 	            : 	'if';
ELSE            :   'else';
FOR             :   'for';
WHILE 	        : 	'while' ;
RETURN          : 	'return';
BREAK 	        : 	'break';
// Function call
call            : rightEql ( '(' arguments? ')' | '.' IDENTIFIER )*;
params          : IDENTIFIER (',' IDENTIFIER)*;
arguments       : expression ( ',' expression )* ;
rightEql        : NUMBER | STRING | BOOL | 'null' | IDENTIFIER | '(' expression ')';
string          : STRING;

NUMBER          : DIGIT+ ( '.' DIGIT+ )? ;
STRING          : '"' ~('"')* '"' ;
IDENTIFIER      : ALPHA ( ALPHA | DIGIT )* ('['(NUMBER | IDENTIFIER)*']')*;


MUL             : '*';
DIV             : '/';
ADD             : '+';
SUB             : '-';
BOOL            : 'true' | 'false';

// Relation operators
EQUALS                  : '==';
NOT_EQUALS              : '!=';
LESS                    : '<';
LESS_OR_EQUALS          : '<=';
GREATER                 : '>';
GREATER_OR_EQUALS       : '>=';

 ALPHA  : [a-zA-Z_] ;
 DIGIT  : [0-9];
WS              : [ \t\u000C\r\n]+ -> skip;