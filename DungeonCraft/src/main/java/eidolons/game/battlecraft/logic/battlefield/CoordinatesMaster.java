package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.core.master.EffectMaster;
import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Loop;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.math.PositionMaster;

import java.util.*;
import java.util.stream.Collectors;

public class CoordinatesMaster {

    public static Coordinates getCoordinateBetween(Coordinates c1, Coordinates c2,
                                                   int displacement, int perpendicularOffset) {
        int x = (c1.x * (100 - displacement) + c2.x * (displacement)) / 100;
        int y = (c1.y * (100 - displacement) + c2.y * (displacement)) / 100;
        if (perpendicularOffset != 0) {
            int distance = PositionMaster.getDistance(c1, c2);
            double sin = Math.abs(new Double(c1.x - c2.x)) / distance;
            double cos = Math.abs(new Double(c1.y - c2.y)) / distance;
            int x_offset = (int) Math.round(perpendicularOffset * sin);
            int y_offset = (int) Math.round(perpendicularOffset * cos);
            x += x_offset;
            y += y_offset;
        }
        return Coordinates.get(true, x, y);
    }

    public static Coordinates getFarmostCoordinateInDirection(DIRECTION d,
                                                              List<Coordinates> coordinates) {
        return getFarmostCoordinateInDirection(d, coordinates, true);
    }


    public static Coordinates getFarmostCoordinateInDirection(DIRECTION d,
                                                              List<Coordinates> coordinates, final Boolean prefLessMoreMiddle) {
        coordinates = getSortedByProximityToEdge(d, coordinates, prefLessMoreMiddle);
        if (!ListMaster.isNotEmpty(coordinates))
            return null;
        return coordinates.get(0);
    }

    public static List<Coordinates> getSortedByProximityToEdge(DIRECTION d,
                                                               List<Coordinates> coordinates, final Boolean prefLessMoreMiddle) {
        final Boolean x_more = d.growX;
        final Boolean y_more = d.growY;
        final int x1 = getMinX(coordinates);
        final int x2 = getMaxX(coordinates);
        final int y1 = getMinY(coordinates);
        final int y2 = getMaxY(coordinates);
        coordinates.sort((o1, o2) -> compare_(x1, x2, y1, y2, prefLessMoreMiddle, x_more, y_more, o1, o2));
        return coordinates;
    }

    public static List<Coordinates> getEdgeCoordinatesFromSquare(FACING_DIRECTION direction,
                                                                 List<Coordinates> coordinates) {
        if (coordinates.isEmpty()) {
            return coordinates;
        }
        coordinates = getSortedByProximityToEdge(direction.getDirection(), coordinates, null);
        int edge = coordinates.get(0).getXorY(!direction.isVertical());
        List<Coordinates> list = new ArrayList<>();
        for (Coordinates c : coordinates) {
            if (direction.isCloserToZero()) {
                if (c.getXorY(!direction.isVertical()) > edge) {
                    break;
                }
            } else if (c.getXorY(!direction.isVertical()) < edge) {
                break;
            }
            list.add(c);
        }
        return list;
    }

