package main.system.math;

import com.badlogic.gdx.math.Vector2;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.XLine;
import main.system.auxiliary.secondary.Bools;
import main.system.graphics.GuiManager;

import java.util.List;

public class PositionMaster {

    private static Double distances[][]  = new Double[50][50];

    public static Coordinates getMiddleCoordinate(FACING_DIRECTION side) {
        switch (side) {
            case EAST:
                return Coordinates.get(0, getMiddleIndex(true));
            case NONE:
                return Coordinates.get(getMiddleIndex(false), getMiddleIndex(true));
            case NORTH:
                return Coordinates.get(getMiddleIndex(false), 0);
            case SOUTH:
                return Coordinates.get(getMiddleIndex(false), getY());
            case WEST:
                return Coordinates.get(getX(), getMiddleIndex(true));
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

    public static boolean inLine(int x1, int x2, int y1, int y2) {
        if (x1 == x2)
            return true;
        return y1 == y2;
    }

    public static boolean inLine(Coordinates coordinates1, Coordinates coordinates2) {
        return inLine(coordinates1.x, coordinates2.x, coordinates1.y, coordinates2.y);

    }

    public static boolean inYLine(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.y == coordinates2.y;
    }

    public static boolean inXLine(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.x == coordinates2.x;
    }

    public static int getMaxStraightDistance(Coordinates coordinates, Coordinates cell) {

        int range = Math.max(Math.abs(getX_Diff(coordinates, cell)), Math.abs(getY_Diff(
         coordinates, cell)));
        return range;
    }

    public static int getX_Diff(Coordinates coordinates1, Coordinates coordinates2) {
        return Math.abs(coordinates1.x - coordinates2.x);
    }

    public static int getY_Diff(Coordinates coordinates1, Coordinates coordinates2) {
        return Math.abs(coordinates1.y - coordinates2.y);
    }

    public static int getDistance(Coordinates c1, Coordinates c2) {
        return (int) Math.round(getExactDistance(c1, c2));
    }

    public static double getExactDistance(Coordinates coordinates1, Coordinates coordinates2) {
        if (coordinates1.x < 0 || coordinates1.y < 0 || coordinates2.x < 0 || coordinates2.y < 0)
            return 0;
        int x = getX_Diff(coordinates1, coordinates2);
        int y = getY_Diff(coordinates1, coordinates2);
        if (x>=distances.length) {
            return Math.sqrt(x * x + y * y);
        }
        if (y>=distances[0].length) {
            return Math.sqrt(x * x + y * y);
        }
        Double result = distances[ x][ y] ;
        if (result != null)
            return result;
        if (x == 0) {
            return y;
        }
        if (y == 0) {
            return x;
        }

        result = Math.sqrt(x * x + y * y);
        distances[x][y] = result;
        return result;
    }
    public static double getExactDistanceNoCache(Coordinates coordinates1, Coordinates coordinates2) {
        int x = getX_Diff(coordinates1, coordinates2);
        int y = getY_Diff(coordinates1, coordinates2);
        return Math.sqrt(x * x + y * y);
    }

    public static double getExactDistance(Obj obj, Obj obj1) {
        return getExactDistance(obj.getCoordinates(), obj1.getCoordinates());
    }

    public static boolean isToTheLeft(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.x < coordinates2.x;
    }

    public static boolean isAbove(Coordinates coordinates1, Coordinates coordinates2) {
        return coordinates1.y < coordinates2.y;
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
        double d = getExactDistance(obj1, obj2);
        return ((int) ((roundMathematically) ? Math.round(d) : d));
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
        if (c1.x == c2.x) {
            return null;
        }
        return c1.x < c2.x;
    }

    public static Boolean isAboveOr(Coordinates c1, Coordinates c2) {
        if (c1.y == c2.y) {
            return null;
        }
        return c1.y < c2.y;
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
        return getDistanceToLine(x, y, x1, y1, x2, y2);
    }

    public static double getDistanceToLine(
     int x,
     int y,
     int x1,
     int y1,
     int x2,
     int y2) {
        double result = Math.abs((y2 - y1) * x - (x2 - x1) * y - y2 * x1 + x2 * y1)
         / Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
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
            if (!Bools.isTrue(north_or_south)) {
                return false;
            }
        }
        Boolean toTheLeftOr = isToTheLeftOr(obj1, obj2);
        if (toTheLeftOr == null) {
            if (west_or_east != null) {
                return false;
            }
        } else if (toTheLeftOr) {
            if (!Bools.isTrue(west_or_east)) {
                return false;
            }
        }

        return true;

    }

    public static float getAngle(Obj obj, Obj obj2) {
        return getAngle(obj.getCoordinates(), obj2.getCoordinates());
    }

    public static float getAngle(Coordinates c, Coordinates c2) {
        return new Vector2(c.x, c.y).angle(new Vector2(c2.x, c2.y));

    }

    public static void initDistancesCache( ) {
        initDistancesCache(GuiManager.getBF_CompDisplayedCellsX(),
         GuiManager.getBF_CompDisplayedCellsY());
    }
        public static void initDistancesCache(int w, int h) {
        distances = new Double[w][h] ;
    }

    public static int getLogicalY(int y) {
        return getY()- y - 1;
    }
    public static int getOpenGlY(int y) {
        return getY()- y - 1;
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
