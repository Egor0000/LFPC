package md.utm.isa.lab3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import static md.utm.isa.lab3.TokenType.*;

public class TokenMapper {
    private static final HashMap<TokenType, String> tokens;
    static {
        tokens = new HashMap<>();
        tokens.put(IF, "if");
        tokens.put(ELSE, "else");
        tokens.put(FOR, "for");
        tokens.put(WHILE, "while");
        tokens.put(RETURN, "return");
        tokens.put(LET, "let");
        tokens.put(DEF, "def");
        tokens.put(TRUE, "true");
        tokens.put(FALSE, "false");
        tokens.put(NULL, "null");
        tokens.put(OR, "or");
        tokens.put(AND, "and");
        tokens.put(LPAREN, "(");
        tokens.put(RPAREN, ")");
        tokens.put(LBRACE, "{");
        tokens.put(RBRACE, "}");
        tokens.put(LBRACK, "[");
        tokens.put(RBRACK, "]");
        tokens.put(COMMA , ",");
        tokens.put(DOT   , ".");
        tokens.put(SEMICOLON, ";");
        tokens.put(PLUS, "+");
        tokens.put(MINUS, "-");
        tokens.put(MULT, "*");
        tokens.put(DIV, "/");
        tokens.put(INC_PLUS, "++");
        tokens.put(INC_MINUS, "--");
        tokens.put(EQUAL, "=");
        tokens.put(EQUALS, "==");
        tokens.put(NOT_EQUALS, "!=");
        tokens.put(NOT, "!");
        tokens.put(LESS, "<");
        tokens.put(LESS_OR_EQUALS, "<=");
        tokens.put(GREATER, ">");
        tokens.put(GREATER_OR_EQUALS, ">=");
        tokens.put(HASH, "#");
    }

    private static final HashSet<String> singleTokenSet;
    static {
        singleTokenSet = new HashSet<>();
        singleTokenSet.add(tokens.get(LPAREN));
        singleTokenSet.add(tokens.get(RPAREN));
        singleTokenSet.add(tokens.get(LBRACE));
        singleTokenSet.add(tokens.get(RBRACE));
        singleTokenSet.add(tokens.get(LBRACK));
        singleTokenSet.add(tokens.get(RBRACK));
        singleTokenSet.add(tokens.get(COMMA));
        singleTokenSet.add(tokens.get(SEMICOLON));
        singleTokenSet.add(tokens.get(DOT));
        singleTokenSet.add(tokens.get(MULT));
        singleTokenSet.add(tokens.get(DIV));
        singleTokenSet.add(tokens.get(PLUS));
        singleTokenSet.add(tokens.get(MINUS));
        singleTokenSet.add(tokens.get(LESS));
        singleTokenSet.add(tokens.get(GREATER));
        singleTokenSet.add(tokens.get(EQUAL));
        singleTokenSet.add(tokens.get(NOT));
        singleTokenSet.add(tokens.get(HASH));
    }

    private static final HashSet<String> doubleTokenSet;
    static {
        doubleTokenSet = new HashSet<>();
        doubleTokenSet.add(tokens.get(INC_PLUS));
        doubleTokenSet.add(tokens.get(INC_MINUS));
        doubleTokenSet.add(tokens.get(EQUALS));
        doubleTokenSet.add(tokens.get(NOT_EQUALS));
        doubleTokenSet.add(tokens.get(GREATER_OR_EQUALS));
        doubleTokenSet.add(tokens.get(LESS_OR_EQUALS));
    }

    public static boolean isToken(String value){
        return tokens.containsValue(value);
    }

    public static TokenType getTokenType(String value){
        for (Map.Entry<TokenType, String> entry : tokens.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isSingleToken(String token){
        return singleTokenSet.contains(token);
    }

    public static boolean isDoubleToken(String token){
        return doubleTokenSet.contains(token);
    }
}
