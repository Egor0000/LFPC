package md.utm.isa.lab4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class State {
    private String value;
    private boolean isTerminal;
    private boolean isNonTerminal;
    private boolean isEmpty;

    public State(Character ch){
        this.value = String.valueOf(ch);
        addState(ch);
    }

    public State(String str){
        this.value = str;
        addState(str.charAt(0));
    }


    private void addState(Character ch){
        if (Character.isLowerCase(ch)) {
            isTerminal = true;
        } else if (Character.isUpperCase(ch)) {
            isNonTerminal = true;
        } else if (ch == '?') {
            isTerminal = true;
            isEmpty = true;
        }
    }
}

