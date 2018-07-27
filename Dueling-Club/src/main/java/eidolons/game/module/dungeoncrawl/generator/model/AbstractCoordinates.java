package eidolons.game.module.dungeoncrawl.generator.model;

import main.game.bf.Coordinates;

/**
 * Created by JustMe on 7/25/2018.
 */
public class AbstractCoordinates extends Coordinates {
    public AbstractCoordinates(int x, int y) {
        super(true, x, y);
    }

    @Override
    protected void checkInvalid() {
    }
    protected boolean isAllowInvalidAdjacent() {
        return true;
    }
}
