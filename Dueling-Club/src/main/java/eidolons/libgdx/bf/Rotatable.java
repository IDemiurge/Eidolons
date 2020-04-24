package eidolons.libgdx.bf;

import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;

public interface Rotatable {
    DIRECTION getDirection();

    FACING_DIRECTION getFacing();

}
