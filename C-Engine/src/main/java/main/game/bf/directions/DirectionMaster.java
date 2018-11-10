package main.game.bf.directions;

import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.PositionMaster;

public class DirectionMaster {
    public static final DIRECTION FLIP_DIRECTION = DIRECTION.LEFT;
    private static DIRECTION[][] relative_directions;

    public static DIRECTION getDirectionByFacing(FACING_DIRECTION f, UNIT_DIRECTION d) {
        return getDirectionByDegree(f.getDirection().getDegrees() + d.getDegrees());

    }

    public static DIRECTION getDirectionByDegree(int degrees) {
        degrees %= 360;
        if (degrees < 0) {
            degrees += 360;
        }

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
        LogMaster.log(1, "invalid direction: " + degrees);
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

    public static DIRECTION flipVertically(DIRECTION direction) {
        switch (direction) {
            case UP:
                return DIRECTION.DOWN;
            case DOWN:
                return DIRECTION.UP;
            case UP_LEFT:
                return DIRECTION.DOWN_LEFT;
            case UP_RIGHT:
                return DIRECTION.DOWN_RIGHT;
            case DOWN_RIGHT:
                return DIRECTION.UP_RIGHT;
            case DOWN_LEFT:
                return DIRECTION.UP_LEFT;
        }
        return direction;
    }

    public static DIRECTION flipHorizontally(DIRECTION direction) {
        switch (direction) {
            case UP_LEFT:
                return DIRECTION.UP_RIGHT;
            case UP_RIGHT:
                return DIRECTION.UP_LEFT;
            case DOWN_RIGHT:
                return DIRECTION.DOWN_LEFT;
            case DOWN_LEFT:
                return DIRECTION.DOWN_RIGHT;
            case RIGHT:
                return DIRECTION.LEFT;
            case LEFT:
                return DIRECTION.RIGHT;
        }
        return direction;
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
        if (relative_directions == null) {
            return getRelativeDirectionNoCache(source, target);
        }
        int diffX = source.x - target.x;
        int diffY = source.y - target.y;
        int absX = Math.abs(diffX);
        int absY = Math.abs(diffY);

        DIRECTION d = relative_directions[absX][absY];
        if (d != null) {
            if (diffX < 0) {
                d = d.flipHorizontally();
            }
            if (diffY < 0) {
                d = d.flipVertically();
            }
            return d;
        }
        //        d = relative_directions[-diffX][-diffY];
        //        if (d != null) {
        //            return d.flip();
        //        }
        source = new Coordinates(source.x, source.y);
        target = new Coordinates(target.x, target.y);
        if (diffX < 0) {
            int x = target.getX();
            target.setX(source.getX());
            source.setX(x);
        }
        if (diffY < 0) {
            int y = target.getY();
            target.setY(source.getY());
            source.setY(y);
        }
        d = getRelativeDirectionNoCache(source, target);
        relative_directions[absX][absY] = d;
        return d;
    }

    public static DIRECTION getRelativeDirectionNoCache(Coordinates source, Coordinates target) {

        if (PositionMaster.isAbove(source, target)) {
            if (PositionMaster.isToTheLeft(source, target)) {
                return DIRECTION.DOWN_RIGHT;
            } else {
                if (PositionMaster.inXLine(source, target)) {
                    return DIRECTION.DOWN;
                } else {
                    return DIRECTION.DOWN_LEFT;
                }
            }
        } else {
            if (PositionMaster.inYLine(source, target)) {
                if (PositionMaster.isToTheLeft(source, target)) {
                    return DIRECTION.RIGHT;
                } else {
                    return DIRECTION.LEFT;
                }
            }
        }
        if (PositionMaster.isToTheLeft(source, target)) {
            return DIRECTION.UP_RIGHT;
        } else {
            if (PositionMaster.inXLine(source, target)) {
                return DIRECTION.UP;
            } else {
                return DIRECTION.UP_LEFT;
            }
        }

    }

    public static DIRECTION getRelativeDirection(Obj source, Obj target) {
        if (PositionMaster.isAbove(source, target)) {
            if (PositionMaster.isToTheLeft(source, target)) {
                return DIRECTION.DOWN_RIGHT;
            } else {
                if (PositionMaster.inXLine(source, target)) {
                    return DIRECTION.DOWN;
                } else {
                    return DIRECTION.DOWN_LEFT;
                }
            }
        } else {
            if (PositionMaster.inYLine(source, target)) {
                if (PositionMaster.isToTheLeft(source, target)) {
                    return DIRECTION.RIGHT;
                } else {
                    return DIRECTION.LEFT;
                }
            }
        }
        if (PositionMaster.isToTheLeft(source, target)) {
            return DIRECTION.UP_RIGHT;
        } else {
            if (PositionMaster.inXLine(source, target)) {
                return DIRECTION.UP;
            } else {
                return DIRECTION.UP_LEFT;
            }
        }

    }

    public static DIRECTION getRandomDirection() {
        return new EnumMaster<DIRECTION>().getRandomEnumConst(DIRECTION.class);
    }

    public static void initCache(Integer cellsX, Integer cellsY) {
        //save over TODO
        relative_directions = new DIRECTION[cellsX][cellsY];
    }
}
