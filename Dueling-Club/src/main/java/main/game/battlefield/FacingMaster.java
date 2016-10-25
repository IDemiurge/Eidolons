package main.game.battlefield;

import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.entity.obj.BattlefieldObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_UnitObj;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.Arrays;

public class FacingMaster {
    public static FACING_DIRECTION rotate(FACING_DIRECTION oldDirection, boolean clockwise) {
        switch (oldDirection) {
            case EAST:
                return (!clockwise) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
            case NONE:
                break;
            case NORTH:
                return (!clockwise) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;
            case SOUTH:
                return (clockwise) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;
            case WEST:
                return (clockwise) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;

        }
        return oldDirection;
    }

    public static FACING_SINGLE getSingleFacing(DC_UnitObj obj1, BattlefieldObj obj2) {
        return getSingleFacing(obj1.getFacing(), obj1, obj2);
    }

    public static FACING_SINGLE getSingleFacing(FACING_DIRECTION facing, DC_UnitObj obj1,
                                                BattlefieldObj obj2) {
        return getSingleFacing(facing, obj1.getCoordinates(), obj2.getCoordinates());
    }

    public static FACING_SINGLE getSingleFacing(FACING_DIRECTION facing, Coordinates c1,
                                                Coordinates c2) {
        if (c1.equals(c2))
            return FACING_SINGLE.IN_FRONT;
        switch (facing) {
            case NONE:
                return FACING_SINGLE.NONE;
            case EAST:
                if (c1.getX() == c2.getX())
                    return FACING_SINGLE.TO_THE_SIDE;
                if (!PositionMaster.isToTheLeft(c1, c2))
                    return FACING_SINGLE.BEHIND;
                else
                    return FACING_SINGLE.IN_FRONT;

            case NORTH:
                if (c1.getY() == c2.getY())
                    return FACING_SINGLE.TO_THE_SIDE;
                if (PositionMaster.isAbove(c1, c2))
                    return FACING_SINGLE.BEHIND;
                else
                    return FACING_SINGLE.IN_FRONT;
            case SOUTH:
                if (c1.getY() == c2.getY())
                    return FACING_SINGLE.TO_THE_SIDE;
                if (!PositionMaster.isAbove(c1, c2))
                    return FACING_SINGLE.BEHIND;
                else
                    return FACING_SINGLE.IN_FRONT;
            case WEST:
                if (c1.getX() == c2.getX())
                    return FACING_SINGLE.TO_THE_SIDE;
                if (PositionMaster.isToTheLeft(c1, c2))
                    return FACING_SINGLE.BEHIND;
                else
                    return FACING_SINGLE.IN_FRONT;

        }
        return FACING_SINGLE.NONE;

    }

    public static FACING_DIRECTION rotate180(FACING_DIRECTION side) {
        switch (side) {
            case EAST:
                return FACING_DIRECTION.WEST;
            case NORTH:
                return FACING_DIRECTION.SOUTH;
            case SOUTH:
                return FACING_DIRECTION.NORTH;
            case WEST:
                return FACING_DIRECTION.EAST;
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
                return FACING_DIRECTION.SOUTH;
            case LEFT:
                return FACING_DIRECTION.WEST;
            case RIGHT:
                return FACING_DIRECTION.EAST;
            case UP:
                return FACING_DIRECTION.NORTH;
            case DOWN_LEFT:
                if (random)
                    return (RandomWizard.random()) ? FACING_DIRECTION.WEST : FACING_DIRECTION.SOUTH;
                else
                    return (horizontalPreference) ? FACING_DIRECTION.WEST : FACING_DIRECTION.SOUTH;
            case DOWN_RIGHT:
                if (random)
                    return (RandomWizard.random()) ? FACING_DIRECTION.EAST : FACING_DIRECTION.SOUTH;
                else
                    return (horizontalPreference) ? FACING_DIRECTION.EAST : FACING_DIRECTION.SOUTH;
            case UP_LEFT:
                if (random)
                    return (RandomWizard.random()) ? FACING_DIRECTION.WEST : FACING_DIRECTION.NORTH;
                else
                    return (horizontalPreference) ? FACING_DIRECTION.WEST : FACING_DIRECTION.NORTH;
            case UP_RIGHT:
                if (random)
                    return (RandomWizard.random()) ? FACING_DIRECTION.EAST : FACING_DIRECTION.NORTH;
                else
                    return (horizontalPreference) ? FACING_DIRECTION.EAST : FACING_DIRECTION.NORTH;

        }
        return null;
    }

    public static FACING_DIRECTION getRelativeFacing(DC_HeroObj wall, DC_HeroObj adjWall) {
        return getRelativeFacing(wall.getCoordinates(), adjWall.getCoordinates());
    }

    public static FACING_DIRECTION getPresetFacing(boolean me) {
        return me ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
    }

    public static FACING_DIRECTION getRelativeFacing(Coordinates c, Coordinates c2) {
        Boolean above = PositionMaster.isAboveOr(c, c2);
        if (above != null)
            return above ? FACING_DIRECTION.SOUTH : FACING_DIRECTION.NORTH;
        Boolean left = PositionMaster.isToTheLeftOr(c, c2);
        if (left == null)
            return left ? FACING_DIRECTION.EAST : FACING_DIRECTION.WEST;

        // if diagonal
        return FACING_DIRECTION.NONE;
    }

    public static FACING_DIRECTION getRandomFacing() {
        return getRandomFacing(null);
    }

    public static FACING_DIRECTION getRandomFacing(FACING_DIRECTION... exceptions) {
        if (exceptions != null)
            if (exceptions.length >= 4)
                return null;

        FACING_DIRECTION f = new EnumMaster<FACING_DIRECTION>()
                .getRandomEnumConst(FACING_DIRECTION.class);

        if (f == FACING_DIRECTION.NONE)
            return getRandomFacing();

        if (exceptions != null)
            if (Arrays.asList(exceptions).contains(f))
                return getRandomFacing();

        return f;
    }

}
