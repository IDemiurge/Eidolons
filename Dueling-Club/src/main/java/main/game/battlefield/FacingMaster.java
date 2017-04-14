package main.game.battlefield;

import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

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

    public static FACING_SINGLE getSingleFacing(BattleFieldObject obj1, BfObj obj2) {
        return getSingleFacing(obj1.getFacing(), obj1, obj2);
    }

    public static FACING_SINGLE getSingleFacing(FACING_DIRECTION facing, BattleFieldObject obj1,
                                                BfObj obj2) {
        return getSingleFacing(facing, obj1.getCoordinates(), obj2.getCoordinates());
    }

    public static FACING_SINGLE getSingleFacing(FACING_DIRECTION facing, Coordinates c1,
                                                Coordinates c2) {
        if (c1.equals(c2)) {
            return UnitEnums.FACING_SINGLE.IN_FRONT;
        }
        switch (facing) {
            case NONE:
                return UnitEnums.FACING_SINGLE.NONE;
            case EAST:
                if (c1.getX() == c2.getX()) {
                    return UnitEnums.FACING_SINGLE.TO_THE_SIDE;
                }
                if (!PositionMaster.isToTheLeft(c1, c2)) {
                    return UnitEnums.FACING_SINGLE.BEHIND;
                } else {
                    return UnitEnums.FACING_SINGLE.IN_FRONT;
                }

            case NORTH:
                if (c1.getY() == c2.getY()) {
                    return UnitEnums.FACING_SINGLE.TO_THE_SIDE;
                }
                if (PositionMaster.isAbove(c1, c2)) {
                    return UnitEnums.FACING_SINGLE.BEHIND;
                } else {
                    return UnitEnums.FACING_SINGLE.IN_FRONT;
                }
            case SOUTH:
                if (c1.getY() == c2.getY()) {
                    return UnitEnums.FACING_SINGLE.TO_THE_SIDE;
                }
                if (!PositionMaster.isAbove(c1, c2)) {
                    return UnitEnums.FACING_SINGLE.BEHIND;
                } else {
                    return UnitEnums.FACING_SINGLE.IN_FRONT;
                }
            case WEST:
                if (c1.getX() == c2.getX()) {
                    return UnitEnums.FACING_SINGLE.TO_THE_SIDE;
                }
                if (PositionMaster.isToTheLeft(c1, c2)) {
                    return UnitEnums.FACING_SINGLE.BEHIND;
                } else {
                    return UnitEnums.FACING_SINGLE.IN_FRONT;
                }

        }
        return UnitEnums.FACING_SINGLE.NONE;

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
                if (random) {
                    return (RandomWizard.random()) ? FACING_DIRECTION.WEST : FACING_DIRECTION.SOUTH;
                } else {
                    return (horizontalPreference) ? FACING_DIRECTION.WEST : FACING_DIRECTION.SOUTH;
                }
            case DOWN_RIGHT:
                if (random) {
                    return (RandomWizard.random()) ? FACING_DIRECTION.EAST : FACING_DIRECTION.SOUTH;
                } else {
                    return (horizontalPreference) ? FACING_DIRECTION.EAST : FACING_DIRECTION.SOUTH;
                }
            case UP_LEFT:
                if (random) {
                    return (RandomWizard.random()) ? FACING_DIRECTION.WEST : FACING_DIRECTION.NORTH;
                } else {
                    return (horizontalPreference) ? FACING_DIRECTION.WEST : FACING_DIRECTION.NORTH;
                }
            case UP_RIGHT:
                if (random) {
                    return (RandomWizard.random()) ? FACING_DIRECTION.EAST : FACING_DIRECTION.NORTH;
                } else {
                    return (horizontalPreference) ? FACING_DIRECTION.EAST : FACING_DIRECTION.NORTH;
                }

        }
        return null;
    }

    public static FACING_DIRECTION getRelativeFacing(Unit wall, Unit adjWall) {
        return getRelativeFacing(wall.getCoordinates(), adjWall.getCoordinates());
    }

    public static FACING_DIRECTION getPresetFacing(boolean me) {
        return me ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
    }

    public static FACING_DIRECTION getRelativeFacing(Coordinates c, Coordinates c2) {
        boolean x_y = Math.abs(c.x - c2.x) > Math.abs(c.y - c2.y);
        if (Math.abs(c.x - c2.x) == Math.abs(c.y - c2.y)) {
            x_y = RandomWizard.random();
        }

        if (x_y) {
            Boolean left = PositionMaster.isToTheLeftOr(c, c2);
            if (left != null) {
                return left ? FACING_DIRECTION.EAST : FACING_DIRECTION.WEST;
            }
        }
        Boolean above = PositionMaster.isAboveOr(c, c2);
        if (above != null) {
            return above ? FACING_DIRECTION.SOUTH : FACING_DIRECTION.NORTH;
        }
        return null;

//        List<Coordinates> list = c.getAdjacent(false);
//       return getRelativeFacing( list.get(RandomWizard.getRandomListIndex(list)), c2);
//        new Coordinates(c.x, c.y+(RandomWizard.random() ? 1 : -1))
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

        if (f == FACING_DIRECTION.NONE) {
            return getRandomFacing();
        }

        if (exceptions != null) {
            if (Arrays.asList(exceptions).contains(f)) {
                return getRandomFacing();
            }
        }

        return f;
    }

    public static FACING_DIRECTION getFacing(Obj sourceObj) {
        if (sourceObj instanceof Unit) {
            return ((Unit) sourceObj).getFacing();
        }
        return null;
    }

    public static FACING_DIRECTION getOptimalFacingTowardsUnits(Coordinates c,
                                                                Collection<? extends Obj> units) {
        HashMap<FACING_DIRECTION, Integer> map = new HashMap<>();
        for (Obj member : units) { // [QUICK
            // FIX]
            // getGame().getParty().getMembers()
            FACING_DIRECTION facing = FacingMaster.getRelativeFacing(c, member.getCoordinates());
            if (facing == null) {
                facing = FacingMaster.getFacingFromDirection(DirectionMaster.getRelativeDirection(
                        c, member.getCoordinates()));
            }

            Integer i = map.get(facing);
            if (i == null) {
                i = 0;
            }
            i++;
            map.put(facing, i);

        }
        FACING_DIRECTION pick = null;
        Integer max = 0;
        for (FACING_DIRECTION fac : map.keySet()) {
            if (map.get(fac) > max) {
                max = map.get(fac);
                pick = fac;
            }
        }
        return pick;
    }
}
