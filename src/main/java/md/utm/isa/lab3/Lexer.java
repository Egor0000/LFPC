package md.utm.isa.lab3;

import java.util.List;

public interface Lexer {
    List<Token> analyze(String source);

    String getPrintedResult();
}
