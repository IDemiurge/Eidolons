package eidolons.ability.conditions;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.entity.Ref;
import main.game.bf.Coordinates;

import java.util.List;

public class AreaCondition extends DC_Condition {
    private final List<Coordinates> coordinates;

    public AreaCondition(Coordinates coordinates, int width, int height) {
        this(coordinates, width, height, false);
    }
    public AreaCondition(Coordinates coordinates, int width, int height, boolean bottomToTop) {
        super();
        this.coordinates = CoordinatesMaster.getCoordinatesBetweenInclusive(coordinates,
                new Coordinates(coordinates.x + width, coordinates.y + height * ((bottomToTop)? -1 : 1)));

    }

    @Override
    public boolean check(Ref ref) {
        return coordinates.contains(ref.getSourceObj().getCoordinates());
    }
}
