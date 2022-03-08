package md.utm.isa.lab3;

public class Token {
    private TokenType tokenType;
    private String lexeme;
    private Object literal;
    private int startPos = -1;
    private int endPos = -1;

    public Token() {
    }

    public Token(TokenType tokenType, String lexeme, Object literal, int startPos, int endPos) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.literal = literal;
        this.startPos = startPos;
        this.endPos = endPos;
    }


    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public void setLiteral(Object literal) {
        this.literal = literal;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        return "Token{" +
                "\n tokenType=" + tokenType +
                ((lexeme == null)?
                ",\n lexeme=" + lexeme:
                ",\n lexeme='" + lexeme + '\'') +
                ",\n literal=" + literal +
                ",\n startPos=" + startPos +
                ",\n endPos=" + endPos +
                "}\n";
    }
}
