package main.game.battlefield;

import main.entity.obj.Obj;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.math.PositionMaster;

public class DirectionMaster {
    public static final DIRECTION FLIP_DIRECTION = DIRECTION.LEFT;

    public static DIRECTION getDirectionByFacing(FACING_DIRECTION f, UNIT_DIRECTION d) {
        return getDirectionByDegree(f.getDirection().getDegrees() + d.getDegrees());

    }

    public static DIRECTION getDirectionByDegree(int degrees) {
        degrees %= 360;
        if (degrees < 0)
            degrees += 360;
        switch (degrees) {
            case 0:
                return DIRECTION.RIGHT;
            case 45:
                return DIRECTION.UP_RIGHT;
            case 90:
                return DIRECTION.UP;
            case 135:
                return DIRECTION.UP_LEFT;
            case 180:
                return DIRECTION.LEFT;
            case 225:
                return DIRECTION.DOWN_LEFT;
            case 270:
                return DIRECTION.DOWN;
            case 315:
                return DIRECTION.DOWN_RIGHT;
        }
        main.system.auxiliary.LogMaster.log(1, "invalid direction: " + degrees);
        return null;
    }

    public static DIRECTION rotate90(DIRECTION direction, boolean clockwise) {
        int degrees = direction.getDegrees();
        degrees += (clockwise) ? -90 : 90;
        return getDirectionByDegree(degrees);
    }

    public static DIRECTION rotate45(DIRECTION direction, boolean clockwise) {
        int degrees = direction.getDegrees();
        degrees += (clockwise) ? -45 : 45;
        return getDirectionByDegree(degrees);

    }

    public static DIRECTION rotate180(DIRECTION direction) {
        return flip(direction);
    }

    public static DIRECTION flip(DIRECTION direction) {
        switch (direction) {
            case DOWN:
                return DIRECTION.UP;
            case DOWN_LEFT:
                return DIRECTION.UP_RIGHT;
            case DOWN_RIGHT:
                return DIRECTION.UP_LEFT;
            case LEFT:
                return DIRECTION.RIGHT;
            case RIGHT:
                return DIRECTION.LEFT;
            case UP:
                return DIRECTION.DOWN;
            case UP_LEFT:
                return DIRECTION.DOWN_RIGHT;
            case UP_RIGHT:
                return DIRECTION.DOWN_LEFT;
        }
        return direction;
    }

    public static DIRECTION getRelativeDirection(Coordinates source, Coordinates target) {
        if (PositionMaster.isAbove(source, target)) {
            if (PositionMaster.isToTheLeft(source, target))
                return DIRECTION.DOWN_RIGHT;
            else {
                if (PositionMaster.inXLine(source, target))
                    return DIRECTION.DOWN;
                else
                    return DIRECTION.DOWN_LEFT;
            }
        } else {
            if (PositionMaster.inYLine(source, target)) {
                if (PositionMaster.isToTheLeft(source, target))
                    return DIRECTION.RIGHT;
                else
                    return DIRECTION.LEFT;
            }
        }
        if (PositionMaster.isToTheLeft(source, target))
            return DIRECTION.UP_RIGHT;
        else {
            if (PositionMaster.inXLine(source, target))
                return DIRECTION.UP;
            else
                return DIRECTION.UP_LEFT;
        }

    }

    public static DIRECTION getRelativeDirection(Obj source, Obj target) {
        if (PositionMaster.isAbove(source, target)) {
            if (PositionMaster.isToTheLeft(source, target))
                return DIRECTION.DOWN_RIGHT;
            else {
                if (PositionMaster.inXLine(source, target))
                    return DIRECTION.DOWN;
                else
                    return DIRECTION.DOWN_LEFT;
            }
        } else {
            if (PositionMaster.inYLine(source, target)) {
                if (PositionMaster.isToTheLeft(source, target))
                    return DIRECTION.RIGHT;
                else
                    return DIRECTION.LEFT;
            }
        }
        if (PositionMaster.isToTheLeft(source, target))
            return DIRECTION.UP_RIGHT;
        else {
            if (PositionMaster.inXLine(source, target))
                return DIRECTION.UP;
            else
                return DIRECTION.UP_LEFT;
        }

    }

    public static DIRECTION getRandomDirection() {
        DIRECTION d = new EnumMaster<DIRECTION>().getRandomEnumConst(DIRECTION.class);
        return d;
    }

}
