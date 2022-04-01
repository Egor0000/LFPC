package md.utm.isa.lab4;

import md.utm.isa.utils.FileUtil;
import md.utm.isa.utils.StringUtil;

import java.io.File;
import java.util.Scanner;

public class ChomskyConverter {
    private String filePath;
    private Grammar grammar;
    private Grammar chomskyFinal;

    public ChomskyConverter(String filePath) throws Exception {
        this.filePath = filePath;
        this.grammar = convertFileToGrammar();
        eliminateEmptyProds();
        eliminateRenaming();
        eliminateNonProductive();
        eliminateInaccessibleSymbols();
        convertToChomskyGrammar();
    }

    // Convert file input to Grammar
    public Grammar convertFileToGrammar() throws Exception {
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

    // Eliminate e-productions
    public void eliminateEmptyProds(){
        // Remove Productions with empty string
        do {
            grammar.removeEmptyProductions();
            // Replace all productions that contain the removed non-terminal
            grammar.addMissingProductions();
        } while (!grammar.getEmptyProductions().isEmpty());
        // Add new production for each new production with replaced empty string
    }

    // Eliminate any renaming
    public void eliminateRenaming() throws Exception {
        do {
            grammar.eliminateRenaming();
        } while (!grammar.getRenamings().isEmpty());
    }

    // eliminate non-productive
    public void eliminateNonProductive() {
        grammar.eliminateNonProductive();
    }

    // Eliminate inaccessible symbols
    public void eliminateInaccessibleSymbols(){
        grammar.visitStates();
    }

    // Convert to Chomsky Grammar
    public void convertToChomskyGrammar(){
        grammar.convertToChomskyGrammar();
    }

    // Eliminate the non productive symbols
    // Obtain the Chomsky Normal Form

    private void readLine(String line, int lineNr, Grammar grammar){
        if (lineNr == 0){
            grammar.readDefinition(line);
        } else {
            grammar.readProduction(line);
        }
    }
}
