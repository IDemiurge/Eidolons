package eidolons.game.exploration.dungeon.generator.model;

import eidolons.game.exploration.dungeon.generator.tilemap.TilesMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.*;
import java.util.stream.Collectors;

public class ModelMaster {

    private static Map<FACING_DIRECTION, Integer> exitCounterMap = new HashMap<>();

    public static void resetExitCounterMap() {
        exitCounterMap = new HashMap<>();
        MapMaster.addToIntegerMap(exitCounterMap, FACING_DIRECTION.SOUTH, 1);
        MapMaster.addToIntegerMap(exitCounterMap, FACING_DIRECTION.EAST, 1);
        MapMaster.addToIntegerMap(exitCounterMap, FACING_DIRECTION.WEST, 1);
        MapMaster.addToIntegerMap(exitCounterMap, FACING_DIRECTION.NORTH, 1);
    }

    public static boolean isRoomOnEdge(Room room, LevelModel model) {
        Coordinates p = room.getCoordinates();

        if (p.y - room.getHeight() - 1 <= model.getTopMost())
            return true;
        if (p.y + room.getHeight() + 1 >= model.getBottomMost())
            return true;
        if (p.x + room.getWidth() + 1 >= model.getRightMost())
            return true;
        return p.x - room.getWidth() - 1 >= model.getLeftMost();
    }

    public static List<Coordinates> getPossibleExits(FACING_DIRECTION to, Room room) {
        return Arrays.stream(getWallCoordinatesArray(room, to)).filter(point ->
         canExitAt(point, room)).collect(Collectors.toList());
        //        Coordinates[] wall = getWallCoordinatesArray(room, to);
        //        List<Coordinates> list = new ArrayList<>();
        //        for (Coordinates point : wall) {
        //            if (canExitAt(point, room))
        //                list.add(point);
        //        }
        //        return list;

    }

    public static boolean canExitAt(Coordinates at, Room room) {
        Coordinates c = new AbstractCoordinates(at.x, at.y);
        for (Coordinates adj : c.getAdjacentCoordinates(false)) {
            if (adj.x >= room.getWidth() || adj.x < 0 ||
             adj.y < 0 || adj.y >= room.getHeight())
                continue;
            if (TilesMaster.isPassable(room.getCells()[adj.x][adj.y]))
                return true;

        }
        return false;
    }

    public static Coordinates[] getWallCoordinatesArray(Room room, FACING_DIRECTION side) {
        Coordinates[] wall = side.isVertical() ? new Coordinates[room.getWidth()] : new Coordinates[room.getHeight()];

        for (int i = 0; i < wall.length; i++) {
            int x;
            int y;
            if (side.isVertical()) {
                x = i;
                y = side.isCloserToZero() ? 0 : room.getHeight() - 1;
            } else {
                y = i;
                x = side.isCloserToZero() ? 0 : room.getWidth() - 1;
            }
            wall[i] = new AbstractCoordinates(x, y);
        }

        return wall;
    }

    public static int getAdjacentToVoid(LevelModel model, Room room, FACING_DIRECTION side) {
        int n = 0;
        if (side == null) {
            for (FACING_DIRECTION sub : FACING_DIRECTION.normalFacing) {
                n += getAdjacentToVoid(model, room, sub);
            }
        } else {
            Coordinates[] wall = getWallCoordinatesArray(room, side);
            n = (int) Arrays.stream(wall).filter(point -> {
                Coordinates offset = new AbstractCoordinates(point.x, point.y).getOffsetByX(
                 room.getCoordinates().x).getOffsetByY(room.getCoordinates().y);
                int x = offset.x;
                int y = offset.y;

                //void out there if there will be no more occupied cells in that direction
                return model.getOccupiedCells().stream().filter(p -> {
                    if (side.isVertical()) {
                        if (p.x == x)
                            return side.isCloserToZero()
                             ? p.y < y
                             : p.y > y;
                    } else {
                        if (p.y == y)
                            return side.isCloserToZero()
                             ? p.x < x
                             : p.x > x;

                    }
                    return false;
                }).count() == 0;
            }).count();

            //            if (side == FACING_DIRECTION.WEST) {
            //                wall = room.getCells()[0];
            //            }
            //            if (side == FACING_DIRECTION.EAST) {
            //                wall = room.getCells()[room.getWidth() - 1];
            //            }
            //            if (side == FACING_DIRECTION.NORTH) {
            //                wall = new String[room.getWidth() - 1];
            //                for (String[] strings : room.getCells()) {
            //                    wall[i++] = strings[0];
            //                }
            //            }
            //            if (side == FACING_DIRECTION.SOUTH) {
            //                wall = new String[room.getWidth() - 1];
            //                for (String[] strings : room.getCells()) {
            //                    wall[i++] = strings[room.getHeight() - 1];
            //                }
            //            }

        }
        return n;
    }

