package md.utm.isa.lab2;

public class NFA extends FiniteAutomaton{
    @Override
    public boolean concatInnerState() {
        return false;
    }
}
