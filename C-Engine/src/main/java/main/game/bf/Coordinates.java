package main.game.bf;

import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coordinates {

    static Coordinates[][] coordinates;
    private static int h;
    private static int w;
    private static boolean flipX;
    private static boolean flipY;
    private static boolean rotate;
    private static Map<Coordinates, Map<DIRECTION, Coordinates>> adjacenctDirectionMap = new HashMap<>();
    private static Map<Coordinates, List<Coordinates>> adjacenctMap = new HashMap<>();
    private static Map<Coordinates, List<Coordinates>> adjacenctMapNoDiags = new HashMap<>();
    private static Map<Coordinates, List<Coordinates>> adjacenctMapDiagsOnly = new HashMap<>();
    public int x;
    public int y;
    protected int z = 0;
    Coordinates[] adjacent;
    Coordinates[] adjacenctNoDiags;
    Coordinates[] adjacenctDiagsOnly;
    private boolean invalid = false;

    public Coordinates() {
        this.x = 0;
        this.y = x;
    }

    public Coordinates(int x, int y) {
        this(true, x, y);
    }

    public Coordinates(boolean allowinvalid, int x, int y) {
        if (!allowinvalid) {
            if (x >= GuiManager.getCurrentLevelCellsX()) {
                x = GuiManager.getCurrentLevelCellsX() - 1;
                this.setInvalid(true);
            }
            if (x < 0) {
                x = 0;
                this.setInvalid(true);
            }
            if (y >= GuiManager.getCurrentLevelCellsY()) {
                y = GuiManager.getCurrentLevelCellsY() - 1;
                this.setInvalid(true);
            }
            if (y < 0) {
                y = 0;
                this.setInvalid(true);
            }
        }
        this.x = x;
        this.y = y;
        if (flipX) {
            x = GuiManager.getCurrentLevelCellsX() - x;
        }
        if (flipY) {
            y = GuiManager.getCurrentLevelCellsY() - y;
        }
        if (rotate) {
            int buffer = x;
            x = y;
            y = buffer;
        }
    }

    public Coordinates(String s) {
        this(false, s);
    }

    public Coordinates(double x, double y) {
        this((int) x, (int) y);
    }

    public Coordinates(boolean custom, String s) {
        this(custom, StringMaster.getInteger(splitCoordinateString(s)[0].trim()), StringMaster
         .getInteger(splitCoordinateString(s)[1].trim()));
    }

    public static void clearCaches() {
        adjacenctDirectionMap.clear();
        adjacenctMap.clear();
        adjacenctMapNoDiags.clear();
        adjacenctMapDiagsOnly.clear();

    }

    public static Map<Coordinates, Map<DIRECTION, Coordinates>> getAdjacenctDirectionMap() {
        return adjacenctDirectionMap;
    }

    public static Map<Coordinates, List<Coordinates>> getAdjacenctMap(Boolean diags) {
        if (diags != null)
            return diags ? adjacenctMap : adjacenctMapNoDiags;
        return adjacenctMapDiagsOnly;
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
        return StringMaster.cropParenthesises(s).split(StringMaster.getCoordinatesSeparator());
    }

    public static Coordinates[] getCoordinates(String string) {
        if (string.isEmpty()) {
            return new Coordinates[0];
        }
        Coordinates[] coordinates = new Coordinates[string.split(",").length];
        int i = 0;
        for (String s : string.split(",")) {
            Coordinates c = new Coordinates(s);
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

    public int hashCode() {

        return x * 10 + y;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof ZCoordinates) {
            if (this instanceof ZCoordinates) {
                ZCoordinates z1 = (ZCoordinates) this;
                ZCoordinates z2 = (ZCoordinates) arg0;
                if (z1.z != z2.z) {
                    return false;
                }
            }
        }
        if (arg0 instanceof Coordinates) {
            Coordinates c = (Coordinates) arg0;
            return c.x == x && c.y == y && c.z == z;
        }
        return false;
    }

    @Override
    public String toString() {
        return x + StringMaster.getCoordinatesSeparator() + y
         // + (z != 0 ? "; sublevel (Z): " + z : "")
         ;
    }

    public Coordinates invert() {
        if (h == 0) {
            h = GuiManager.getBF_CompDisplayedCellsY();
        }
        if (w == 0) {
            w = GuiManager.getBF_CompDisplayedCellsX();
        }
        this.x = w - 1 - x;
        this.y = h - 1 - y;
        LogMaster.log(2, "Inverted to " + toString());
        return this;
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
        Map<DIRECTION, Coordinates> map = getAdjacenctDirectionMap().get(this);
        Coordinates c = null;
        if (map == null) {
            map = new HashMap<>();
            getAdjacenctDirectionMap().put(this, map);
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
        if (!allowInvalid) {
            if (!withinBounds(x1, y1)) {
                return null;
            }
        }
        c = new Coordinates(allowInvalid, x1, y1);
        map.put(direction, c);
        return c;
    }

    private List<Coordinates> getAdjacentDiagonal() {
        return getAdjacent(true);
    }

    public List<Coordinates> getAdjacent(boolean diagonal) {
        List<Coordinates> list = new ArrayList<>();
        Coordinates c = this;
        Coordinates e;
        if (!diagonal) {
            e = new Coordinates(false, c.x, c.y - 1);
            if (!e.isInvalid()) {
                list.add(e);
            }

            e = new Coordinates(false, c.x - 1, c.y);
            if (!e.isInvalid()) {
                list.add(e);
            }
            e = new Coordinates(false, c.x + 1, c.y);
            if (!e.isInvalid()) {
                list.add(e);
            }

            e = new Coordinates(false, c.x, c.y + 1);
            if (!e.isInvalid()) {
                list.add(e);
            }
        } else {
            e = new Coordinates(false, c.x - 1, c.y + 1);
            if (!e.isInvalid()) {
                list.add(e);
            }
            e = new Coordinates(false, c.x + 1, c.y - 1);
            if (!e.isInvalid()) {
                list.add(e);
            }

            e = new Coordinates(false, c.x - 1, c.y - 1);
            if (!e.isInvalid()) {
                list.add(e);
            }

            e = new Coordinates(false, c.x + 1, c.y + 1);
            if (!e.isInvalid()) {
                list.add(e);
            }
        }
        return list;
    }

    public List<Coordinates> getAdjacentOrthagonal() {
        return getAdjacent(false);
    }

    public List<Coordinates> getAdjacentCoordinates() {
        return getAdjacentCoordinates(true);
    }

    public Coordinates[] getAdjacent() {
        if (adjacent == null) {
            adjacent = getAdjacentCoordinates().toArray(new Coordinates[
             getAdjacentCoordinates().size()]);
        }
        return adjacent;
    }

    public Coordinates[] getAdjacenctNoDiags() {
        if (adjacenctNoDiags == null) {
            adjacenctNoDiags = getAdjacentCoordinates(false).toArray(new Coordinates[
             getAdjacentCoordinates(false).size()]);
        }
        return adjacenctNoDiags;
    }

    public Coordinates[] getAdjacenctDiagsOnly() {
        if (adjacenctDiagsOnly == null) {
            adjacenctDiagsOnly = getAdjacentCoordinates(null).toArray(new Coordinates[
             getAdjacentCoordinates(null).size()]);
        }
        return adjacenctDiagsOnly;
    }

    public List<Coordinates> getAdjacentCoordinates(Boolean diagonals_included_not_only) {

        List<Coordinates> list = getAdjacenctMap(diagonals_included_not_only).get(this);
        if (list != null)
            return list;
        list = new ArrayList<>();

        if (diagonals_included_not_only != null) {
            if (diagonals_included_not_only) {
                list.addAll(getAdjacentDiagonal());
            }
            list.addAll(getAdjacentOrthagonal());
        } else {
            list.addAll(getAdjacentDiagonal());
        }
        getAdjacenctMap(diagonals_included_not_only).put(this, list);
        return list;

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
                if (Math.abs(coordinates.x - x) <= 1) {
                    return true;
                }
            }
            return false;
        }
        if (Math.abs(coordinates.x - x) == 1) {
            if (Math.abs(coordinates.y - y) < 1) {
                return true;
            }
        }
        if (Math.abs(coordinates.y - y) == 1) {
            if (Math.abs(coordinates.x - x) < 1) {
                return true;
            }
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
            if (Math.abs(coordinates.x - x) <= 1) {
                return true;
            }
        }
        return false;
    }

    public Coordinates getOffsetByX(int i) {
        return new Coordinates(x + i, y);
    }

    public Coordinates getOffsetByY(int i) {
        return new Coordinates(x, y + i);
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
        return (int) Math.round(Math.sqrt((x - c.x) * (x - c.x) + (y - c.y) * (y - c.y)));
    }


    public Coordinates offset(Coordinates coordinates) {
        return getOffsetByX(coordinates.x).getOffsetByY(coordinates.y);
    }
}