    public static List<Coordinates> getCoordinateList(Room room) {
        List<Coordinates> list = new ArrayList<>();
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                list.add(new AbstractCoordinates(x, y).offset(room.getCoordinates()));
            }
        }
        return list;
    }

    public static boolean isBetween(Room r, Room room, Room room2, boolean xOrY) {
        Room closerToZero = room.getCoordinates().getXorY(xOrY) < room.getCoordinates().getXorY(xOrY)
         ? room
         : room2;
        int start = closerToZero.getCoordinates().getXorY(xOrY) +
         (xOrY ? room.getWidth() : room.getHeight());
        int end = (closerToZero == room ? room2 : room).getCoordinates().getXorY(xOrY);

        int n = r.getCoordinates().getXorY(xOrY);
        return n > start && n < end;

    }

    public static List<Room> getEdgeRooms(LevelModel model) {
        return model.getRoomMap().values().stream()
         .filter(room -> room.getExitCoordinates().size() < room.getExits().length)
         .filter(room -> isRoomOnEdge(room, model))
         .sorted(new SortMaster<Room>().getSorterByExpression_(r -> getSorterEdgeValue(r, model)))
         .collect(Collectors.toList());
    }

    public static Integer getSorterEdgeValue(Room r, LevelModel model) {
        int val = 3 * r.getUsedExits().size();
        int dst = CoordinatesMaster.getMinDistanceFromEdge(r.getCoordinates(), model.getCurrentWidth() - r.getWidth(),
         model.getCurrentHeight() - r.getHeight());
        switch (r.getType()) {
            case TREASURE_ROOM:
            case EXIT_ROOM:
                val *= 2;
            case DEATH_ROOM:
            case GUARD_ROOM:
                val /= 2;
        }
        return dst - val;
    }

    public static Integer getExitSortValue(FACING_DIRECTION side, Room r, LevelModel model) {
        Coordinates coordinates = RoomAttacher.adjust(
         r.getCoordinates(), side, r, true, false);
        switch (side) { //distance from the corresponding edge...
            case NORTH:
                return RandomWizard.getRandomInt(16) + model.getTopMost() - coordinates.y - model.getCurrentHeight() / 2
                 - exitCounterMap.get(FACING_DIRECTION.NORTH) * 5;
            case WEST:
                return RandomWizard.getRandomInt(14) + model.getLeftMost() - coordinates.x - model.getCurrentWidth() / 2
                 - exitCounterMap.get(FACING_DIRECTION.WEST) * 5;
            case EAST:
                return RandomWizard.getRandomInt(9) - model.getRightMost() - coordinates.getX() - r.getWidth() - model.getCurrentWidth() / 2
                 - exitCounterMap.get(FACING_DIRECTION.EAST) * 5;
            case SOUTH:
                return RandomWizard.getRandomInt(8) - model.getBottomMost() - coordinates.getY() - r.getHeight() - model.getCurrentHeight() / 2
                 - exitCounterMap.get(FACING_DIRECTION.SOUTH) * 5;
        }
        return null;
    }

    public static Integer getSorterValue(LevelModel model, Coordinates point, boolean n_s, boolean w_e) {
        //        transformed =         model.getOccupiedCells().stream().map(c->
        //         Coordinates.getVar(true, c.x, c.y)).collect(Collectors.toList());

        //        roomExit = room.getExits()[room.getExitCoordinates().size()];
        //        getPrioritizedDirection(room);
        //        int n = ModelMaster.getAdjacentToVoid(model, room, side);
        return model.getTopMost();

        //try to make more square
        // prioritized direction

        //        CoordinatesMaster.getFarmostCoordinateInDirection()
        //         CoordinatesMaster.getEdgeCoordinatesFromSquare()
    }

    public static void exitChosen(FACING_DIRECTION parentExit) {
        MapMaster.addToIntegerMap(exitCounterMap, parentExit, 1);
    }


}
