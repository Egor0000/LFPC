package md.utm.isa.lab5;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Grammar {
    private String initialSymbol;
    private String prodSymbol;
    private Set<String> nonTerminal = new HashSet<>();
    private Set<String> terminal = new HashSet<>();
    private Map<String, Set<String>> productions = new HashMap<>();
    private Map<String, String> reversedProductions = new HashMap<>();

    public Map<String, String> getReversedProductions() {
        productions.forEach((key, val) -> {
            val.forEach(symbolString -> {
                reversedProductions.put(symbolString, key);
            });
        });
        return reversedProductions;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "nonTerminal=" + nonTerminal +
                ", terminal=" + terminal +
                '}';
    }
}
