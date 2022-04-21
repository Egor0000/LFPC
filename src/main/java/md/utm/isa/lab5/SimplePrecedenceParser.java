package md.utm.isa.lab5;

import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class SimplePrecedenceParser {
    private final Grammar grammar;

    private Map<String, Set<String>> first = new HashMap<>();
    private Map<String, Set<String>> last = new HashMap<>();
    private Map<Map.Entry<String, String>, Set<String>> precedenceTable = new HashMap<>();
    private Set<String> entries = new HashSet<>();
    private Map<String, String> reversedProduction = new HashMap<>();

    private List<String> stack = new ArrayList<>();
    private String input;
    private boolean accept = false;

    public SimplePrecedenceParser(Grammar grammar) throws Exception {
        this.grammar = grammar;
        fillFirstLast();
        initPrecedenceTable();

        samePivot();
        foundPivot();
        potentialPivot();


    }

    public void parse(String input) {
        try {
            this.accept = parseInput(input);
            printConsoleTable();
            printStack();
            System.out.println("\nACCEPT: " + this.accept);
        } catch (Exception ex) {
            printConsoleTable();
            printStack();
            System.out.println("ACCEPT: " + this.accept + "\nCause: " + ex.getMessage());
        }
    }

    /*
    Fill first last sets
     */
    private void fillFirstLast() {
        for (String leftSymbol: grammar.getProductions().keySet()) {
            fillRecursivelyFirst(leftSymbol);
        }

        for (String leftSymbol: grammar.getProductions().keySet()) {
            fillRecursivelyLast(leftSymbol);
        }
    }

    private void fillRecursivelyFirst(String left) {
        Set<String> right = grammar.getProductions().get(left);
        if (right == null || right.size() == 0) {
            return;
        }

        if (!first.containsKey(left)) {
            first.put(left, new HashSet<String>());
        }

        for (String symbolString: right) {
            if (symbolString.length() == 0) {
                continue;
            }
            String firstSymbol = String.valueOf(symbolString.toCharArray()[0]);

            if (left.equals(firstSymbol)) {
                first.get(left).add(firstSymbol);
                continue;
            }
            if (grammar.getTerminal().contains(firstSymbol)) {
                first.get(left).add(firstSymbol);
            } else if (grammar.getNonTerminal().contains(firstSymbol)) {
                first.get(left).add(firstSymbol);
                fillRecursivelyFirst(firstSymbol);
                first.get(left).addAll(first.get(firstSymbol));
            }
        }
    }

    private void fillRecursivelyLast(String left) {
        Set<String> right = grammar.getProductions().get(left);
        if (right == null || right.size() == 0) {
            return;
        }

        if (!last.containsKey(left)) {
            last.put(left, new HashSet<String>());
        }

        for (String symbolString: right) {
            if (symbolString.length() == 0) {
                continue;
            }
            char[] charSymbols = symbolString.toCharArray();
            String lastSymbol = String.valueOf(charSymbols[charSymbols.length-1]);

            if (left.equals(lastSymbol)) {
                last.get(left).add(lastSymbol);
                continue;
            }
            if (grammar.getTerminal().contains(lastSymbol)) {
                last.get(left).add(lastSymbol);
            } else if (grammar.getNonTerminal().contains(lastSymbol)) {
                last.get(left).add(lastSymbol);
                fillRecursivelyLast(lastSymbol);
                last.get(left).addAll(last.get(lastSymbol));
            }
        }
    }


    /*
    Build precedence matrix
     */
    private void initPrecedenceTable(){
        Set<String> entries = new HashSet<>();
        entries.addAll(grammar.getNonTerminal());
        entries.addAll(grammar.getTerminal());
        entries.add("$");
        this.entries = new HashSet<>(entries);

        for (String row: entries) {
            for (String column: entries) {
                precedenceTable.put(new AbstractMap.SimpleEntry<>(row, column), new HashSet<>());
            }
        }
    }

    private void samePivot() {
        grammar.getProductions().forEach((left, value) -> {
            value.forEach(right -> {
                if (right.length()>1) {
                    char[] symbolArray = right.toCharArray();
                    for (int  i=0; i< symbolArray.length-1; i++) {
                        precedenceTable.get(new AbstractMap.SimpleEntry<>(String.valueOf(symbolArray[i]),
                                String.valueOf(symbolArray[i+1]))).add("=");
                    }
                }
            });
        });
    }

    private void foundPivot() {
        grammar.getProductions().forEach((left, value) -> {
            value.forEach(right -> {
                if (right.length()>1) {
                    char[] symbolArray = right.toCharArray();

                    for (int  i=0; i< symbolArray.length-1; i++) {
                        String l = String.valueOf(symbolArray[i]);
                        String r = String.valueOf(symbolArray[i+1]);

                        // case VnVt
                        if (grammar.getNonTerminal().contains(l) && grammar.getTerminal().contains(r)) {
                            for (String last: last.get(l)) {
                                precedenceTable.get(new AbstractMap.SimpleEntry<>(last,
                                r)).add(">");
                            }
                        }

                        // case VnVn
                        if (grammar.getNonTerminal().contains(l) && grammar.getNonTerminal().contains(r)) {
                            for (String last: last.get(l)) {
                                for (String first: first.get(r)) {
                                    if (grammar.getTerminal().contains(first)) {
                                        precedenceTable.get(new AbstractMap.SimpleEntry<>(last,
                                                first)).add(">");
                                    }
                                }
                            }
                        }

                    }
                }
            });
        });
    }

    private void potentialPivot() {
        grammar.getProductions().forEach((left, value) -> {
            value.forEach(right -> {
                if (right.length() > 1) {
                    char[] symbolArray = right.toCharArray();
                    for (int  i =0; i<symbolArray.length-1; i++) {
                        String l = String.valueOf(symbolArray[i]);
                        String r = String.valueOf(symbolArray[i+1]);

                        if (grammar.getNonTerminal().contains(r)) {
                            for (String first: first.get(r)) {
                                precedenceTable.get(new AbstractMap.SimpleEntry<>(l, first)).add("<");
                            }
                        }
                    }
                }
            });
        });
    }


    /*
    String Analysis
     */
    //todo perform string analysis based on precedence table and relationship
    public boolean parseInput(String input) throws Exception {
        input = String.format("%s", input);
        this.input = input;

        reversedProduction = grammar.getReversedProductions();

        try {
            //todo add stop requirment for invalid input
            while (!this.input.equals(grammar.getInitialSymbol()) || this.input.length() < 1) {
                this.input = addPrecedence(this.input);
                stack.add(this.input);
                this.input = reduceInput(this.input);
            }
            stack.add(this.input);
        } catch (Exception ex) {
            throw new Exception(ex);
        }

//        while (!input.equals(String.format("<%s>", grammar.getInitialSymbol()))) {
//
//        }

        return true;
    }

    private String addPrecedence(String input) throws Exception {
        List<String> modList = new ArrayList<>();
        char[] chars = input.toCharArray();
        for(int i =0; i<chars.length-1; i++) {
            String s = String.valueOf(chars[i]);
            modList.add(s);
            if (!grammar.getNonTerminal().contains(s) && !grammar.getTerminal().contains(s)) {
                if (!s.equals("<") && !s.equals("=") && !s.equals(">")) {
                    throw new RuntimeException(String.format("Invalid character %s. Character not present in the alphabet", s));
                }
                continue;
            }
            String nextChar = String.valueOf(chars[i+1]);


            if (precedenceTable.containsKey(new AbstractMap.SimpleEntry<>(s, nextChar))) {
                Set<String> precedenceSet = precedenceTable.get(new AbstractMap.SimpleEntry<>(s, nextChar));
                if (precedenceSet.size()==1) {
                    modList.add((String) precedenceSet.toArray()[0]);
                } else if (precedenceSet.size()==2 && precedenceSet.contains("=")) {
                    modList.add("=");
                } else if (precedenceSet.size() == 0) {
                    throw new Exception("Parsing Error.");
                }else {
                    throw new Exception("Parsing Error. A precedence table cell contains more than 2 entries");
                }
            }

        }

            modList.add(String.valueOf(chars[chars.length -1]));

        return  String.format("<%s>", String.join("", modList));
    }

    private String reduceInput(String input) throws Exception {
        LinkedList<String> result = new LinkedList<>();
        AtomicInteger previousRelation = new AtomicInteger();

        AtomicInteger lastSmall = new AtomicInteger();
        AtomicBoolean lastEqual = new AtomicBoolean();

        input.chars().mapToObj(item -> (char) item).forEach(c -> {
            if (c == '<' || c == '>') {
                lastEqual.set(false);
                if (previousRelation.get() == '<' && c == '>') {
                    String replaced = "";
                    try {
                        replaced = replaceRelation(result.pollLast());
                        result.addLast(replaced);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
                previousRelation.set(c);
            } else if (c == '=') {
                lastEqual.set(true);
            } else {
                if (lastEqual.get()) {
                    result.addLast(result.pollLast()+String.valueOf(c));
                } else {
                    result.add(String.valueOf(c));
                }

            }
        });

        return String.join("", result);
    }

    private String replaceRelation(String relation) throws Exception {
        String reversedResult = reversedProduction.get(relation);
        if (reversedResult == null) {
//            throw new Exception("Parsing error");
            return null;
        }
        return reversedProduction.get(relation);
    }

    public void printConsoleTable() {
        /*
         * leftJustifiedRows - If true, it will add "-" as a flag to format string to
         * make it left justified. Otherwise right justified.
         */
        boolean leftJustifiedRows = false;

        /*
         * Table to print in console in 2-dimensional array. Each sub-array is a row.
         */
        List<String> firstRow = new ArrayList<>();
        firstRow.add(" ");
        firstRow.addAll(entries);

        List<List<String>> listTable = new ArrayList<>();
        listTable.add(firstRow);
        for(int i=0; i< firstRow.size()-1; i++){
            List<String> row = new ArrayList<>();
            String defRow = firstRow.get(i+1);
            row.add(firstRow.get(i+1));
            for (String column: entries) {
                row.add(String.join("",precedenceTable.get(new AbstractMap.SimpleEntry<>(defRow, column))));
            }
            listTable.add(row);
        }
        List<String[]> arrList = listTable.stream().map(list -> list.toArray(new String[0])).collect(Collectors.toList());
        String[][] table = listTable.stream().map(list -> list.toArray(new String[0])).toArray(String[][]::new);

        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         *
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(table).forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
            if (columnLengths.get(i) == null) {
                columnLengths.put(i, 0);
            }
            if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
            }
        }));

        /*
         * Prepare format String
         */
        final StringBuilder formatString = new StringBuilder("");
        String flag = leftJustifiedRows ? "-" : "";
        columnLengths.entrySet().stream().forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
        formatString.append("|\n");

        /*
         * Prepare line for top, bottom & below header row.
         */
        String line = columnLengths.entrySet().stream().reduce("", (ln, b) -> {
            String templn = "+-";
            templn = templn + Stream.iterate(0, (i -> i < b.getValue()), (i -> ++i)).reduce("", (ln1, b1) -> ln1 + "-",
                    (a1, b1) -> a1 + b1);
            templn = templn + "-";
            return ln + templn;
        }, (a, b) -> a + b);
        line = line + "+\n";

        /*
         * Print table
         */
        System.out.print(line);
        Arrays.stream(table).limit(1).forEach(a -> System.out.printf(formatString.toString(), a));
        System.out.print(line);

        Stream.iterate(1, (i -> i < table.length), (i -> ++i))
                .forEach(a -> System.out.printf(formatString.toString(), table[a]));
        System.out.print(line);
    }

    private void printStack() {
        String result = "";
        for(String row:this.stack) {
            result = String.format("%s\n%s", result, row);
        }
        System.out.println(result);
    }

}
