package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphEdge;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;

import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 7/21/2018.
 */
public class ExitMaster {
    public static EXIT_TEMPLATE getExitTemplateToLinks(
     int links, FACING_DIRECTION entrance, ROOM_TYPE type) {
        if (type == ROOM_TYPE.EXIT_ROOM) {
            switch (entrance) {
                case NORTH:
                case SOUTH:
                    return RandomWizard.random() ? EXIT_TEMPLATE.FORK : EXIT_TEMPLATE.CROSSROAD;
                case WEST:
                    return RandomWizard.random() ? EXIT_TEMPLATE.ANGLE : EXIT_TEMPLATE.THROUGH;
                case EAST:
                    return EXIT_TEMPLATE.CROSSROAD;
            }
        }

        if (links == 0)
            return EXIT_TEMPLATE.CUL_DE_SAC;
        else if (links >= 2)
            return EXIT_TEMPLATE.FORK;
        else if (links >= 3)
            return RandomWizard.random() ?EXIT_TEMPLATE.CROSSROAD:  EXIT_TEMPLATE.FORK;
        else if (links >= 4)
            return EXIT_TEMPLATE.CROSSROAD;

        return
             RandomWizard.random() ? EXIT_TEMPLATE.ANGLE : EXIT_TEMPLATE.THROUGH;
    }

    public static FACING_DIRECTION getExit(EXIT_TEMPLATE roomExitTemplate,
                                           FACING_DIRECTION entrance) {
        //mirrored
        if (roomExitTemplate == EXIT_TEMPLATE.THROUGH)
            return entrance;
        DIRECTION dir = entrance.getDirection();
        return FacingMaster.getFacingFromDirection(DirectionMaster.rotate90(dir, true));
    }

    public static EXIT_TEMPLATE getRandomSingleExitTemplate() {
        return
         RandomWizard.random() ? RandomWizard.random() ? EXIT_TEMPLATE.THROUGH : EXIT_TEMPLATE.ANGLE :
          EXIT_TEMPLATE.THROUGH;
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
                return new FACING_DIRECTION[0];
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

    public static Coordinates getExitCoordinates(Room link, FACING_DIRECTION side) {
        return RoomAttacher.adjust(link.getCoordinates(), side, link, true);
        //        +link.getWidth()
    }

    public static Coordinates findExit(Room room, FACING_DIRECTION direction) {
        int i = 0;
        for (FACING_DIRECTION exit : room.getUsedExits()) {
            if (exit == direction)
                if (room.getExitCoordinates().size() > i)
                    return room.getExitCoordinates().get(i);
            i++;
        }
        return null;


    }

    public static int getExitCount(EXIT_TEMPLATE exitTemplate) {
        switch (exitTemplate) {
            case THROUGH:
            case ANGLE:
                return 1;
            case FORK:
                return 2;
            case CROSSROAD:
                return 3;
            case CUL_DE_SAC:
                return 0;
        }
        return 0;
    }
}
