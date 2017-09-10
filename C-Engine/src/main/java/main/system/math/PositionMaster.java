package main.system.math;

import com.badlogic.gdx.math.Vector2;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.swing.XLine;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.graphics.GuiManager;

import java.util.List;

public class PositionMaster {

    public static Coordinates getMiddleCoordinate(FACING_DIRECTION side) {
        switch (side) {
            case EAST:
                return new Coordinates(0, getMiddleIndex(true));
            case NONE:
                return new Coordinates(getMiddleIndex(false), getMiddleIndex(true));
            case NORTH:
                return new Coordinates(getMiddleIndex(false), 0);
            case SOUTH:
                return new Coordinates(getMiddleIndex(false), getY());
            case WEST:
                return new Coordinates(getX(), getMiddleIndex(true));
        }
        return null;
    }

    public static int getMiddleIndex(boolean vertical) {
        return ((vertical) ? getY() : getX()) / 2;
    }

    public static int getX() {
        return GuiManager.getCurrentLevelCellsX();
    }

    public static int getY() {
        return GuiManager.getCurrentLevelCellsY();
    }

    public static boolean noObstaclesInLine(Coordinates c1, Coordinates c2, BattleFieldGrid grid,
                                            Obj source) {

        if (c1.x == c2.x) {
            return grid.noObstaclesX(c1.x, c1.y, c2.y, source);
        }
        if (c1.y == c2.y) {
            return grid.noObstaclesY(c1.y, c1.x, c2.x, source);
        }
        return true;
    }

    public static boolean noObstaclesInDiagonal(Coordinates c1, Coordinates c2,
                                                BattleFieldGrid grid, Obj source) {
        return grid.noObstaclesOnDiagonal(c1, c2, source);
    }

    public static boolean inLine(Coordinates coordinates1, Coordinates coordinates2) {
        return (inYLine(coordinates1, coordinates2) || inXLine(coordinates1, coordinates2));

    }

    public static boolean inYLine(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.getY() == coordinates2.getY();
    }

    public static boolean inXLine(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.getX() == coordinates2.getX();
    }

    public static int getMaxStraightDistance(Coordinates coordinates, Coordinates cell) {

        int range = Math.max(Math.abs(getX_Diff(coordinates, cell)), Math.abs(getY_Diff(
         coordinates, cell)));
        return range;
    }

    public static int getX_Diff(Coordinates coordinates1, Coordinates coordinates2) {
        return Math.abs(coordinates1.getX() - coordinates2.getX());
    }

    public static int getY_Diff(Coordinates coordinates1, Coordinates coordinates2) {
        return Math.abs(coordinates1.getY() - coordinates2.getY());
    }

    public static int getDistance(Coordinates c1, Coordinates c2) {
        return (int) Math.round(getExactDistance(c1, c2));
    }

    public static double getExactDistance(Coordinates coordinates1, Coordinates coordinates2) {
        if (inLine(coordinates1, coordinates2)) {
            return getMaxStraightDistance(coordinates1, coordinates2);
        }
        // diagonal adjacency?!

        int x = getX_Diff(coordinates1, coordinates2);
        int y = getY_Diff(coordinates1, coordinates2);
        if (x == 0) {
            return y;
        }
        if (y == 0) {
            return x;
        }

        return Math.sqrt(x * x + y * y);
    }

    public static boolean isToTheLeft(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.getX() < coordinates2.getX();
    }

    public static boolean isAbove(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.getY() < coordinates2.getY();
    }

    public static boolean inLine(Obj obj1, Obj obj2) {
        return (inYLine(obj1, obj2) || inXLine(obj1, obj2));

    }

    public static boolean inYLine(Obj obj1, Obj obj2) {
        return obj1.getY() == obj2.getY();
    }

    public static boolean inXLine(Obj obj1, Obj obj2) {
        return obj1.getX() == obj2.getX();
    }

    public static boolean checkNoObstaclesInLine(MicroObj obj1, MicroObj obj2) {
        return noObstaclesInLine(obj1.getCoordinates(), obj2.getCoordinates(), obj1.getGame()
         .getBattleField().getGrid(), obj1);
    }

    public static int getMaxStraightDistance(Obj obj, Obj cell) {

        int range = Math.max(Math.abs(getX_Diff(obj, cell)), Math.abs(getY_Diff(obj, cell)));
        return range;
    }

    public static int getX_Diff(Obj obj1, Obj obj2) {
        return Math.abs(obj1.getX() - obj2.getX());
    }

