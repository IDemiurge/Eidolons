package eidolons.macro.map;

import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;

public class MacroCoordinates extends AbstractCoordinates {

    public MacroCoordinates(int x, int y) {
        super(true, x, y);
    }

    public MacroCoordinates(String boundary) {
        super(true, boundary);
    }
}
