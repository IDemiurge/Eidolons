package main.game.module.dungeoncrawl.dungeon;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;
import main.test.frontend.FAST_DC;

import java.util.LinkedList;
import java.util.List;

public class DungeonLevelMaster {
    public static final String TEST_ENTRANCE_DATA = "Crystal Cave(Dark Tunnel=corner,center);";

    public static final String EXIT_PREFIX = ", exit ";
    public static final String EXIT_SUFFIX = "!exit";
    public static final String ENTRANCE_PREFIX = ", enter ";
    public static final String ENTRANCE_SUFFIX = "!enter ";
    public static final String LEVEL_NAME_SEPARATOR = ",";

    public static final String ENTRANCE_SEPARATOR = ";";

    static Coordinates testEntranceCoordinates;
    private static int lowerMostZ = -1;
    private static int upperMostZ = 1;

    private static Boolean sublevelTestOn = false;

    public static boolean isSublevelTestOn() {
        if (sublevelTestOn != null) {
            return sublevelTestOn;
        }
        if (FAST_DC.isRunning()) {
            return true;
        }
        if (CoreEngine.isLevelEditor()) {
            return true;
        }
        return false;
    }

    public static void generateEntranceData(Dungeon dungeon) {

    }

    private static boolean isCenter(DungeonPlan plan, Coordinates coordinates, boolean xOrY) {
        return Math.min(coordinates.getXorY(xOrY), plan.getDimension(xOrY)
                - coordinates.getXorY(xOrY)) > plan.getCellsY() / 4;
    }

    private static boolean isMiddle(DungeonPlan plan, Coordinates coordinates, boolean xOrY) {
        return Math.min(coordinates.getXorY(xOrY), plan.getDimension(xOrY)
                - coordinates.getXorY(xOrY)) > plan.getCellsY() / 6;
    }

    public static ENTRANCE_LAYOUT getLayout(DungeonPlan plan, Coordinates coordinates) {
        Boolean middle_center_null = null;
        Boolean east_west_null = null;
        Boolean south_north_null = null;
        // maybe start with isMiddle?
        // if both are middle then it's center more or less, right?
        // middle - if either x or y is ON EDGE
        // center - if both are at least middle...
        pos:
        while (true) {
            if (isMiddle(plan, coordinates, true)) {
                middle_center_null = true;
                if (isCenter(plan, coordinates, true)) {
                    middle_center_null = false;
                }

                if (isCenter(plan, coordinates, false)) {
                    middle_center_null = false;
                    break pos; // CENTER
                } else {
                    DIRECTION edge = CoordinatesMaster.getClosestEdge(coordinates,
                            plan.getCellsX(), plan.getCellsY());
                    if (edge.isVertical()) {
                        south_north_null = (CoordinatesMaster.getClosestEdge(coordinates, plan
                                .getCellsX(), plan.getCellsY()) == DIRECTION.DOWN);
                    } else {
                        east_west_null = (CoordinatesMaster.getClosestEdge(coordinates, plan
                                .getCellsX(), plan.getCellsY()) == DIRECTION.RIGHT);
                    }
                    break pos; // CENTER_SIDE
                }

            }

            if (isMiddle(plan, coordinates, false)) {
                middle_center_null = true;
                if (isCenter(plan, coordinates, false)) {
                    middle_center_null = false;
                }
                if (isCenter(plan, coordinates, true)) {
                    middle_center_null = true;
                    break pos; // CENTER
                } else {
                    DIRECTION edge = CoordinatesMaster.getClosestEdge(coordinates,
                            plan.getCellsX(), plan.getCellsY());
                    if (edge.isVertical()) {
                        south_north_null = (CoordinatesMaster.getClosestEdge(coordinates, plan
                                .getCellsX(), plan.getCellsY()) == DIRECTION.DOWN);
                    } else {
                        east_west_null = (CoordinatesMaster.getClosestEdge(coordinates, plan
                                .getCellsX(), plan.getCellsY()) == DIRECTION.RIGHT);
                        break pos; // CENTER_SIDE
                    }
                }
            } else {
                south_north_null = coordinates.y > plan.getCellsY() - coordinates.y;
                east_west_null = coordinates.x > plan.getCellsX() - coordinates.x;
                break pos;
                // CORNER
            }
        }
        // if (CoordinatesMaster.getClosestEdgeY(coordinates,
        // plan.getCellsX(), plan.getCellsY())
        // == DIRECTION.DOWN) return mi
        if (middle_center_null == null) {
            if (east_west_null) {
                return south_north_null ? ENTRANCE_LAYOUT.SOUTH_EAST : ENTRANCE_LAYOUT.NORTH_EAST;
            }
            return south_north_null ? ENTRANCE_LAYOUT.SOUTH_WEST : ENTRANCE_LAYOUT.NORTH_WEST;
        } else {
            if (middle_center_null) {
                if (south_north_null != null) {
                    return south_north_null ? ENTRANCE_LAYOUT.MIDDLE_SOUTH
                            : ENTRANCE_LAYOUT.MIDDLE_NORTH;
                }
                return east_west_null ? ENTRANCE_LAYOUT.MIDDLE_EAST : ENTRANCE_LAYOUT.MIDDLE_WEST;
            } else {
                if (south_north_null != null) {
                    return south_north_null ? ENTRANCE_LAYOUT.CENTER_SOUTH
                            : ENTRANCE_LAYOUT.CENTER_NORTH;
                }
                if (east_west_null == null) {
                    return ENTRANCE_LAYOUT.CENTER;
                }
                return east_west_null ? ENTRANCE_LAYOUT.CENTER_EAST : ENTRANCE_LAYOUT.CENTER_WEST;
            }
        }
    }