    public static int getY_Diff(Obj obj1, Obj obj2) {
        return Math.abs(obj1.getY() - obj2.getY());
    }

    public static int getDistance(Obj obj1, Obj obj2) {
        return getDistance(obj1, obj2, true);
    }

    public static int getDistance(Obj obj1, Obj obj2, boolean roundMathematically) {
        int x = getX_Diff(obj1, obj2);
        int y = getY_Diff(obj1, obj2);
        if (x == 0) {
            return y;
        }
        if (y == 0) {
            return x;
        }

        double sqrt = Math.sqrt(x * x + y * y);
        int distance =

         ((int) ((roundMathematically) ? Math.round(sqrt) : sqrt));

        return distance;
    }

    public static boolean isToTheLeft(Obj obj1, Obj obj2) {
        return obj1.getX() < obj2.getX();
    }

    public static boolean isAbove(Obj obj1, Obj obj2) {
        return obj1.getY() < obj2.getY();
    }

    public static Boolean isToTheLeftOr(Obj obj1, Obj obj2) {
        return isToTheLeftOr(obj1.getCoordinates(), obj2.getCoordinates());
    }

    public static Boolean isAboveOr(Obj obj1, Obj obj2) {
        return isAboveOr(obj1.getCoordinates(), obj2.getCoordinates());
    }

    public static Boolean isToTheLeftOr(Coordinates c1, Coordinates c2) {
        if (c1.getX() == c2.getX()) {
            return null;
        }
        return c1.getX() < c2.getX();
    }

    public static Boolean isAboveOr(Coordinates c1, Coordinates c2) {
        if (c1.getY() == c2.getY()) {
            return null;
        }
        return c1.getY() < c2.getY();
    }

    public static boolean inLineDiagonally(Coordinates c1, Coordinates c2) {
        return getX_Diff(c1, c2) == getY_Diff(c1, c2);
    }

    public static Coordinates getClosestCoordinate(Coordinates source_coordinates, List<Obj> objects) {
        int distance = Integer.MAX_VALUE;
        Coordinates coordinates = null;
        for (Obj obj : objects) {
            if (distance > getDistance(source_coordinates, obj.getCoordinates())) {
                distance = getDistance(source_coordinates, obj.getCoordinates());
                coordinates = obj.getCoordinates();
            }
        }
        return coordinates;
    }

    public static boolean noObstaclesInLine(Obj obj1, Obj obj2, BattleFieldGrid grid) {
        return noObstaclesInLine(obj1.getCoordinates(), obj2.getCoordinates(), grid, obj1);

    }

    public static double getDistanceToLine(XLine xLine, Coordinates coordinates) {
        int x = coordinates.x;
        int y = coordinates.y;
        int x1 = xLine.getX1();
        int x2 = xLine.getX2();
        int y1 = xLine.getY1();
        int y2 = xLine.getY2();
        double result = Math.abs((y2 - y1) * x - (x2 - x1) * y - y2 * x1 + x2 * y1)
         / Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        // return (int) Math.round(result);
        // denominator =
        return result;
    }

    public static boolean checkDirection(Obj obj1, Obj obj2, Boolean north_or_south,
                                         Boolean west_or_east) {
        if (obj1.getCoordinates().equals(obj2.getCoordinates())) {

        }
        Boolean aboveOr = isAboveOr(obj1, obj2);
        if (aboveOr == null) {
            if (north_or_south != null) {
                return false;
            }
        } else if (aboveOr) { // strict - permit null?
            if (!BooleanMaster.isTrue(north_or_south)) {
                return false;
            }
        }
        Boolean toTheLeftOr = isToTheLeftOr(obj1, obj2);
        if (toTheLeftOr == null) {
            if (west_or_east != null) {
                return false;
            }
        } else if (toTheLeftOr) {
            if (!BooleanMaster.isTrue(west_or_east)) {
                return false;
            }
        }

        return true;

    }

    public static float getAngle(Obj obj, Obj obj2) {
        return getAngle(obj.getCoordinates(), obj2.getCoordinates());
    }

    public static float getAngle(Coordinates c, Coordinates c2) {
        return new Vector2(c.getX(), c.getY()).angle(new Vector2(c2.getX(), c2.getY()));

    }

    public enum SHAPES {
        CONE, RECTANGLE {
            public boolean isRemoveBase() {
                return true;
            }
        },
        STAR, CROSS,;

        public boolean isRemoveBase() {
            return false;
        }
    }

}
