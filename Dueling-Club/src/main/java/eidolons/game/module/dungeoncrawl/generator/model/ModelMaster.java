package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModelMaster {

    public static boolean isRoomOnEdge(Room room, LevelModel model) {
        Coordinates p = room.getCoordinates();

        if (p.y - room.getHeight() - 1 <= model.getTopMost())
            return true;
        if (p.y + room.getHeight() + 1 >= model.getBottomMost())
            return true;
        if (p.x + room.getWidth() + 1 >= model.getRightMost())
            return true;
        if (p.x - room.getWidth() - 1 >= model.getLeftMost())
            return true;
        return false;
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
        Coordinates c = new AbstractCoordinates( at.x, at.y);
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
            int x = 0;
            int y = 0;
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
            n= (int) Arrays.stream(wall).filter(point -> {
                Coordinates offset = new AbstractCoordinates(  point.x, point.y).getOffsetByX(
                 room.getCoordinates().x).getOffsetByY(room.getCoordinates().y);
                int x= offset.x;
                int y= offset.y;

                //void out there if there will be no more occupied cells in that direction
             return    model.getOccupiedCells().stream().filter(p->{
                 if (side.isVertical()) {
                     if (p.x==x)
                         return side.isCloserToZero()
                          ? p.y< y
                          : p.y> y;
                 }else{
                     if (p.y==y)
                         return side.isCloserToZero()
                          ? p.x< x
                          : p.x> x;

                 }
                 return false;
                }).count()==0;
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
        List<Coordinates> list=     new ArrayList<>() ;
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                list.add(new AbstractCoordinates(x, y));
            }
        }
        return list;
    }
}
