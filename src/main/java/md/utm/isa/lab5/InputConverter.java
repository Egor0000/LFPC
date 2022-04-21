package md.utm.isa.lab5;

import md.utm.isa.utils.FileUtil;
import md.utm.isa.utils.StringUtil;

import java.io.File;
import java.util.Scanner;

public class InputConverter {
    private String filePath;

    public InputConverter(String filePath) {
        this.filePath = filePath;
    }

    public Grammar convertInputToGrammar()  throws Exception{
        Grammar grammar = new Grammar();
        File file;

        try {
            file = FileUtil.getFileFromResources(filePath);
        }catch (Exception ex){
            throw new Exception("No file with path " + filePath);
        }

        Scanner reader = new Scanner(file);
        int lineNr = 0;
        while (reader.hasNextLine()){
            readLine(StringUtil.removeNonAscii(reader.nextLine()), lineNr, grammar);
            lineNr++;
        }

        return grammar;
    }

    private void readLine(String line, int lineNr, Grammar grammar){
        if (lineNr == 0){
            GrammarConverter.fillDefinition(grammar, line);
        } else {
            GrammarConverter.fillProduction(grammar, line);
        }
    }
}
