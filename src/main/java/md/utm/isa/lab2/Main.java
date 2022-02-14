package md.utm.isa.lab2;



public class Main {
    public static void main(String[] args) throws Exception{
        String pathName = "lab2/data.txt";

        InputParser inputParser = new InputParser();
        NFA nfa = inputParser.convertInputToNFA(pathName);
        DFA dfa = new DFA(nfa);

        GraphBuilder graphBuilder = new GraphBuilder();
//        graphBuilder.buildGraph(nfa, "nfa");
        graphBuilder.buildGraph(dfa, "dfa");

        nfa.displayTransitionTable();
        dfa.displayTransitionTable();
    }

}
