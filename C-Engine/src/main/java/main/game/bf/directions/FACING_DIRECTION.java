package main.game.bf.directions;

import main.system.auxiliary.StringMaster;

/**
 * Created by Giskard on 6/9/2018.
 */
public enum FACING_DIRECTION {
    NORTH(DIRECTION.UP, true, true),
    WEST(DIRECTION.LEFT, false, true),
    EAST(DIRECTION.RIGHT, false, false),
    SOUTH(DIRECTION.DOWN, true, false),
    NONE(null, false, false);

    public static final FACING_DIRECTION[] values = values();
    public static final FACING_DIRECTION[] normalFacing = {
     NORTH,     WEST, EAST, SOUTH
    };
    private DIRECTION direction;
    private boolean vertical;

    public boolean isCloserToZero() {
        return closerToZero;
    }

    public void setCloserToZero(boolean closerToZero) {
        this.closerToZero = closerToZero;
    }

    private boolean closerToZero;

    FACING_DIRECTION(DIRECTION direction, boolean vertical, boolean closerToZero) {
        this.direction = direction;
        this.vertical = (vertical);
        this.closerToZero = (closerToZero);
    }

    public DIRECTION getDirection() {
        return direction;
    }


    public String getName() {
        return StringMaster.getWellFormattedString(name());
    }

    public boolean isVertical() {
        return vertical;
    }


}