    public static Coordinates getEntranceCoordinates(FACING_DIRECTION side,
                                                     ENTRANCE_POINT_TEMPLATE t, Dungeon sublevel) {
        int x = sublevel.getCellsX();
        int y = sublevel.getCellsY();
        // if (isSublevelTestOn())
        // return new Coordinates(x / 2, y / 2);
        if (t == null) {
            t = ENTRANCE_POINT_TEMPLATE.ZONE_RANDOM;
        }

        switch (t) {
            case CENTER:
            case CENTER_OFFSET:
                return new Coordinates(x / 2 + getOffset(t, x, side), y / 2 + getOffset(t, y, side));
            case CORNER:
                switch (side) {
                    case EAST:
                        return new Coordinates(x - 1, y - 1);
                    case NORTH:
                        return new Coordinates(x - 1, 0);
                    case SOUTH:
                        return new Coordinates(0, y - 1);
                    case WEST:
                        return new Coordinates(0, 0);
                }
            case MIDDLE:
                switch (side) {
                    case EAST:
                        return new Coordinates(x - 1, y / 2);
                    case NORTH:
                        return new Coordinates(x / 2, 0);
                    case SOUTH:
                        return new Coordinates(x / 2, y - 1);
                    case WEST:
                        return new Coordinates(0, y / 2);
                }
            case SIDE:
                return new Coordinates(x / 2, y / 2);
            case ZONE_RANDOM:
                return new Coordinates(x / 2, y / 2);
            case ZONE_SIDE:
                return new Coordinates(x / 2, y / 2);
        }

        return null;

    }

    private static int getOffset(ENTRANCE_POINT_TEMPLATE t, int x, FACING_DIRECTION side) {
        if (t != ENTRANCE_POINT_TEMPLATE.CENTER_OFFSET) {
            return 0;
        }
        int perc = 0;
        switch (side) {
            case SOUTH:
            case EAST:
                perc = 25;
                break;
            case NORTH:
            case WEST:
                perc = -25;
                break;
        }
        return perc * x / 100;
    }

    // boolean trap, boolean door
    public static List<Dungeon> getAvailableDungeons(Unit unit) {
        List<Dungeon> list = new LinkedList<>();
//        for (Dungeon dungeon : DungeonMaster.getDungeons()) {
//            for (Entrance e : dungeon.getEntrances()) {
//                if (e.isOpen()) {
//                    if (e.getCoordinates().equals(unit.getCoordinates())) {
//                        list.add(dungeon);
//                        continue;
//                    }
//                }
//            }
//        }
        return list;
    }

