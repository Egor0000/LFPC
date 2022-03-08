package md.utm.isa.lab3;

import md.utm.isa.utils.FileUtil;

public class Main {
    private static final String FILE_NAME = "src/main/resources/lab3/results.txt";

    public static void main(String[] args) throws Exception {
        Lexer lexer = new LexerImpl("lab3/data.txt");
        FileUtil.writeToFile(FILE_NAME, lexer.getPrintedResult());
    }
}
