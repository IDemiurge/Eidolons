package main.libgdx;

import main.game.battlefield.Coordinates;

public interface Rotatable {
    Coordinates.DIRECTION getDirection();

    Coordinates.FACING_DIRECTION getFacing();

}
