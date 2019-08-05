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
    NONE(DIRECTION.DOWN, false, false);

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


    public FACING_DIRECTION flip() {
                switch (this) {
                    case EAST:
                        return WEST;
                    case NORTH:
                        return SOUTH;
                    case SOUTH:
                        return NORTH;
                    case WEST:
                        return EAST;
                    case NONE:
                        return this;
                }
            return this;
        }

    public FACING_DIRECTION rotate(boolean clockwise) {
        switch (this){
            case NORTH:
                return clockwise ? EAST : WEST;
            case WEST:
                return clockwise ? NORTH : SOUTH;
            case EAST:
                return clockwise ? SOUTH : NORTH;
            case SOUTH:
                return clockwise ? WEST : EAST;
            case NONE:
                break;
        }
        return NONE;
    }}