    public static int compare_(int x1, int x2, int y1, int y2, final Boolean prefLessMoreMiddle,
                               final Boolean x_more, final Boolean y_more, Coordinates o1, Coordinates o2) {
        int result = 0;
        boolean x = true;
        if (x_more != null) {
            x = false;
            if (o1.getX() != o2.getX()) {
                if (x_more) {
                    result = o1.getX() < o2.getX() ? 1 : -1;
                } else {
                    result = o1.getX() > o2.getX() ? 1 : -1;
                }
            }
        }
        if (y_more != null) {
            if (o1.getY() != o2.getY()) {
                if (y_more) {
                    result = o1.getY() < o2.getY() ? 1 : -1;
                } else {
                    result = o1.getY() > o2.getY() ? 1 : -1;
                }
            }
        }
        float midX = (float) (x1 + x2) / 2;
        float midY = (float) (y1 + y2) / 2;
        if (result == 0) {
            if (prefLessMoreMiddle == null) {
                if (o1.getY() == o2.getY()) {
                    if (Math.abs(o1.getX() - midX) > Math.abs(o2.getX() - midX)) {
                        result = 1;
                    }
                    if (Math.abs(o1.getX() - midX) < Math.abs(o2.getX() - midX)) {
                        result = -1;
                    }
                    // need min/max to prioritize middle!
                } else if (o1.getX() == o2.getX()) {
                    if (Math.abs(o1.getY() - midY) > Math.abs(o2.getY() - midY)) {
                        result = 1;
                    }
                    if (Math.abs(o1.getY() - midY) < Math.abs(o2.getY() - midY)) {
                        result = -1;
                    }
                    // need min/max to prioritize middle!
                }
            } else {
                if (prefLessMoreMiddle) {
                    if (x) {
                        result = o1.getX() < o2.getX() ? 1 : -1;
                    } else {
                        result = o1.getY() < o2.getY() ? 1 : -1;
                    }
                } else if (x) {
                    result = o1.getX() > o2.getX() ? 1 : -1;
                } else {
                    result = o1.getY() > o2.getY() ? 1 : -1;
                }
            }
        }

        return result;
    }

    public static boolean isWithinBounds(Coordinates c, int x1, int x2, int y1, int y2) {
        if (c.x < x1) {
            return false;
        }
        if (c.x > x2) {
            return false;
        }
        if (c.y < y1) {
            return false;
        }
        return c.y <= y2;
    }

    public static String getCoordinatesStringData(
            Collection<Coordinates> coordinates) {
        int x1 = getMinX(coordinates);
        int x2 = getMaxX(coordinates);
        int y1 = getMinY(coordinates);
        int y2 = getMaxY(coordinates);
        List<Coordinates> exceptions = new ArrayList<>(getCoordinatesWithin(x1, x2, y1, y2));
        return getBoundsString(x1, x2, y1, y2) + " " + exceptions.toString();
    }

    public static String getBoundsFromCoordinates(Collection<Coordinates> coordinates) {
        int x1 = getMinX(coordinates);
        int x2 = getMaxX(coordinates);
        int y1 = getMinY(coordinates);
        int y2 = getMaxY(coordinates);
        return getBoundsString(x1, x2, y1, y2);
    }

    public static String getBoundsString(int x1, int x2, int y1, int y2) {
        return "X" + x1 + "-" + x2 + "; Y" + y1 + "-" + y2;
    }

    public static List<Coordinates> getCornerCoordinates(Collection<Coordinates> list) {
        int[] array = getMinMaxCoordinates(list);
        List<Coordinates> corners = new ArrayList<>();
        corners.add(Coordinates.get(true, array[0], array[2]));
        corners.add(Coordinates.get(true, array[0], array[3]));
        corners.add(Coordinates.get(true, array[1], array[3]));
        corners.add(Coordinates.get(true, array[1], array[2]));
        return corners;
    }

    public static Coordinates getCorner(DIRECTION d, Collection<Coordinates> list) {
        int x = getMinX(list);
        int x1 = getMaxX(list);
        int y = getMinY(list);
        int y1 = getMaxY(list);
        switch (d) {
            case UP_LEFT:
                return Coordinates.get(x, y);
            case UP_RIGHT:
                return Coordinates.get(x1, y);
            case DOWN_RIGHT:
                return Coordinates.get(x1, y1);
            case DOWN_LEFT:
                return Coordinates.get(x, y1);
        }
        return Coordinates.get(x, y);
    }

    public static int[] getMinMaxCoordinates(Collection<Coordinates> list) {
        int x = getMinX(list);
        int x1 = getMaxX(list);
        int y = getMinY(list);
        int y1 = getMaxY(list);
        return new int[]{x, x1, y, y1};
    }

