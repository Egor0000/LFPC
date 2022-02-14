package md.utm.isa.lab2;

import java.util.*;
import java.util.stream.Stream;

abstract public class FiniteAutomaton extends LinkedHashMap<State, Transition> {
    private State initialState;
    private ArrayList<State> finalStates = new ArrayList<>();
    private String[] alphabet;
    private State[] states;

    public State getInitialState() {
        return initialState;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public State[] getStates() {
        return states;
    }

    public void setStates(State[] states) {
        this.states = states;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }


    public ArrayList<State> getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(ArrayList<State> finalStates) {
        this.finalStates = finalStates;
    }

    public void addFinalState(State state){
        this.finalStates.add(state);
    }

    public String stateToString(State state){
        if(state.isEmpty()){
            return "-";
        }
        return String.join("", state);
    }

    abstract public boolean concatInnerState();

    public void displayTransitionTable(){

        /*
         * leftJustifiedRows - If true, it will add "-" as a flag to format string to
         * make it left justified. Otherwise right justified.
         */
        boolean leftJustifiedRows = false;

        /*
         * Table to print in console in 2-dimensional array. Each sub-array is a row.
         */
        int alphCount = this.alphabet.length;
        ArrayList<String> definition = new ArrayList<>();
        definition.add("State");
        definition.addAll(Arrays.asList(alphabet));

        String[] definitionArray = definition.toArray(new String[alphCount+1]);

        ArrayList<String[]> table = new ArrayList<>();
        table.add(definitionArray);
        for(State state: keySet()){
            table.add(getTableRow(state));
        }
//
//        String[][] table = new String[][] { { "State", "First Name", "Last Name", "Age" },
//                { "1", "John", "Johnson", "45" }, { "2", "Tom", "", "35" }, { "3", "Rose", "Johnson", "22" },
//                { "4", "Jimmy", "Kimmel", "" } };

        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         *
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        table.forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
            if (columnLengths.get(i) == null) {
                columnLengths.put(i, 0);
            }
            if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
            }
        }));
        System.out.println(getAutomatonName()+ " Transition Table");
        System.out.println("columnLengths = " + columnLengths);

        /*
         * Prepare format String
         */
        final StringBuilder formatString = new StringBuilder("");
        String flag = leftJustifiedRows ? "-" : "";
        columnLengths.entrySet().stream().forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
        formatString.append("|\n");
        System.out.println("formatString = " + formatString.toString());

        String[][] arrayTable = table.toArray(new String[0][]);

        /*
         * Print table
         */
        Stream.iterate(0, (i -> i < arrayTable.length), (i -> ++i))
                .forEach(a -> System.out.printf(formatString.toString(), arrayTable[a]));
    }

    private String[] getTableRow(State state){
        ArrayList<String> row = new ArrayList<>();
        String stateString = stateToString(state);
        row.add(stateString);
        Transition transition = get(state);
        for(String letter: alphabet){
            if(transition.containsKey(letter)){
                row.add(stateToString(transition.get(letter)));
            } else {
                row.add(" - ");
            }
        }
        return row.toArray(new String[0]);
    }

    private String getAutomatonName(){
        if (this instanceof NFA){
            return "NFA";
        } else if (this instanceof DFA){
            return "DFA";
        }
        return "Unknown";
    }
}