    public static List<Entrance> getAvailableDungeonEntrances(Unit unit) {
        List<Entrance> list = new LinkedList<>();
//        for (Dungeon dungeon : DungeonMaster.getDungeons()) {
//            for (Entrance e : dungeon.getEntrances()) {
//                if (e.isOpen()) {
//                    if (e.getCoordinates().equals(unit.getCoordinates())) {
//                        list.add(e);
//                        continue;
//                    }
//                }
//            }
//        }
        return list;
    }

    public static int getNextZ(boolean down) {
        if (down) {
            lowerMostZ--;
            return lowerMostZ;
        }
        upperMostZ++;
        return upperMostZ;
    }

    public static ENTRANCE_LAYOUT transformLayout(ENTRANCE_LAYOUT entranceLayout, Boolean flipX,
                                                  Boolean flipY, Boolean rotate) {
        if (flipY != null) {
            entranceLayout = applyFlipY(entranceLayout, flipY);
        }
        if (flipX != null) {
            entranceLayout = applyFlipX(entranceLayout, flipX);
        }

        return entranceLayout;
    }

    public static ENTRANCE_LAYOUT applyFlipY(ENTRANCE_LAYOUT entranceLayout, Boolean flipY) {
        switch (entranceLayout) {
            case CENTER_NORTH:
                return ENTRANCE_LAYOUT.CENTER_SOUTH;
            case CENTER_SOUTH:
                return ENTRANCE_LAYOUT.CENTER_NORTH;

            case MIDDLE_NORTH:
                return ENTRANCE_LAYOUT.MIDDLE_SOUTH;
            case MIDDLE_SOUTH:
                return ENTRANCE_LAYOUT.MIDDLE_NORTH;

            case NORTH_EAST:
                return ENTRANCE_LAYOUT.SOUTH_EAST;
            case NORTH_WEST:
                return ENTRANCE_LAYOUT.SOUTH_WEST;

            case SOUTH_EAST:
                return ENTRANCE_LAYOUT.NORTH_EAST;
            case SOUTH_WEST:
                return ENTRANCE_LAYOUT.NORTH_WEST;
            default:
                break;
        }
        return entranceLayout;
    }

    public static Coordinates getEntranceCoordinates(ENTRANCE_LAYOUT entranceLayout, Dungeon dungeon) {
        FACING_DIRECTION side;
        ENTRANCE_POINT_TEMPLATE template = null;
        switch (entranceLayout) {
            case CENTER:
            case CENTER_EAST:
            case CENTER_NORTH:
            case CENTER_SOUTH:
            case CENTER_WEST:
                template = ENTRANCE_POINT_TEMPLATE.CENTER;
                break;
            case MIDDLE_EAST:
            case MIDDLE_NORTH:
            case MIDDLE_SOUTH:
            case MIDDLE_WEST:
                template = ENTRANCE_POINT_TEMPLATE.MIDDLE;
                break;
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_EAST:
            case SOUTH_WEST:
                template = ENTRANCE_POINT_TEMPLATE.CORNER;
                break;

        }
        // return new Coordinates(x - 1, y - 1);
        // case NORTH:
        // return new Coordinates(x - 1, 0);
        // case SOUTH:
        // return new Coordinates(0, y - 1);
        // case WEST:
        // return new Coordinates(0, 0);
        side = FACING_DIRECTION.SOUTH;
        return getEntranceCoordinates(side, template, dungeon);
    }

