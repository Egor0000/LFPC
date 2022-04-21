package md.utm.isa.lab5;

import lombok.Data;
import md.utm.isa.utils.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class GrammarConverter {
    public static void fillDefinition(Grammar grammar, String input) {
        int idVt = input.indexOf("VT", 10);
        int idVn = input.indexOf("VN", 10);
        String definition = input.substring(0, idVn);
        grammar.setInitialSymbol(getStartSymbol(definition));
        grammar.setProdSymbol(getProdSymbol(definition));

        String nonterminal = input.substring(idVn, idVt);
        grammar.setNonTerminal(convertToSymbolSet(nonterminal));

        String terminal = input.substring(idVt);
        grammar.setTerminal(convertToSymbolSet(terminal));
    }

    public static void fillProduction(Grammar grammar, String input) {
        String prodSymbol = grammar.getProdSymbol();
        input = StringUtil.removeNonAscii(input);

        if (prodSymbol == null) {
            throw new RuntimeException("No definition for production set provided");
        }

//        input = input.replace(prodSymbol, " ").replaceAll("[^ A-Za-z.\\?]","");
        input = input.replace(prodSymbol, " ").replaceAll("[ 0-9={}]","");

        String[] fullProds = input.split("\\.", -1);
        List.of(fullProds).forEach(p -> {
            String[] split= p.split("\\?", -1);

            if (split.length>1) {
                split[0]= split[0].replace(" ", "");
                split[1] = split[1].trim();

                if (grammar.getProductions().containsKey(split[0])) {
                    grammar.getProductions().get(split[0]).addAll(List.of(split[1].replaceAll(" ", "")));
                } else {
                    grammar.getProductions().put(split[0],
                            new HashSet<>(List.of(split[1].replaceAll(" ", ""))));
                }
            }
        });
    }

    public static Set<String> convertToSymbolSet(String input) {
        String[] splitByFirst = input.split("\\{");
        String[] splitBySecond = new String[]{};
        if (splitByFirst.length>1) {
            splitBySecond = splitByFirst[1].split("}");
        }
        return new HashSet<>(List.of(splitBySecond[0].replaceAll(" ", "").split(",")));
    }

    public static String getStartSymbol(String input) {
        String[] splitByRight = input.split("\\)");
        return splitByRight[0].replaceAll(" ","").split(",")[3];
    }

    public static String getProdSymbol(String input) {
        String[] splitByRight = input.split("\\)");
        return splitByRight[0].replaceAll(" ","").split(",")[2];
    }

}
