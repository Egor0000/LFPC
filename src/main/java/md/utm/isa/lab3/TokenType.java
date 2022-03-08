package md.utm.isa.lab3;

public enum TokenType {
    // Basic keywords
    IF,
    ELSE,
    FOR,
    WHILE,
    RETURN,
    LET,
    TRUE,
    FALSE,
    DEF,
    NULL,

    // Literals
    STRING_LITERAL,
    NUMBER_LITERAL,

// Logical
    OR,
    AND,
    // Separators
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    COMMA ,
    DOT   ,
    SEMICOLON,
    NOT   ,
    HASH,

    // Operations
    PLUS,
    MINUS,
    MULT,
    DIV,
    INC_PLUS,
    INC_MINUS,
    EQUAL,

// Relation operators
    EQUALS,
    NOT_EQUALS,
    LESS,
    LESS_OR_EQUALS,
    GREATER,
    GREATER_OR_EQUALS,

    IDENTIFIER,
}