    public static int[] getMinMaxCoordinates(String string) {
        string = string.replace("_", "").trim();
        int[] array = new int[4];
        int i = 0;
        for (String s : string.split(";")) {
            array[i++] = NumberUtils.getIntParse(s.split("-")[0].trim().substring(1));
            array[i++] = NumberUtils.getIntParse(s.split("-")[1]);
        }
        return array;
    }

    public static int getMaxX(Collection<Coordinates> coordinates) {
        int x = Integer.MIN_VALUE;
        for (Coordinates c : coordinates) {
            if (c.x > x) {
                x = c.x;
            }
        }
        return x;
    }

    public static int getMaxY(Collection<Coordinates> coordinates) {
        int y = Integer.MIN_VALUE;
        for (Coordinates c : coordinates) {
            if (c.y > y) {
                y = c.y;
            }
        }
        return y;
    }

    public static int getMinX(Collection<Coordinates> coordinates) {
        int x = Integer.MAX_VALUE;
        for (Coordinates c : coordinates) {
            if (c.x < x) {
                x = c.x;
            }
        }
        return x;
    }

    public static int getMinY(Collection<Coordinates> coordinates) {
        int y = Integer.MAX_VALUE;
        for (Coordinates c : coordinates) {
            if (c.y < y) {
                y = c.y;
            }
        }
        return y;
    }

    public static List<Coordinates> getAdjacentToSquare(List<Coordinates> coordinates) {
        List<Coordinates> list = new ArrayList<>();
        int x1 = getMinX(coordinates);
        int x2 = getMaxX(coordinates);
        int y1 = getMinY(coordinates);
        int y2 = getMaxY(coordinates);
        for (int x = x1 - 1; x < x2 + 1; x++) {
            Coordinates c = Coordinates.get(x, y1 - 1);
            if (!c.isInvalid()) {
                list.add(c);
            }
            Coordinates e = Coordinates.get(x, y2 + 1);
            if (!e.isInvalid()) {
                list.add(e);
            }
        }
        for (int y = y1 - 2; y < y2; y++) {
            Coordinates e = Coordinates.get(x1 - 1, y);
            if (!e.isInvalid()) {
                list.add(e);
            }
            Coordinates e2 = Coordinates.get(x2 + 1, y);
            if (!e2.isInvalid()) {
                list.add(e2);
            }
        }
        return list;
    }

    public static List<Coordinates> getAdjacentToBothGroups(Collection<Coordinates> coordinates,
                                                            Collection<Coordinates> coordinates2) {
        List<Coordinates> list = new ArrayList<>();
        list.addAll(coordinates);
        list.addAll(coordinates2);
        int x = getMinX(list);
        int x1 = getMaxX(list);
        int y = getMinY(list);
        int y1 = getMaxY(list);
        Collection<Coordinates> pool = getCoordinatesWithin(x, x1, y, y1);
        return getAdjacentToBothGroups(pool, coordinates, coordinates2);
    }

    public static List<Coordinates> getCoordinatesWithin(int x, int x1, int y, int y1) {
        return getCoordinatesWithin(x, x1, y, y1, false);
    }

    public static List<Coordinates> getCoordinatesWithin(int x, int x1, int y, int y1, boolean inclusive) {
        if (inclusive) {
            x--;
            y--;
        }
        List<Coordinates> list = new ArrayList<>();
        for (; x1 > x; x1--) {
            for (int y_ = y1; y_ > y; y_--) {
                list.add(Coordinates.get(true, x1, y_));
            }
        }
        return list;
    }

    public static List<Coordinates> getAdjacentToBothGroups(
            Collection<Coordinates> coordinatesPool, Collection<Coordinates> coordinates,
            Collection<Coordinates> coordinates2) {
        List<Coordinates> list = new ArrayList<>();
        Set<Coordinates> adjacent1 = new HashSet<>();
        for (Coordinates c : coordinatesPool) {
            for (Coordinates c1 : coordinates) {
                if (c.isAdjacent(c1)) {
                    adjacent1.add(c1);
                }
            }
        }

        loop:
        for (Coordinates c : adjacent1) {
            for (Coordinates c2 : coordinates2) {
                if (c.isAdjacent(c2)) {
                    list.add(c);
                    continue loop;
                }
            }
        }

        return list;
    }

