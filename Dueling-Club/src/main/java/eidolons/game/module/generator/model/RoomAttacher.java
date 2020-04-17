package eidolons.game.module.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.generator.LevelData;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.Loop;

import java.util.Arrays;
import java.util.Collection;

import static main.game.bf.directions.FACING_DIRECTION.*;
import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomAttacher {

    private final LevelData data;
    private final LevelModel model;
    private final RoomTemplateMaster templateMaster;

    public RoomAttacher(LevelData data, LevelModel model, RoomTemplateMaster templateMaster) {
        this.data = data;
        this.model = model;
        this.templateMaster = templateMaster;
    }

    public static Coordinates getRoomCoordinates(Coordinates entranceCoordinates, FACING_DIRECTION entrance, RoomModel model) {
        return adjust(entranceCoordinates, entrance, model, false, false);
    }

    public static Coordinates adjust(Coordinates point, FACING_DIRECTION side, RoomModel parent,
                                     boolean getEntranceOrRoomCoordinates) {
        return adjust(point, side, parent, getEntranceOrRoomCoordinates, true);
    }

    public static Coordinates adjust(Coordinates point, FACING_DIRECTION side, RoomModel parent,
                                     boolean getEntranceOrRoomCoordinates, boolean canAdjustEven) {

        return adjust(point, side, parent.getWidth(), parent.getHeight(), getEntranceOrRoomCoordinates, canAdjustEven);
    }

    public static Coordinates adjust(Coordinates key, FACING_DIRECTION parentExit,
                                     int w, int h, boolean getEntranceOrRoomCoordinates, boolean canAdjustEven) {
        return adjust(key, parentExit, null, w, h, getEntranceOrRoomCoordinates, false);
    }

    public static Coordinates adjust(Coordinates point, FACING_DIRECTION side, RoomModel parent,
                                     int width, int height, boolean getEntranceOrRoomCoordinates, boolean canAdjustEven) {
        int x = point.x;
        int y = point.y;
        int i;
        if (getEntranceOrRoomCoordinates)
            i = -1;
        else
            i = 1;

        if (side == SOUTH) {
            //  otherwise it is already compensated
            if (canAdjustEven)
                if (width % 2 == 0)
                    width = adjustDimension(width, true, parent);
            x -= i * width / 2;
            y -= i * height;
        } else if (side == NORTH) {
            if (canAdjustEven)
                if (width % 2 == 0)
                    width = adjustDimension(width, true, parent);
            x -= i * width / 2;
        } else if (side == EAST) {
            x -= i * width;
            if (canAdjustEven)
                if (height % 2 == 0)
                    height = adjustDimension(height, false, parent);
            y -= i * height / 2;
        } else if (side == WEST) {
            if (canAdjustEven)
                if (height % 2 == 0)
                    height = adjustDimension(height, false, parent);
            y -= i * height / 2;
        }
        return new AbstractCoordinates(x, y);
    }

    private static int adjustDimension(int value, boolean xOrY, RoomModel parent) {
        if (!parent.isDisplaced())
            if (isAdjustDisplacedOnly())
                return value;
        if (xOrY) {
            if (!isEvenDimensionAdjustedX())
                return value;
            value--;
        } else {
            if (!isEvenDimensionAdjustedY())
                return value;
            value--;
        }
        return value;
    }

    private static boolean isAdjustDisplacedOnly() {
        return false;
    }

    private static boolean isEvenDimensionAdjustedX() {
        return true;
    }

    private static boolean isEvenDimensionAdjustedY() {
        return true;
    }


    public static boolean canPlace(
     RoomModel roomModel, Coordinates p, Collection<Coordinates> cells, int w, int h) {
        for (int x = p.x + 1; x + 1 < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y + 1; y + 1 < p.y + roomModel.getHeight(); y++) {
                //                if (p.x < 0)
                //                    return false;
                //                if (p.y < 0)
                //                    return false;
                //                if (p.x >= w)
                //                    return false;
                //                if (p.y >= h)
                //                    return false;
                if (cells.contains(new AbstractCoordinates(x, y)))
                    return false;
            }
        }
        return true;
    }

    public void alignExits(Room parent,
                           Room child) {
        //        if (child.getType() != ROOM_TYPE.EXIT_ROOM && parent.getExitTemplate() != EXIT_TEMPLATE.ANGLE)
        //            return;
        int offset = Traverser.getExitsOffset(parent, child);
        if (offset == 0)
            return;
        if (Math.abs(offset) > 2)
            return;
        FACING_DIRECTION to = child.getEntrance().isVertical() ? EAST : SOUTH;
        if (offset < 0)
            to = child.getEntrance().isVertical() ? WEST : NORTH;
        log(1, "ROOM OFFSET: " + child + " to the " + to);
        model.offset(child, to);
    }

    public Room findFitting(Coordinates entranceCoordinates, EXIT_TEMPLATE roomExitTemplate,
                            ROOM_TYPE roomType, FACING_DIRECTION parentExit, LevelZone zone) {
        Loop loop = new Loop(40); //model.getGenerator().getGeneratorData()
        RoomModel roomModel = null;
        templateMaster.resetSizedRandomRoomPools(zone.getTemplateGroup());

        FACING_DIRECTION roomEntrance = FacingMaster.rotate180
         (parentExit);
        while (true) {
            if (loop.ended()) {
                break;
            }
            roomModel = templateMaster.getNextRandomModel(roomType,
             roomExitTemplate
             , roomEntrance, zone.getTemplateGroup());
            if (roomModel == null) {
                //                templateMaster.resetSizedRandomRoomPools(zone.getTemplateGroup());
                //                continue;
                if (roomExitTemplate == EXIT_TEMPLATE.CROSSROAD)
                    break;
                else return findFitting(entranceCoordinates, EXIT_TEMPLATE.CROSSROAD, roomType, parentExit, zone);
            }

            Coordinates roomCoordinates = getRoomCoordinates(entranceCoordinates, roomEntrance,
             roomModel);
            Room room = new Room(roomCoordinates, roomModel, roomEntrance);
            room.setZone(zone);
            if (!canPlace(roomModel, roomCoordinates, model.getOccupiedCells(), data.getX(), data.getY())) {
                log(1, "Cannot place " + roomModel.getType() + " at " +
                 roomCoordinates + " with parent exit to the " + parentExit + "; rooms N=" +
                 model.getRoomMap().size());
                continue;
            }
            if (room.getType() != ROOM_TYPE.ENTRANCE_ROOM && roomEntrance != null)
                if (!Traverser.checkEntrancesPassable(room)) {
                    log(1, "Cannot traverse\n " + roomModel.getType() + "\n from " +
                     roomEntrance + "\n rotations: " + Arrays.deepToString(room.getRotations()));
                    if (room.getRotations() == null || room.getRotations().length == 0)
                        continue;
                    continue;
                }

            return room;


        }

        log(1, "Failed to place " + roomType + " at " +
         entranceCoordinates + " with parent exit to the " + parentExit);
        return null;
    }


}
