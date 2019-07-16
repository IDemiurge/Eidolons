package eidolons.ability.conditions;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.entity.Ref;
import main.game.bf.Coordinates;

import java.util.List;

public class AreaCondition extends DC_Condition {
    private final List<Coordinates> coordinates;

    public AreaCondition(Coordinates coordinates, int width, int height) {
        super();
        this.coordinates = CoordinatesMaster.getCoordinatesBetween(coordinates, new Coordinates(coordinates.x + width, coordinates.y + height));

    }

    @Override
    public boolean check(Ref ref) {
        if (coordinates.contains(ref.getSourceObj().getCoordinates())) {
            return true;
        }
        return false;
    }
}
