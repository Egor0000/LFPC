package md.utm.isa.lab4;

import lombok.Data;

@Data
public class Left {
    private State state;

    public Left(String stringState){
        this.state = new State(stringState, false, true, false);
    }

}