    public static ENTRANCE_LAYOUT applyFlipX(ENTRANCE_LAYOUT entranceLayout, Boolean flipX) {
        switch (entranceLayout) {
            case CENTER_WEST:
                return ENTRANCE_LAYOUT.CENTER_EAST;
            case CENTER_EAST:
                return ENTRANCE_LAYOUT.CENTER_WEST;

            case MIDDLE_WEST:
                return ENTRANCE_LAYOUT.MIDDLE_EAST;
            case MIDDLE_EAST:
                return ENTRANCE_LAYOUT.MIDDLE_WEST;

            case NORTH_EAST:
                return ENTRANCE_LAYOUT.NORTH_WEST;
            case NORTH_WEST:
                return ENTRANCE_LAYOUT.NORTH_EAST;

            case SOUTH_EAST:
                return ENTRANCE_LAYOUT.SOUTH_WEST;
            case SOUTH_WEST:
                return ENTRANCE_LAYOUT.SOUTH_EAST;
            default:
                break;
        }
        return entranceLayout;
    }

    public static ENTRANCE_LAYOUT getEnterLayout(String fileName) {
        String entranceString = null;
        try {
            entranceString = fileName.substring(
                    fileName.indexOf(DungeonLevelMaster.ENTRANCE_PREFIX),
                    fileName.indexOf(DungeonLevelMaster.ENTRANCE_SUFFIX)).split("-")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        ENTRANCE_LAYOUT entranceLayout = new EnumMaster<ENTRANCE_LAYOUT>().retrieveEnumConst(
                ENTRANCE_LAYOUT.class, entranceString);
        return entranceLayout;
    }

    public static ENTRANCE_LAYOUT getExitLayout(String fileName) {
        String exitString = null;
        try {
            exitString = fileName.substring(fileName.indexOf(DungeonLevelMaster.EXIT_PREFIX),
                    fileName.indexOf(DungeonLevelMaster.EXIT_SUFFIX)).split("-")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        ENTRANCE_LAYOUT exitLayout = new EnumMaster<ENTRANCE_LAYOUT>().retrieveEnumConst(
                ENTRANCE_LAYOUT.class, exitString);
        return exitLayout;
    }

    public static String getEntranceData(Dungeon sublevel, ENTRANCE_LAYOUT entranceLayout) {
        // TODO Auto-generated method stub
        return null;
    }

    public enum ENTRANCE_LAYOUT {
        MIDDLE_SOUTH, MIDDLE_NORTH, MIDDLE_WEST, MIDDLE_EAST,

        SOUTH_WEST, SOUTH_EAST, NORTH_WEST, NORTH_EAST,

        CENTER, CENTER_SOUTH, CENTER_EAST, CENTER_WEST, CENTER_NORTH,
    }

    public enum ENTRANCE_POINT_TEMPLATE {
        ZONE_SIDE, ZONE_RANDOM, CENTER, MIDDLE, SIDE, CORNER, CENTER_OFFSET;
    }
    // if (isCenter(plan, coordinates, true)) {
    // middle_center_null = false;
    // break pos;
    // }
    // if (!isCenter(plan, coordinates, false)) {
    // if (!middle_center_null && isMiddle(plan, coordinates,
    // false))
    // middle_center_null = true;
    // else {
    // south_north_null =
    // (CoordinatesMaster.getClosestEdge(coordinates, plan
    // .getCellsX(), plan.getCellsY()) == DIRECTION.DOWN);
    // }
    // } else {
    // south_north_null =
    // (CoordinatesMaster.getClosestEdge(coordinates, plan
    // .getCellsX(), plan.getCellsY()) == DIRECTION.DOWN);
    // middle_center_null = true;
    // }
    // south_north_null = coordinates.y > plan.getCellsY() -
    // coordinates.y;
    // else south_north_null = coordinates.y > plan.getCellsY() -
    // coordinates.y;
    // else if (isCenter(plan, coordinates, false)) {
    // if (isCenter(plan, coordinates, true)) {
    // middle_center_null = false;
    // } else if (isMiddle(plan, coordinates, true)) {
    // east_west_null = (CoordinatesMaster.getClosestEdge(coordinates,
    // plan
    // .getCellsX(), plan.getCellsY()) == DIRECTION.RIGHT);
    // middle_center_null = true;
    // } else {
    // east_west_null = coordinates.x > plan.getCellsX() -
    // coordinates.x;
    // }
}
