package md.utm.isa.lab4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Production{
    private Left leftPart;
    private Right rightPart;

    public boolean hasEmptyState(){
        return rightPart.getStates().stream().anyMatch(State::isEmpty);
    }

}
