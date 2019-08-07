package main.elements.conditions.standard;

import main.data.ability.OmittedConstructor;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class CoordinateCondition extends MicroCondition {
    private Coordinates c;

    @OmittedConstructor
    public CoordinateCondition(Coordinates c) {
        this.c = c;
    }

    @Override
    public boolean check(Ref ref) {
        return ref.getMatchObj().getCoordinates().equals(c);
    }
}
