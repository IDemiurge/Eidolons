package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.StackingRule;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.entity.Entity;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.*;
import java.util.function.Function;

public class FacingMaster {
    public static final FACING_DIRECTION[] FACING_DIRECTIONS = {
     main.game.bf.directions.FACING_DIRECTION.NORTH,
     main.game.bf.directions.FACING_DIRECTION.WEST,
     main.game.bf.directions.FACING_DIRECTION.EAST,
     main.game.bf.directions.FACING_DIRECTION.SOUTH
    };

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

    public static FACING_SINGLE getSingleFacing(BattleFieldObject obj1, BfObj obj2) {
        return getSingleFacing(obj1.getFacing(), obj1, obj2);
    }

    public static FACING_SINGLE getSingleFacing(FACING_DIRECTION facing, BattleFieldObject obj1,
                                                BfObj obj2) {
        return getSingleFacing(facing, obj1.getCoordinates(), obj2.getCoordinates());
    }

    public static FACING_SINGLE getSingleFacing_ (Obj sourceObj, Obj targetObj) {
        return getSingleFacing((BattleFieldObject) sourceObj, (BfObj) targetObj);
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

    public static FACING_DIRECTION getFacing(Obj sourceObj) {
        if (sourceObj instanceof Unit) {
            return ((Unit) sourceObj).getFacing();
        }
        return null;
    }

    public static FACING_DIRECTION getOptimalFacingTowardsUnits(
     Coordinates c, Collection<? extends Obj> units

    ) {
        return getOptimalFacingTowardsUnits(c, units, t -> 1);
    }

    public static FACING_DIRECTION getOptimalFacingTowardsUnits(
     Coordinates c, Collection<? extends Obj> units
     , Function<Entity, Integer> function
    ) {
        HashMap<FACING_DIRECTION, Double> map = new LinkedHashMap<>();
        for (FACING_DIRECTION facing : main.game.bf.directions.FACING_DIRECTION.values())
            for (Obj member : units) {
                if (FacingMaster.getSingleFacing(facing, c, member.getCoordinates()) != FACING_SINGLE.IN_FRONT) {
                    continue;
                }
                double x = ((Unit) member).calculatePower() / PositionMaster
                 .getExactDistance(member.getCoordinates(), c);
                Double i = map.get(facing);
                if (i == null) {
                    i = 0.0;
                }
                i += function.apply(member) * x;
                map.put(facing, i);

            }
        FACING_DIRECTION pick = null;
        Double max = 0.0;
        for (FACING_DIRECTION fac : map.keySet()) {
            if (map.get(fac) > max) {
                max = map.get(fac);
                pick = fac;
            }
        }
        return pick;
    }

    public static FACING_DIRECTION getOptimalFacingTowardsEmptySpaces(Unit unit) {
        Set<Coordinates> coordinates = unit.getCoordinates().getAdjacentCoordinates();
        return getOptimalFacing(unit, coordinates, (c) -> {
            if (StackingRule.checkCanPlace(c, unit, null))
                return 1;
            return 0;
        });

    }

    public static FACING_DIRECTION getOptimalFacing(Unit unit, Set<Coordinates> coordinates,
                                                    Function<Coordinates, Integer> function) {
        HashMap<FACING_DIRECTION, Double> map = new LinkedHashMap<>();

        for (FACING_DIRECTION facing : main.game.bf.directions.FACING_DIRECTION.values()) {
            Double i = 0.0;
            for (Coordinates c : coordinates) {
                if (FacingMaster.getSingleFacing(facing, unit.getCoordinates(), c) != FACING_SINGLE.IN_FRONT) {
                    continue;
                }
                i += function.apply(c);
            }
            map.put(facing, i);
        }
        FACING_DIRECTION pick = null;
        Double max = 0.0;
        for (FACING_DIRECTION fac : map.keySet()) {
            if (map.get(fac) > max) {
                max = map.get(fac);
                pick = fac;
            }
        }
        return pick;
    }

    public static FACING_DIRECTION getFacing(String facing) {
        return new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class, facing);
    }

}
