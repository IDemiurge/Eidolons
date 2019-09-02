package eidolons.entity.obj.unit;

import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

public interface FacingEntity {
    FACING_DIRECTION getFacing();

    Coordinates getCoordinates();
}
