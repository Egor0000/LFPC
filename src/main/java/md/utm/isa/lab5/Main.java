package md.utm.isa.lab5;

import md.utm.isa.utils.FileUtil;

public class Main {
    public static void main(String[] args) throws Exception {
        InputConverter inputConverter = new InputConverter("lab5/data.txt");
        try {
            Grammar grammar = inputConverter.convertInputToGrammar();
            SimplePrecedenceParser parser = new SimplePrecedenceParser(grammar);
            parser.parse(FileUtil.readRowFromFile("lab5/input.txt", 0));
        } catch (Exception ex) {
            throw ex;
        }
    }
}
