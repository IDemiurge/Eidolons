package eidolons.game.module.dungeoncrawl.generator.model;

import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

/**
 * Created by JustMe on 7/25/2018.
 */
public class AbstractCoordinates extends Coordinates {
    public AbstractCoordinates(int x, int y) {
        super(true, x, y);
    }

    public AbstractCoordinates(String s) {
        super(s);
    }

    public AbstractCoordinates(Coordinates c) {
        this(c.x, c.y);
    }

    public Coordinates getOffsetByX(int i) {
        return new AbstractCoordinates(x + i, y);
    }

    @Override
    protected Coordinates create(boolean allowInvalid, int x1, int y1) {
        return new AbstractCoordinates( x1, y1);
    }

    public Coordinates getOffsetByY(int i) {
        return new AbstractCoordinates(x, y + i);
    }
    @Override
    protected void checkInvalid() {
    }
    protected boolean isAllowInvalidAdjacent() {
        return true;
    }

    public Coordinates getAdjacentCoordinate(DIRECTION direction) {
        return getAdjacentCoordinate(true, direction);
    }

    @Override
    public int dst(Coordinates c) {
        return (int) Math.round(dst_(c));
    }

    @Override
    public double dst_(Coordinates c) {
        int xDiff = c.x - x;
        int yDiff = c.y - y;
        return  Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}
