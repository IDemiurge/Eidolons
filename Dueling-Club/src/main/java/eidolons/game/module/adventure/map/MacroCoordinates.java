package eidolons.game.module.adventure.map;

import main.game.bf.Coordinates;

public class MacroCoordinates extends Coordinates {

    public MacroCoordinates(int x, int y) {
        super(true, x, y);
    }

    public MacroCoordinates(String boundary) {
        super(true, boundary);
    }
}
