package md.utm.isa.lab2;

import md.utm.isa.utils.FileUtil;

import java.io.File;
import java.util.Scanner;

public class InputParser {

    public NFA convertInputToNFA(String pathName) throws Exception{
        NFA nfa = new NFA();

        File file = FileUtil.getFileFromResources(pathName);
        Scanner reader = new Scanner(file);

        int lineCount = 0;
        while (reader.hasNextLine()){
            String line = reader.nextLine();
            if (line.charAt(0) == '%'){
                lineCount = 1;
                continue;
            }
            processLine(nfa, line, lineCount);

            lineCount++;
        }

        return nfa;
    }

    private void processLine(NFA nfa, String line, int lineNr){
        if (lineNr == 1) {
            nfa.setInitialState(readAcceptor(line));
        } else if (lineNr == 2){
            nfa.setStates(readStates(line));
        } else if (lineNr == 3) {
            nfa.setAlphabet(readAlphabet(line));
            nfa.addFinalState(readFinalState(line));
        } else if (lineNr>3 && lineNr <10) {
            addRelation(nfa, line);
        }
    }

    private void addRelation(NFA nfa, String relationLine){

        String[] statesAndInput = relationLine.split("\\(")[1].split("[^\\w]+");
        if(!containsState(nfa, statesAndInput[0])){
            State state = new State();
            state.add(statesAndInput[0]);

            Transition transition = new Transition();
            State innerState = new State();
            innerState.add(statesAndInput[2]);
            transition.put(statesAndInput[1], innerState);

            nfa.put(state, transition);
        } else {
            State outerState = new State();
            outerState.add(statesAndInput[0]);

            Transition transition = nfa.get(outerState);
            if (!transition.containsKey(statesAndInput[1])){
                State state = new State();
                state.add(statesAndInput[2]);

                transition.put(statesAndInput[1], state);
            }else {
                transition.get(statesAndInput[1]).add(statesAndInput[2]);
            }
        }
    }

    private boolean containsState(NFA nfa, String state) {
        for(State s: nfa.keySet()){
            if (s.contains(state))
                return true;
        }
        return false;
    }

    private State readAcceptor(String acceptor){
        String[] accParam = acceptor.split(",");

        State initialState = new State();
        initialState.add(accParam[3].replaceAll("[^\\w]+", ""));
        return initialState;
    }

    private State readFinalState(String line){
        String[] finalParam = line.split(" = ");

        State finalState = new State();
        finalState.add(finalParam[2].split("\\{")[1].replaceAll("[^\\w]+", ""));
        return finalState;
    }

    private String[] readAlphabet(String line){
        String alphabetDeff = line.substring(0,line.lastIndexOf(","));
        String alphabet = alphabetDeff.substring(alphabetDeff.indexOf('{')+1, alphabetDeff.indexOf('}')).replaceAll(" ", "");
        return alphabet.split(",");
    }

    private State[] readStates(String line){
        String statesString = line.substring(line.indexOf('{'), line.indexOf('}'));
        String[] splitStates = statesString.split(",");
        State[] states = new State[splitStates.length];

        for (int i = 0; i < states.length; i++) {
            State state = new State();
            String stateString = splitStates[i];
            state.add(splitStates[i].replaceAll("[^\\w]+", ""));
            states[i] = state;
        }

        return states;
    }
}
