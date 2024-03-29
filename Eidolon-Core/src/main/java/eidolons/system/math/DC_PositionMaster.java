package eidolons.system.math;

import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.data.XList;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.math.PositionMaster;
import main.system.math.PositionMaster.SHAPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DC_PositionMaster {
    public static Set<Coordinates> getShapedCoordinates(
     Coordinates baseCoordinate,  int width,
     int height,DIRECTION direction, SHAPE shape) {
        Set<Coordinates> list = new HashSet<>();
        List<Coordinates> line;
        switch (shape) {
            case CROSS:
                list.addAll(getLine(direction, baseCoordinate, height));
                list.addAll(getLine(direction.flip()
                        , baseCoordinate, height));
                list.addAll(getLine(direction.rotate90(false)
                        , baseCoordinate, width));
                list.addAll(getLine(direction.rotate90(true)
                        , baseCoordinate, width));
                break;
            case STAR:
                // four diagonals
                list.addAll(getLine(direction.rotate45(true), baseCoordinate, height));
                list.addAll(getLine(direction.rotate45(false), baseCoordinate, width));
                list.addAll(getLine(direction.rotate90(true).rotate45(true),
                        baseCoordinate, width));
                list.addAll(getLine(direction.rotate90(false).rotate45(false),
                        baseCoordinate, height));
                break;
            case CONE:
                // define a filter function based on facing and apply base
                // coordinates

                // getOrCreate horizontal lines of increasing width
                for (int i = 0; i < height; i++) {

                    list.addAll(getLine(direction.rotate90(false)
                     , baseCoordinate, width));
                    list.addAll(getLine(direction.rotate90(true)
                     , baseCoordinate, width));
                    baseCoordinate = baseCoordinate
                     .getAdjacentCoordinate(direction);
                    if (baseCoordinate == null) {
                        break;
                    }
                    width++;
                }

                break;
            case RECTANGLE:
                list.addAll(getLine(direction, baseCoordinate,
                 height));
                line = getLine(direction, baseCoordinate, height);
                for (Coordinates c : line) {
                    list.addAll(getLine(direction.rotate90(false)
                     , c, width));
                    list.addAll(getLine(direction.rotate90(true)
                     , c, width));
                }
                line.remove(baseCoordinate);
                list.addAll(line);
                // for (Coordinates c : getLine(FacingManager
                // .rotate(facing, false), baseCoordinate,
                // width)) {
                // if (c == baseCoordinate)
                // continue;
                // list.addAll(getLine(facing, c, height));
                // }
                // for (Coordinates c : getLine(FacingManager.rotate(facing,
                // true)
                // , baseCoordinate, width)) {
                // if (c == baseCoordinate)
                // continue;
                // list.addAll(getLine(facing, c, height));
                // }
                break;
        }
        return list;
    }

    public static List<Coordinates> getRectangle(
     DIRECTION lengthDirection, Coordinates baseCoordinate,
     int length, int width) {
        return getRectangle(lengthDirection,
         lengthDirection.rotate90(true), baseCoordinate,
         length, width);
    }

    public static List<Coordinates> getRectangle(
     DIRECTION lengthDirection, DIRECTION widthDirection,
     Coordinates baseCoordinate, int length, int width) {
        return getRectangle(false, lengthDirection, widthDirection,
         baseCoordinate, length, width);
    }

    public static List<Coordinates> getRectangle(boolean allowInvalid,
                                                 DIRECTION lengthDirection, DIRECTION widthDirection,
                                                 Coordinates baseCoordinate, int length, int width) {
        XList<Coordinates> list = new XList();
        length--;
        if (length < 0) {
            return list;
        }
        width--;
        if (width < 0) {
            return list;
        }
        List<Coordinates> line = getLine(allowInvalid,
         lengthDirection, baseCoordinate, length);
        list.addAllUnique(line);
        for (Coordinates c : line) {
            list.addAllUnique(getLine(allowInvalid,
             widthDirection, c, width));
        }
        return list;
    }

    public static List<Coordinates> getLine(DIRECTION direction,
                                            Coordinates baseCoordinate, int length) {
        return getLine(false, direction, baseCoordinate, length);
    }

    public static List<Coordinates> getLine(boolean allowInvalid,
                                            DIRECTION direction, Coordinates baseCoordinate, int length) {
        Coordinates c = baseCoordinate;
        List<Coordinates> list = new ArrayList<>();
        list.add(c);
        for (int i = 0; i < length; i++) {

            c = c.getAdjacentCoordinate(allowInvalid, direction);
            if (c == null) {
                break;
            }
            list.add(c);
        }
        return list;

    }
    public static List<Coordinates> getLine(Coordinates origin, Coordinates destination) {
        DIRECTION d = DirectionMaster.getRelativeDirection(origin, destination);
        return getLine(d, origin, PositionMaster.getDistance(origin, destination));
    }

    public static Coordinates getRandomValidAdjacent(Coordinates originalCoordinates, Unit unit) {
        for (Coordinates coordinates : originalCoordinates.getAdjacentCoordinates()) {
            GridCell cell = unit.getGame().getCell(coordinates);
            if (cell==null )
                continue;
            if (unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, coordinates))
                return coordinates;

        }
        return originalCoordinates;
    }

}
