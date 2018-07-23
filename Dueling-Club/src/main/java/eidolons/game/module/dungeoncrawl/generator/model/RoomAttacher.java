package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.PointX;
import main.system.auxiliary.Loop;

import java.awt.*;

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

    public static Point getRoomPoint(Point entrancePoint, FACING_DIRECTION entrance, RoomModel model) {
        return adjust(entrancePoint, entrance, model, false);
    }

    public static Point adjust(Point point, FACING_DIRECTION side, RoomModel parent,
                               boolean getEntranceOrRoomPoint) {
        int x = point.x;
        int y = point.y;
        int i = 1;
        if (getEntranceOrRoomPoint)
            i = -1;
        int width = parent.getWidth();
        int height = parent.getHeight();

        if (side == FACING_DIRECTION.SOUTH) {
            x -= i * width / 2; //centered ...
            y -= i * height;
        } else if (side == FACING_DIRECTION.NORTH) {
            x -= i * width / 2;
        } else if (side == FACING_DIRECTION.EAST) {
            x -= i * width;
            y -= i * height / 2;
        } else if (side == FACING_DIRECTION.WEST) {
            y -= i * height / 2;
        }
        return new PointX(x, y);
    }

    public static Point getAttachPoint(Room parent, Room model, FACING_DIRECTION side
    ) {
        return
         adjust(
          adjust(parent.getPoint(), side, parent, true),
          FacingMaster.rotate180(side), model, false)
         ;

    }

    public static boolean canPlace(
     RoomModel roomModel, Point p, java.util.List<Point> cells, int w, int h) {
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
                if (cells.contains(new PointX(x, y)))
                    return false;
            }
        }
        return true;
    }

    public Room findFittingAndAttach(Point entrancePoint, EXIT_TEMPLATE roomExitTemplate,
                                     ROOM_TYPE roomType, FACING_DIRECTION parentExit, LevelZone zone) {
        Loop loop = new Loop(5);
        RoomModel roomModel = null;
        templateMaster.resetSizedRandomRoomPools(zone.getTemplateGroup());

        FACING_DIRECTION roomEntrance = FacingMaster.rotate180
         (parentExit);
        while (true) {
            if (loop.ended())
            {
                main.system.auxiliary.log.LogMaster.log(1, "Failed to place " + roomType + " at " +
                 entrancePoint + " with parent exit to the " + parentExit);
                return null;
            }
            roomModel = templateMaster.getNextLargestRandomModel(roomType,
             roomExitTemplate
             , roomEntrance,zone.getTemplateGroup());
            if (roomModel == null) {
                templateMaster.resetSizedRandomRoomPools(zone.getTemplateGroup());
                continue;
            }

            Point roomPoint = getRoomPoint(entrancePoint, roomEntrance, roomModel);
            Room room = new Room(roomModel);
            room.setPoint(roomPoint);
            if (!canPlace(roomModel, roomPoint, model.getOccupiedCells(), data.getX(), data.getY())) {
                main.system.auxiliary.log.LogMaster.log(1, "Cannot place " + roomModel + " at " +
                 roomPoint + " with parent exit to the " + parentExit+ "; rooms N=" +
                 model.getRoomMap().size());
                  continue;
            }
            main.system.auxiliary.log.LogMaster.log(1, "Placing  " + room + " at " +
             roomPoint + "; "+ " with parent exit to the " + parentExit);
                model.addRoom(roomPoint, room);
                return room;
        }
    }


    public void attach(Room to, Room attached, FACING_DIRECTION entrance) {
        Point p = getAttachPoint(to, attached, entrance);
        //check
        //        if (!RoomAttacher.canPlace(roomModel, p, getOccupiedCells(), data.getX(), data.getY())) {
        //            main.system.auxiliary.log.LogMaster.log(1, "Cannot place " + roomModel + " at " +
        //             x +
        //             " " + y + "; total rooms=" + roomMap.size() + "; total cells=" + occupiedCells.size());
        //            return null;
        //        }
        //        Point newPoint = model.addRoom(p, attached).setNewEntrance(entrance);
        //        model.getRoomMap().remove(p);
        //        model.getRoomMap().put(newPoint, attached);

    }


    //        if (parentExit != null) {
    //            Point newPoint = room.setNewEntrance(roomEntrance);
    //            model.getRoomMap().remove(roomPoint);
    //            model.getRoomMap().put(newPoint, room);
    //        }
}
