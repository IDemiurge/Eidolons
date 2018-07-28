package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.Loop;

import java.util.Collection;

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
        return adjust(entranceCoordinates, entrance, model, false);
    }

    public static Coordinates adjust(Coordinates point, FACING_DIRECTION side, RoomModel parent,
                                     boolean getEntranceOrRoomCoordinates) {
        int x = point.x;
        int y = point.y;
        int i = 1;
        if (getEntranceOrRoomCoordinates)
            i = -1;
        int width = parent.getWidth();
        int height = parent.getHeight();

        if (side == FACING_DIRECTION.SOUTH) {
            //  otherwise it is already compensated
            if (parent.isDisplaced())
                if (width % 2 == 0)
                    width++;
            x -= i * width / 2;
            y -= i * height;
        } else if (side == FACING_DIRECTION.NORTH) {
            if (parent.isDisplaced())
                if (width % 2 == 0)
                    width++;
            x -= i * width / 2;
        } else if (side == FACING_DIRECTION.EAST) {
            x -= i * width;
            if (!parent.isDisplaced())
                if (height % 2 == 0)
                    height--;
            y -= i * height / 2;
        } else if (side == FACING_DIRECTION.WEST) {
            if (!parent.isDisplaced())
                if (height % 2 == 0)
                    height -= 1;
            y -= i * height / 2;
        }
        return new AbstractCoordinates(x, y);
    }

    public static Coordinates getAttachCoordinates(Room parent, Room model, FACING_DIRECTION side
    ) {
        return
         adjust(
          adjust(parent.getCoordinates(), side, parent, true),
          FacingMaster.rotate180(side), model, false)
         ;

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

    public Room findFitting(Coordinates entranceCoordinates, EXIT_TEMPLATE roomExitTemplate,
                            ROOM_TYPE roomType, FACING_DIRECTION parentExit, LevelZone zone) {
        Loop loop = new Loop(40); //model.getGenerator().getGeneratorData()
        RoomModel roomModel = null;
        templateMaster.resetSizedRandomRoomPools(zone.getTemplateGroup());

        FACING_DIRECTION roomEntrance = FacingMaster.rotate180
         (parentExit);
        while (true) {
            if (loop.ended()) {
                main.system.auxiliary.log.LogMaster.log(1, "Failed to place " + roomType + " at " +
                 entranceCoordinates + " with parent exit to the " + parentExit);
                return null;
            }
            roomModel = templateMaster.getNextLargestRandomModel(roomType,
             roomExitTemplate
             , roomEntrance, zone.getTemplateGroup());
            if (roomModel == null) {
                templateMaster.resetSizedRandomRoomPools(zone.getTemplateGroup());
                continue;
            }

            Coordinates roomCoordinates = getRoomCoordinates(entranceCoordinates, roomEntrance, roomModel);
            Room room = new Room(roomCoordinates, roomModel);
            room.setZone(zone);
            if (!canPlace(roomModel, roomCoordinates, model.getOccupiedCells(), data.getX(), data.getY())) {
                main.system.auxiliary.log.LogMaster.log(1, "Cannot place " + roomModel + " at " +
                 roomCoordinates + " with parent exit to the " + parentExit + "; rooms N=" +
                 model.getRoomMap().size());

            } else
                return room;

        }
    }


    public void attach(Room to, Room attached, FACING_DIRECTION entrance) {
        Coordinates p = getAttachCoordinates(to, attached, entrance);
        //check
        //        if (!RoomAttacher.canPlace(roomModel, p, getOccupiedCells(), data.getX(), data.getY())) {
        //            main.system.auxiliary.log.LogMaster.log(1, "Cannot place " + roomModel + " at " +
        //             x +
        //             " " + y + "; total rooms=" + roomMap.size() + "; total cells=" + occupiedCells.size());
        //            return null;
        //        }
        //        Coordinates newCoordinates = model.addRoom(p, attached).setNewEntrance(entrance);
        //        model.getRoomMap().remove(p);
        //        model.getRoomMap().put(newCoordinates, attached);

    }


    //        if (parentExit != null) {
    //            Coordinates newCoordinates = room.setNewEntrance(roomEntrance);
    //            model.getRoomMap().remove(roomCoordinates);
    //            model.getRoomMap().put(newCoordinates, room);
    //        }
}