    public static DIRECTION getClosestEdgeY(Coordinates c, Integer cellsX, Integer cellsY) {
        return getClosestEdge(c, cellsX, cellsY, false);
    }

    public static DIRECTION getClosestEdgeX(Coordinates c, Integer cellsX, Integer cellsY) {
        return getClosestEdge(c, cellsX, cellsY, true);
    }

    public static DIRECTION getClosestEdge(Coordinates c, Integer cellsX, Integer cellsY) {
        return getClosestEdge(c, cellsX, cellsY, null);
    }

    public static Coordinates getCenterCoordinate(Collection<Coordinates> coordinates) {
        int x1 = getMinX(coordinates);
        int x2 = getMaxX(coordinates);
        int y1 = getMinY(coordinates);
        int y2 = getMaxY(coordinates);
        return Coordinates.get((x1 + x2) / 2, (y1 + y2) / 2);

    }

    public static DIRECTION getClosestEdge(Coordinates c, Integer cellsX, Integer cellsY,
                                           Boolean x_or_y_only) {
        FACING_DIRECTION x_dir = null;
        FACING_DIRECTION y_dir = null;
        int min_x_diff = Integer.MAX_VALUE;
        int min_y_diff = Integer.MAX_VALUE;
        for (FACING_DIRECTION d : main.game.bf.directions.FACING_DIRECTION.values) {
            int x_diff = Integer.MAX_VALUE;
            int y_diff = Integer.MAX_VALUE;
            switch (d) {
                case EAST:
                    x_diff = cellsX - c.x;
                    break;
                case NORTH:
                    y_diff = c.y;
                    break;
                case SOUTH:
                    y_diff = cellsY - c.y;
                    break;
                case WEST:
                    x_diff = c.x;
                    break;
            }
            if (x_diff < min_x_diff) {
                x_dir = d;
                min_x_diff = x_diff;
            }
            if (y_diff < min_y_diff) {
                y_dir = d;
                min_y_diff = y_diff;
            }
        }
        if (x_dir == y_dir) {
            return x_dir.getDirection();
        }
        if (x_or_y_only != null) {
            return x_or_y_only ? x_dir.getDirection() : y_dir.getDirection();
        }
        return min_x_diff < min_y_diff ? x_dir.getDirection() : y_dir.getDirection();
    }

    public static Boolean isLineXorY(Collection<Coordinates> list) {
        if (isLine(true, list)) {
            return true;
        }
        if (isLine(false, list)) {
            return false;
        }
        return null;
    }

