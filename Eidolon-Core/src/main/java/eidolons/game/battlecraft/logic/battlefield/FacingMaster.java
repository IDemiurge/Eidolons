package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.unit.Unit;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.Arrays;

public class FacingMaster {

    public static FACING_DIRECTION rotate(FACING_DIRECTION oldDirection, boolean clockwise) {
        switch (oldDirection) {
            case EAST:
                return (!clockwise) ? main.game.bf.directions.FACING_DIRECTION.NORTH : main.game.bf.directions.FACING_DIRECTION.SOUTH;
            case NONE:
                break;
            case NORTH:
                return (!clockwise) ? main.game.bf.directions.FACING_DIRECTION.WEST : main.game.bf.directions.FACING_DIRECTION.EAST;
            case SOUTH:
                return (clockwise) ? main.game.bf.directions.FACING_DIRECTION.WEST : main.game.bf.directions.FACING_DIRECTION.EAST;
            case WEST:
                return (clockwise) ? main.game.bf.directions.FACING_DIRECTION.NORTH : main.game.bf.directions.FACING_DIRECTION.SOUTH;

        }
        return oldDirection;
    }


    public static FACING_DIRECTION rotate180(FACING_DIRECTION side) {
        if (side!=null )
        switch (side) {
            case EAST:
                return main.game.bf.directions.FACING_DIRECTION.WEST;
            case NORTH:
                return main.game.bf.directions.FACING_DIRECTION.SOUTH;
            case SOUTH:
                return main.game.bf.directions.FACING_DIRECTION.NORTH;
            case WEST:
                return main.game.bf.directions.FACING_DIRECTION.EAST;
            case NONE:
                return side;
        }
        return side;
    }

    public static FACING_DIRECTION getFacingFromDirection(DIRECTION direction) {
        return getFacingFromDirection(direction, false, false);
    }

    public static FACING_DIRECTION getFacingFromDirection(DIRECTION direction, boolean random,
                                                          boolean horizontalPreference) {
        switch (direction) {
            case DOWN:
                return main.game.bf.directions.FACING_DIRECTION.SOUTH;
            case LEFT:
                return main.game.bf.directions.FACING_DIRECTION.WEST;
            case RIGHT:
                return main.game.bf.directions.FACING_DIRECTION.EAST;
            case UP:
                return main.game.bf.directions.FACING_DIRECTION.NORTH;
            case DOWN_LEFT:
                if (random) {
                    return (RandomWizard.random()) ? main.game.bf.directions.FACING_DIRECTION.WEST : main.game.bf.directions.FACING_DIRECTION.SOUTH;
                } else {
                    return (horizontalPreference) ? main.game.bf.directions.FACING_DIRECTION.WEST : main.game.bf.directions.FACING_DIRECTION.SOUTH;
                }
            case DOWN_RIGHT:
                if (random) {
                    return (RandomWizard.random()) ? main.game.bf.directions.FACING_DIRECTION.EAST : main.game.bf.directions.FACING_DIRECTION.SOUTH;
                } else {
                    return (horizontalPreference) ? main.game.bf.directions.FACING_DIRECTION.EAST : main.game.bf.directions.FACING_DIRECTION.SOUTH;
                }
            case UP_LEFT:
                if (random) {
                    return (RandomWizard.random()) ? main.game.bf.directions.FACING_DIRECTION.WEST : main.game.bf.directions.FACING_DIRECTION.NORTH;
                } else {
                    return (horizontalPreference) ? main.game.bf.directions.FACING_DIRECTION.WEST : main.game.bf.directions.FACING_DIRECTION.NORTH;
                }
            case UP_RIGHT:
                if (random) {
                    return (RandomWizard.random()) ? main.game.bf.directions.FACING_DIRECTION.EAST : main.game.bf.directions.FACING_DIRECTION.NORTH;
                } else {
                    return (horizontalPreference) ? main.game.bf.directions.FACING_DIRECTION.EAST : main.game.bf.directions.FACING_DIRECTION.NORTH;
                }

        }
        return null;
    }

    public static FACING_DIRECTION getRelativeFacing(Unit wall, Unit adjWall) {
        return getRelativeFacing(wall.getCoordinates(), adjWall.getCoordinates());
    }

    public static FACING_DIRECTION getPresetFacing(boolean me) {
        return me ? main.game.bf.directions.FACING_DIRECTION.NORTH : main.game.bf.directions.FACING_DIRECTION.SOUTH;
    }

    public static FACING_DIRECTION getRelativeFacing(Coordinates c, Coordinates c2) {
        boolean x_y = Math.abs(c.x - c2.x) > Math.abs(c.y - c2.y);
        if (Math.abs(c.x - c2.x) == Math.abs(c.y - c2.y)) {
            x_y = RandomWizard.random();
        }

        if (x_y) {
            Boolean left = PositionMaster.isToTheLeftOr(c, c2);
            if (left != null) {
                return left ? main.game.bf.directions.FACING_DIRECTION.EAST : main.game.bf.directions.FACING_DIRECTION.WEST;
            }
        }
        Boolean above = PositionMaster.isAboveOr(c, c2);
        if (above != null) {
            return above ? main.game.bf.directions.FACING_DIRECTION.SOUTH : main.game.bf.directions.FACING_DIRECTION.NORTH;
        }
        return null;

//        List<Coordinates> list = c.getAdjacent(false);
//       return getRelativeFacing( list.getVar(RandomWizard.getRandomIndex(list)), c2);
//        Coordinates.getVar(c.x, c.y+(RandomWizard.random() ? 1 : -1))
    }

    public static FACING_DIRECTION getRandomFacing() {
        return getRandomFacing(null);
    }

    public static FACING_DIRECTION getRandomFacing(FACING_DIRECTION... exceptions) {
        if (exceptions != null) {
            if (exceptions.length >= 4) {
                return null;
            }
        }

        FACING_DIRECTION f = new EnumMaster<FACING_DIRECTION>()
         .getRandomEnumConst(FACING_DIRECTION.class);

        if (f == main.game.bf.directions.FACING_DIRECTION.NONE) {
            return getRandomFacing();
        }

        if (exceptions != null) {
            if (Arrays.asList(exceptions).contains(f)) {
                return getRandomFacing();
            }
        }

        return f;
    }

    public static FACING_DIRECTION getFacing(String facing) {
        return new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class, facing);
    }

}
