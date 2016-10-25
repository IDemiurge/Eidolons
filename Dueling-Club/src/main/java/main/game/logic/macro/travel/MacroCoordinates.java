package main.game.logic.macro.travel;

import main.game.battlefield.Coordinates;

public class MacroCoordinates extends Coordinates {

    public MacroCoordinates(int x, int y) {
        super(true, x, y);
    }

    public MacroCoordinates(String boundary) {
        super(true, boundary);
    }
}
