package md.utm.isa.lab2;

import java.util.*;

public class DFA extends FiniteAutomaton{
    private NFA nfa;

    private Queue<State> stateQueue = new LinkedList<>();


    public DFA() {

    }

    public DFA(NFA nfa) {
        this.nfa = nfa;
        this.setAlphabet(nfa.getAlphabet());

        // add initial state
        State initialState = nfa.getInitialState();
        this.put(initialState, buildTransition(initialState));

        addTransitionToQueue(this.get(initialState));

        while (!stateQueue.isEmpty()){
            State state = stateQueue.poll();
            Transition transition = buildTransition(state);

            this.put(state, transition);
            addTransitionToQueue(transition);

            if(isFinalState(state)){
                this.addFinalState(state);
            }
        }

        System.out.println("here");
    }

    @Override
    public boolean concatInnerState() {
        return true;
    }


    private boolean isFinalState(State state){
        return state.contains(nfa.getFinalStates().get(0).iterator().next());
    }


    private boolean isExistingState(State state){
        return this.containsKey(state);
    }

    private void addTransitionToQueue(Transition transition){
        Collection<State> states = transition.values();
        for (State state: states){
            if (!isExistingState(state) && state.size()>0){
                stateQueue.add(state);
            }
        }
    }

    private boolean transitionHasNewState(Transition transition){
        Collection<State> states = transition.values();
        for (State state:states){
            if(!this.containsKey(state))
                return true;
        }
        return false;
    }

    private Transition buildTransition(State state){
        Transition transition = new Transition();

        for (String letter: nfa.getAlphabet()){
            State newState = new State();
            for (String s : state) {
                State nfaState = new State();
                nfaState.add(s);
                if (nfa.get(nfaState) == null){
                    newState.addAll(new State());
                    continue;
                }
                State letterState = nfa.get(nfaState).get(letter);
                if(letterState == null){
                    newState.addAll(new State());
                } else {
                    newState.addAll(letterState);
                }
            }
            transition.put(letter, newState);
        }

        return transition;
    }
}
