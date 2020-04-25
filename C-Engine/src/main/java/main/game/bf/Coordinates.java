package main.game.bf;

import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Coordinates implements Serializable {


    public static Coordinates[][] coordinates = new Coordinates[100][100];
    public int x;
    public int y;

    protected Coordinates[] adjacent;
    protected Coordinates[] adjacenctNoDiags;
    protected Coordinates[] adjacenctDiagsOnly;
    private boolean invalid = false;

    private static boolean flipX;
    private static boolean flipY;
    private static boolean rotate;

    public Coordinates() {
        this.x = 0;
        this.y = x;
    }

    public Coordinates(int x, int y) {
        this(true, x, y);
    }

    public Coordinates(boolean allowinvalid, int x, int y) {
        this.x = x;
        this.y = y;
        if (!allowinvalid) {
            checkInvalid();
        }
        if (flipX) {
            this.x = GuiManager.getCurrentLevelCellsX() - this.x;
        }
        if (flipY) {
            this.y = GuiManager.getCurrentLevelCellsY() - this.y;
        }
        if (rotate) {
            int buffer = x;
            this.x = y;
            this.y = buffer;
        }
    }

    public Coordinates(String s) {
        this(false, s);
    }

    public Coordinates(double x, double y) {
        this((int) x, (int) y);
    }

    public Coordinates(boolean custom, String s) {
        this(custom, NumberUtils.getInteger(splitCoordinateString(s)[0].trim()), NumberUtils
                .getInteger(splitCoordinateString(s)[1].trim()));
    }

    public static void resetCaches() {
//    TODO     adjacenctDirectionMap.clear();
//        adjacenctMap.clear();
//        adjacenctMapNoDiags.clear();
//        adjacenctMapDiagsOnly.clear();
//        int w = GuiManager.getBF_CompDisplayedCellsX();
//        int h = GuiManager.getBF_CompDisplayedCellsY();
//        coordinates = new Coordinates[MAX_WIDTH][MAX_HEIGHT]; //just in case... memory be damned

    }


    public static boolean withinBounds(int x, int y) {
        if (x < 0) {
            return false;
        }
        if (x >= GuiManager.getCurrentLevelCellsX()) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        return y < GuiManager.getCurrentLevelCellsY();

    }

    public static String[] splitCoordinateString(String s) {
        if (s.contains(StringMaster.COORDINATES_SEPARATOR_ALT)) {
            return StringMaster.cropParenthesises(s).split(
                    StringMaster.COORDINATES_SEPARATOR_ALT);
        }
        return StringMaster.cropParenthesises(s).split(
                StringMaster.COORDINATES_SEPARATOR);
    }

    public static Coordinates[] getCoordinates(String string) {
        if (string.isEmpty()) {
            return new Coordinates[0];
        }
        Coordinates[] coordinates = new Coordinates[string.split(",").length];
        int i = 0;
        for (String s : string.split(",")) {
            Coordinates c = Coordinates.get(s);
            coordinates[i] = c;
            i++;
        }
        return coordinates;
    }

    public static Coordinates getMiddleCoordinate(FACING_DIRECTION side) {
        return PositionMaster.getMiddleCoordinate(side);
    }

    public static void setFlipX(boolean flippedX) {
        flipX = flippedX;
    }

    public static void setFlipY(boolean flippedY) {
        flipY = flippedY;
    }

    public static void setRotated(boolean rotated) {
        rotate = rotated;

    }

    protected static boolean checkInvalid(Coordinates c) {
        if (CoreEngine.isLevelEditor()) {
            return false;
        }
        if (c.x >= GuiManager.getCurrentLevelCellsX()) {
            c.x = GuiManager.getCurrentLevelCellsX() - 1;
            c.setInvalid(true);
        }
        if (c.x < 0) {
            c.x = 0;
            c.setInvalid(true);
        }
        if (c.y >= GuiManager.getCurrentLevelCellsY()) {
            c.y = GuiManager.getCurrentLevelCellsY() - 1;
            c.setInvalid(true);
        }
        if (c.y < 0) {
            c.y = 0;
            c.setInvalid(true);
        }
        return c.isInvalid();
    }

    protected static boolean checkInvalid(int x, int y) {
        if (x >= GuiManager.getCurrentLevelCellsX()) {
            return true;
        }
        if (x < 0) {
            return true;
        }
        if (y >= GuiManager.getCurrentLevelCellsY()) {
            return true;
        }
        return y < 0;
    }

    public static Coordinates get(String s) {
        return get(false, s);
    }

    public static Coordinates get(double x, double y) {
        return get(true, (int) x, (int) y);
    }

    public static Coordinates get(boolean custom, String s) {
        return get(custom, NumberUtils.getInteger(splitCoordinateString(s)[0].trim()), NumberUtils
                .getInteger(splitCoordinateString(s)[1].trim()));
    }

    public static Coordinates get(boolean allowInvalid, int x, int y) {
        return new Coordinates(allowInvalid, x, y);
    }

    public static Coordinates get(int x, int y) {
        Coordinates c = coordinates[x][y];
        if (c == null) {
            c = new Coordinates(true, x, y);
            coordinates[x][y] = c;
        }
        return c;
    }

    public static void initCache(int w, int h) {
        coordinates = new Coordinates[w][h];
    }

    protected void checkInvalid() {
        Coordinates.checkInvalid(this);
    }

    public int hashCode() {

        return x * 10 + y;
    }

    @Override
    public boolean equals(Object arg0) {
        //        if (arg0 instanceof ZCoordinates) {
        //            if (this instanceof ZCoordinates) {
        //                ZCoordinates z1 = (ZCoordinates) this;
        //                ZCoordinates z2 = (ZCoordinates) arg0;
        //                if (z1.z != z2.z) {
        //                    return false;
        //                }
        //            }
        //        }
        if (arg0 instanceof Coordinates) {
            Coordinates c = (Coordinates) arg0;
            return c.x == x && c.y == y;
        }
        return false;
    }

    @Override
    public String toString() {
        return x + StringMaster.getCoordinatesSeparator() + y
                // + (z != 0 ? "; sublevel (Z): " + z : "")
                ;
    }

    public Coordinates swap() {
        int n = x;
        x = y;
        y = n;
        return this;
    }

    public Coordinates negativeY() {
        return new Coordinates(getX(), -getY());
    }

    public Coordinates negativeX() {
        return new Coordinates(-getX(), getY());
    }

    public Coordinates invertY() {
        return get(x, GuiManager.getBF_CompDisplayedCellsY() - 1 - y);
    }

    public Coordinates invertX() {
        return get(GuiManager.getBF_CompDisplayedCellsX() - 1 - x, y);
    }

    public Coordinates invert() {
        return invertX().invertY();
    }

    public Coordinates getAdjacentCoordinate(DIRECTION d, int i) {
        Coordinates c = null;
        for (; i > 0; i--) {
            c = getAdjacentCoordinate(d);
            if (c == null) {
                return null;
            }
        }
        return c;
    }

    public Coordinates getAdjacentCoordinate(DIRECTION direction) {
        return getAdjacentCoordinate(false, direction);
    }

    public Coordinates getAdjacentCoordinate(boolean allowInvalid, DIRECTION direction) {
        Map<DIRECTION, Coordinates> map = BattleFieldManager.getInstance().getAdjacenctDirectionMap().get(this);
        Coordinates c = null;
        if (map == null) {
            map = new HashMap<>();
        } else {
            c = map.get(direction);
            if (c != null) {
                return c;
            }
        }
        int x1 = x;
        int y1 = y;
        switch (direction) {
            case DOWN:
                y1++;
                break;
            case DOWN_LEFT:
                y1++;
                x1--;
                break;
            case DOWN_RIGHT:
                y1++;
                x1++;
                break;
            case LEFT:

                x1--;
                break;
            case RIGHT:
                x1++;
                break;
            case UP:
                y1--;
                break;
            case UP_LEFT:
                x1--;
                y1--;
                break;
            case UP_RIGHT:
                x1++;
                y1--;
                break;
            default:
                break;
        }
        if (!isAllowInvalidAdjacent())
            if (!allowInvalid) {
                if (!withinBounds(x1, y1)) {
                    return null;
                }
            }
        c = create(allowInvalid, x1, y1);
        map.put(direction, c);
        BattleFieldManager.getInstance().getAdjacenctDirectionMap().put(this, map);
        return c;
    }

    protected Coordinates create(boolean allowInvalid, int x1, int y1) {
        return new Coordinates(allowInvalid, x1, y1);
    }

    protected Set<Coordinates> getAdjacentDiagonal() {
        return getAdjacent(true);
    }

    public Set<Coordinates> getAdjacent(boolean diagonal) {
        Set<Coordinates> list = new HashSet<>();
        Coordinates c = this;
        boolean allowInvalidAdjacent = isAllowInvalidAdjacent();
        if (!diagonal) {
            if (allowInvalidAdjacent) {
                list.add(new Coordinates(true, c.x + 1, c.y));
                list.add(new Coordinates(true, c.x - 1, c.y));
                list.add(new Coordinates(true, c.x, c.y + 1));
                list.add(new Coordinates(true, c.x, c.y - 1));
            } else {
                if (!checkInvalid(c.x + 1, c.y))
                    list.add(Coordinates.get(c.x + 1, c.y));
                if (!checkInvalid(c.x - 1, c.y))
                    list.add(Coordinates.get(c.x - 1, c.y));
                if (!checkInvalid(c.x, c.y + 1))
                    list.add(Coordinates.get(c.x, c.y + 1));
                if (!checkInvalid(c.x, c.y - 1))
                    list.add(Coordinates.get(c.x, c.y - 1));
            }

        } else {
            if (allowInvalidAdjacent) {
                list.add(new Coordinates(true, c.x - 1, c.y - 1));
                list.add(new Coordinates(true, c.x - 1, c.y + 1));
                list.add(new Coordinates(true, c.x + 1, c.y - 1));
                list.add(new Coordinates(true, c.x + 1, c.y + 1));
            } else {
                if (!checkInvalid(c.x + 1, c.y - 1))
                    list.add(Coordinates.get(c.x + 1, c.y - 1));

                if (!checkInvalid(c.x + 1, c.y + 1))
                    list.add(Coordinates.get(c.x + 1, c.y + 1));

                if (!checkInvalid(c.x - 1, c.y + 1))
                    list.add(Coordinates.get(c.x - 1, c.y + 1));

                if (!checkInvalid(c.x - 1, c.y - 1))
                    list.add(Coordinates.get(c.x - 1, c.y - 1));
            }
        }
        return list;
    }

    protected boolean isAllowInvalidAdjacent() {
        return false;
    }

    public Set<Coordinates> getAdjacentOrthagonal() {
        return getAdjacent(false);
    }

    public Set<Coordinates> getAdjacentCoordinates() {
        return getAdjacentCoordinates(true);
    }

    public Coordinates[] getAdjacent() {
        if (adjacent == null) {
            adjacent = getAdjacentCoordinates().toArray(new Coordinates[0]);
        }
        return adjacent;
    }

    public Coordinates[] getAdjacenctNoDiags() {
        if (adjacenctNoDiags == null) {
            adjacenctNoDiags = getAdjacentCoordinates(false).toArray(new Coordinates[0]);
        }
        return adjacenctNoDiags;
    }

    public Coordinates[] getAdjacenctDiagsOnly() {
        if (adjacenctDiagsOnly == null) {
            adjacenctDiagsOnly = getAdjacentCoordinates(null).toArray(new Coordinates[0]);
        }
        return adjacenctDiagsOnly;
    }

    public Set<Coordinates> getAdjacentCoordinates(Boolean diagonals_included_not_only) {

        Set<Coordinates> set = BattleFieldManager.getInstance().getAdjacenctMap(diagonals_included_not_only).get(this);
        if (set != null)
            return set;
        set = new HashSet<>();

        if (diagonals_included_not_only != null) {
            if (diagonals_included_not_only) {
                set.addAll(getAdjacentDiagonal());
            }
            set.addAll(getAdjacentOrthagonal());
        } else {
            set.addAll(getAdjacentDiagonal());
        }
        BattleFieldManager.getInstance().getAdjacenctMap(diagonals_included_not_only).put(this, set);
        return set;

    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public boolean isAdjacent(Coordinates coordinates, Boolean diagonals_included_not_only) {
        if (diagonals_included_not_only == null) {
            return Math.abs(coordinates.x - x) == 1 && Math.abs(coordinates.y - y) == 1;
        }
        if (diagonals_included_not_only) {
            if (Math.abs(coordinates.x - x) == 1) {
                if (Math.abs(coordinates.y - y) <= 1) {
                    return true;
                }
            }
            if (Math.abs(coordinates.y - y) == 1) {
                return Math.abs(coordinates.x - x) <= 1;
            }
            return false;
        }
        if (Math.abs(coordinates.x - x) == 1) {
            if (Math.abs(coordinates.y - y) < 1) {
                return true;
            }
        }
        if (Math.abs(coordinates.y - y) == 1) {
            return Math.abs(coordinates.x - x) < 1;
        }
        return false;
    }

    public boolean isAdjacent(Coordinates coordinates) {
        if (Math.abs(coordinates.x - x) == 1) {
            if (Math.abs(coordinates.y - y) <= 1) {
                return true;
            }
        }
        if (Math.abs(coordinates.y - y) == 1) {
            return Math.abs(coordinates.x - x) <= 1;
        }
        return false;
    }

    public Coordinates getOffsetByX(int i) {
        return Coordinates.get(x + i, y);
    }

    public Coordinates getOffsetByY(int i) {
        return Coordinates.get(x, y + i);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getXorY(boolean xOrY) {
        return xOrY ? x : y;
    }

    public int dst(Coordinates c) {
        return PositionMaster.getDistance(
                this, c);
    }

    public double dst_(Coordinates c) {
        return PositionMaster.getExactDistance(
                this, c);
    }

    public Coordinates offset(Coordinates coordinates) {
        Coordinates c = getOffset(coordinates);
        setX(c.getX());
        setY(c.getY());
        return this;
    }

    public Coordinates getOffset(int x, int y) {
        try {
            return get(Math.max(0, getX() + x), Math.max(0, getY() + y));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return get(0,0);
    }

    public Coordinates getOffset(Coordinates coordinates) {
        return get(getX() + coordinates.x, getY() + coordinates.y);
    }

    public float dist(Coordinates coordinates) {
        return PositionMaster.getDistance(this, coordinates);
    }

    public Coordinates negative() {
        return create(true, -x, -y);
    }

    public Coordinates rotate(Boolean rotation, int w, int h) {
        Integer[][] mat = new Integer[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++)
                mat[x][y] = Integer.MIN_VALUE;
        }
        mat[x][0] = getX();
        mat[0][y] = getY();

        mat = ArrayMaster.rotate(rotation, mat);
        //rotate

        for (int x = 0; x < mat.length; x++) {
            for (int y = 0; y < mat[0].length; y++)
                if (mat[x][y] == getX())
                    setX(y);
            if (mat[x][y] == getY())
                setY(x);
        }
        return this;
    }

    public void flipY(int h) {
        setY(h - y);
    }

    public void flipX(int w) {
        setX(w - x);
    }
}
