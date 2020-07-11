package eidolons.game.module.generator.model;

import main.data.ability.construct.VariableManager;
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

    public AbstractCoordinates(boolean allowinvalid, int x, int y) {
        super(allowinvalid, x, y);
    }

    public AbstractCoordinates(boolean custom, String s) {
        super(custom, s);
    }

    public static AbstractCoordinates createFromVars(String substring) {
        if (!substring.contains("(")) {
            return new AbstractCoordinates(0, 0);
        }
        Integer x = Integer.valueOf(VariableManager.getVarList(substring).get(0).trim());
        Integer y = Integer.valueOf(VariableManager.getVarList(substring).get(1).trim());
        return new AbstractCoordinates(true,
                x, y);
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    public Coordinates getOffsetByX(int i) {
        return new AbstractCoordinates(x + i, y);
    }

    @Override
    protected Coordinates create(boolean allowInvalid, int x1, int y1) {
        return new AbstractCoordinates(x1, y1);
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
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}
