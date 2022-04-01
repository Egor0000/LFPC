package md.utm.isa.lab4;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
public class Right {
    private List<State> states = new ArrayList<>();

    public Right(String rightString){
        addRightPartSates(rightString);
    }

    public List<Integer> allEmptyIndexes(String s){
        return IntStream.range(0, states.size())
                .filter(i -> states.get(i).getValue().equals(s))
                .boxed()
                .collect(Collectors.toList());

    }

    private void addRightPartSates(String rightPart){
        String[] ss = rightPart.split("((?<=(A-Za-z))|(?=[A-Za-z]))");;
        for (String s : ss) {
            addRightState(s);
        }
    }

    private void addRightState(String ch){
        State state = new State(ch);
        states.add(state);
    }
}