    public static Boolean isLine(boolean x, Collection<Coordinates> list) {
        Coordinates prev = null;
        for (Coordinates c : list) {
            if (prev == null) {
                prev = c;
                continue;
            }
            if (x) {
                if (prev.y != c.y) {
                    return false;
                } else if (prev.x != c.x) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean isHorizontalLine(Collection<Coordinates> list) {
        return isLine(true, list);
    }

    public static Coordinates getRandomCoordinate(Integer cellsX, Integer cellsY) {
        return Coordinates.get(RandomWizard.getRandomInt(cellsX), RandomWizard.getRandomInt(cellsY));
    }

    public static int getMinDistanceFromEdge(Coordinates c, int dimension, boolean xOrY) {
        return xOrY ? Math.min(dimension - c.x, c.x) : Math.min(dimension - c.y, c.y);
    }

    public static int getMinDistanceFromEdge(Coordinates c, int w, int h) {
        int dstX = Math.min(w - c.x, c.x);
        int dstY = Math.min(h - c.y, c.y);
        return Math.min(dstX, dstY);
    }

    public static int getRangeY(List<Coordinates> coordinates) {
        return getRange(false, coordinates);
    }

    public static int getRangeX(List<Coordinates> coordinates) {
        return getRange(true, coordinates);
    }

    public static int getRange(boolean xOrY, List<Coordinates> coordinates) {
        if (xOrY) {
            return getMaxX(coordinates) - getMinX(coordinates);
        }
        return getMaxY(coordinates) - getMinY(coordinates);
    }

    public static boolean isAdjacent(List<Coordinates> coordinatesKus, Coordinates coordinates) {
        for (Coordinates c : coordinatesKus) {
            if (c.isAdjacent(coordinates)) {
                return true;
            }
        }

        return false;
    }

    public static List<Coordinates> getCoordinatesFromString(String textContent) {
        List<Coordinates> list = new ArrayList<>();
        for (String s : ContainerUtils.open(textContent)) {
            list.add(Coordinates.get(true, s));
        }
        return list;
    }

    public static String getStringFromCoordinates(List<Coordinates> list) {
        StringBuilder textContent = new StringBuilder();
        for (Coordinates c : list) {
            textContent.append(c.toString()).append(";");
        }
        return textContent.toString();
    }

    public static Coordinates getClosestValid(Coordinates coordinates) {
        // TODO Auto-generated method stub
        return coordinates;
    }

    public static List<Coordinates> getCoordinatesWithOffset(List<Coordinates> coordinates,
                                                             int offsetX, int offsetY) {
        List<Coordinates> list = new ArrayList<>();
        for (Coordinates c : coordinates) {
            Coordinates e = Coordinates.get(c.x + offsetX, c.y + offsetY);
            if (e.isInvalid()) {
                continue;
            }
            list.add(e);
        }
        return list;
    }

    public static List<Coordinates> getCoordinatesBetween(Coordinates c, Coordinates c2) {
        return CoordinatesMaster.getCoordinatesWithin(
                Math.min(c.x, c2.x), Math.max(c.x, c2.x), Math.min(c.y, c2.y), Math.max(
                        c.y, c2.y));
    }

    public static List<Coordinates> getCoordinatesBetweenInclusive(Coordinates c, Coordinates c2) {
        return CoordinatesMaster.getCoordinatesWithin(
                Math.min(c.x, c2.x), Math.max(c.x, c2.x), Math.min(c.y, c2.y), Math.max(
                        c.y, c2.y), true);
    }

    public static Set<Coordinates> getZoneCoordinates(ActiveObj entity) {
        Effect effect = EffectMaster.getFirstEffectOfClass(entity,
                SpecialTargetingEffect.class);
        Set<Coordinates> coordinates = null;
        if (effect != null) {
            SpecialTargetingEffect targetEffect = (SpecialTargetingEffect) effect;
            coordinates = targetEffect.getAndNullCoordinates();
        }
        return coordinates;
    }

    public static Coordinates getRandomAdjacentCoordinate(Coordinates coordinates) {
        Loop loop = new Loop(20);
        while (loop.continues()) {
            Coordinates c = coordinates.getAdjacentCoordinate(
                    new RandomWizard<DIRECTION>().getRandomEnumConst(DIRECTION.class));
            if (c == null)
                return c;
        }
        return coordinates;
    }

    public static Coordinates getClosestTo(Coordinates coordinates, List<Coordinates> collect) {
        return collect.stream().sorted(new SortMaster<Coordinates>().getSorterByExpression_(
                c -> (int) -(100 * c.dist(coordinates)))).collect(Collectors.toList()).get(0);
    }

    public static Set<Coordinates> getMissingCoordinatesFromRect(Coordinates c, int w, int h,
                                                                 Set<Coordinates> coordinates) {
        Set<Coordinates> missing = new LinkedHashSet<>();
        for (int i = c.x; i < c.x + w; i++) {
            for (int j = c.y; j < c.y + h; j++) {
                Coordinates c1;
                if (!coordinates.contains(c1 = Coordinates.get(i, j))) {
                    missing.add(c1);
                }
            }
        }
        return missing;
    }

    public static Set<Coordinates> squareToDiamondArea(Set<Coordinates> coordinatesSet) {
        /*
1 1 1
1 1 1

what if it is not square but rectangular? ..
confirm Diagonal on block creation?

merging blocks square-diamond to get a different shape

transform selection
fix selection
3x3 - 1
4x4
5x5 - 2
7x7
         */
        //just cut corners to the middle
        // everything that is beyond both middles...
        int middleX = (getMinX(coordinatesSet) + getMaxX(coordinatesSet)) / 2;
        int middleY = (getMinY(coordinatesSet) + getMaxY(coordinatesSet)) / 2;
        // int x =Math.max(0,  getWidth(coordinatesSet)*3/5 - 3);
        // int y =  Math.max(0, getHeight(coordinatesSet) * 3 / 5 - 3);
        int m = Math.max(getHeight(coordinatesSet), getWidth(coordinatesSet));
        int max = (m - 3) / 2 + 1;
        coordinatesSet.removeIf(c -> {
            int diffX = c.x - middleX;
            int diffY = c.y - middleY;

            return Math.abs(diffX) + Math.abs(diffY) > max;
        });
        return coordinatesSet;
    }

    public static Comparator<Coordinates> getSorter(Coordinates coordinates, boolean closestFirst) {
        return (o1, o2) -> {
            double v1 = o1.dst_(coordinates);
            double v2 = o2.dst_(coordinates);
            if (v1 > v2)
                return closestFirst ? 1 : -1;
            if (v1 < v2)
                return closestFirst ? -1 : 1;
            return 1;
        };
    }

    public static Coordinates[] getInRange(Coordinates c,
                                           int range) {
        Set<Coordinates> set = getInRange_(c, range);
        return set.toArray(new Coordinates[0]);
    }

    public static Set<Coordinates> getInRange_(Coordinates c, int range) {
        Set<Coordinates> set = new HashSet<>();
        set.add(c);
        for (int i = 0; i < range; i++) {
            for (Coordinates adj : c.getAdjacentCoordinates()) {
                set.addAll(getInRange_(adj, i-1));
            }
        }
        return set;
    }

    public boolean isOnEdge(Coordinates c, int border) {
        return false;
    }

    public static int getWidth(Collection<Coordinates> list) {
        int x = getMinX(list);
        int x1 = getMaxX(list);
        return Math.abs(x1 - x) + 1;
    }

    public static int getHeight(Collection<Coordinates> list) {
        int y = getMinY(list);
        int y1 = getMaxY(list);
        return Math.abs(y1 - y) + 1;
    }

    public static Coordinates getUpperLeftCornerCoordinates(Set<Coordinates> list) {
        int x = getMinX(list);
        int y = getMinY(list);
        return Coordinates.get(x, y);
    }

    public static Coordinates getBottomLeft(Collection<Coordinates> list) {
        int x = getMinX(list);
        int y = getMaxY(list);
        return Coordinates.get(x, y);
    }

    public static float getMinDistanceBetweenGroups(Collection<Coordinates> list, Collection<Coordinates> list2,
                                                    float requiredMin) {
        float min = Integer.MAX_VALUE;
        for (Coordinates c : list) {
            for (Coordinates c2 : list2) {
                double dst = c.dst_(c2);
                if (dst < min)
                    min = (float) dst;
                if (dst < requiredMin)
                    return (float) dst;
            }

        }
        return min;
    }


    // public boolean isOnEdgeX(Coordinates coordinates, int border) {
    // return coordinates.getX() - getOffsetX() == 0
    // || coordinates.getX() - getOffsetX() == GuiManager
    // .getBF_CompDisplayedCellsX() - 1;
    // }
    //
    // public boolean isOnEdgeY(Coordinates coordinates) {
    // return coordinates.getY() - getOffsetY() == 0
    // || coordinates.getY() - getOffsetY() == GuiManager
    // .getBF_CompDisplayedCellsY() - 1;
    // }

}
