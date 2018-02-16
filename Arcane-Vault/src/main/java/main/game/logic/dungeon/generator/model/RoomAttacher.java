package main.game.logic.dungeon.generator.model;

import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.logic.dungeon.generator.LevelData;
import main.game.logic.dungeon.generator.graph.LevelGraph;
import main.game.logic.dungeon.generator.graph.LevelGraphEdge;
import main.game.logic.dungeon.generator.graph.LevelGraphNode;

import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomAttacher {

    private final LevelData data;
    private final LevelModel model;

    public RoomAttacher(LevelData data, LevelModel model) {
        this.data = data;
        this.model = model;
    }

    public static FACING_DIRECTION[] getExits(LevelGraphNode node, LevelGraph graph) {
        return new FACING_DIRECTION[0];
    }

    public static FACING_DIRECTION[] getExits(EXIT_TEMPLATE exitTemplate, Boolean[] rotated) {
        FACING_DIRECTION[] exits = getExits(exitTemplate);
        int i = 0;
        if (rotated != null)
            for (Boolean bool : rotated) {
                i = 0;
                for (FACING_DIRECTION exit : exits) {
                    exits[i] = FacingMaster.rotate(exit, bool);
                    i++;
                }

            }
        return exits;
    }

    //WEST is default ENTRANCE
    public static FACING_DIRECTION[] getExits(EXIT_TEMPLATE exitTemplate) {
        switch (exitTemplate) {
            case THROUGH:
                return new FACING_DIRECTION[]{
                 FACING_DIRECTION.EAST
                };
            case ANGLE:
                return new FACING_DIRECTION[]{
                 FACING_DIRECTION.SOUTH
                };
            case CROSSROAD:
                return new FACING_DIRECTION[]{
                 FACING_DIRECTION.NORTH, FACING_DIRECTION.EAST, FACING_DIRECTION.SOUTH
                };
            case FORK:
                return new FACING_DIRECTION[]{
                 FACING_DIRECTION.NORTH, FACING_DIRECTION.SOUTH
                };
            case CUL_DE_SAC:
                break;
        }
        return new FACING_DIRECTION[0];
    }

    public static FACING_DIRECTION[] getExits(LevelGraphNode node, LevelGraph graph,
                                              Set<LevelGraphEdge> links,
                                              List<LevelGraphNode> linkedNodes) {
        //can we have duplicates in linkedNodes?
        FacingMaster.getRandomFacing();
        //rotate? mirror?
//        new ArrayMaster<FACING_DIRECTION>().getArray(FACING_DIRECTION.)
        return new FACING_DIRECTION[0];
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
        if (side == FACING_DIRECTION.SOUTH) {
            x -= i * parent.getWidth() / 2; //centered ...
            y -= i * parent.getHeight();
        } else if (side == FACING_DIRECTION.NORTH) {
            x -= i * parent.getWidth() / 2;
        } else if (side == FACING_DIRECTION.EAST) {
            x -= i * parent.getWidth();
            y -= i * parent.getHeight() / 2;
        } else if (side == FACING_DIRECTION.WEST) {
            y -= i * parent.getHeight() / 2;
        }
        return new Point(x, y);
    }

    public static Point getExitPoint(Room link, FACING_DIRECTION side) {
        return adjust(link.getPoint(), side, link, true);
//        +link.getWidth()
    }

    public static Point getAttachPoint(Room parent, Room model, FACING_DIRECTION side
    ) {
        int x = parent.getPoint().x + (parent.getWidth() //- model.getWidth() ???
        ) / 2;
        int y = parent.getPoint().y + (parent.getHeight()// - model.getHeight()
        ) / 2;
        return adjust(new Point(x, y), side, parent, true);

    }

    public void attach(Room to, Room attached, FACING_DIRECTION entrance) {
        Point p = getAttachPoint(to, attached, entrance);
        //check
        Point newPoint = model.addRoom(p, attached).setNewEntrance(entrance);
        model.getRoomMap().remove(p);
        model.getRoomMap().put(newPoint, attached);

    }

}
