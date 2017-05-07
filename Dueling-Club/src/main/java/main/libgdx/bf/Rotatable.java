package main.libgdx.bf;

import main.game.bf.Coordinates;

public interface Rotatable {
    Coordinates.DIRECTION getDirection();

    Coordinates.FACING_DIRECTION getFacing();

}
