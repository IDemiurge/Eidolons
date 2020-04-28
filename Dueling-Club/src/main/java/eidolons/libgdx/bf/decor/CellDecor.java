package eidolons.libgdx.bf.decor;

import eidolons.entity.obj.Structure;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Map;

public class CellDecor {
    /**
     *
     * based on the objects?
     *
     * using the overlay field?
     *
     * how to ensure no overlapping?
     *
     *
     *
     *
     */

    Map<Coordinates, Coordinates> layerMap;

    public void init(){

        GuiEventManager.bind(GuiEventType.INIT_CELL_DECOR , p-> {
            Structure object = (Structure) p.get();

//            object.getProperty(PROPS.)

            switch (object.getBfObjGroup()) {

                case WALL:
                case CRYSTAL:
                case REMAINS:
                case VEGETATION:
                case ROCKS:
                case TREES:
                case WATER:
                case DUNGEON:
                case TREASURE:
                case CONTAINER:
                case LIGHT_EMITTER:
                case DOOR:
                case TRAP:
                case ENTRANCE:
                case LOCK:
                case STATUES:
                case INTERIOR:
                case HANGING:
                case MAGICAL:
                case WINDOWS:
                case GRAVES:
                case GATEWAY:
                case CONSTRUCT:
                case RUINS:
                case COLUMNS:
                    break;
            }
        });

    }

    public enum CELL_PATTERN {
        CROSS,
        CROSS_DIAG,
        CENTERPIECE,
        CHESS,

        GRID,
        SPIRAL,
        CONCENTRIC,
        OUTER_BORDER,
//        DIAMOND,
    }
    public enum CELL_UNDERLAY{
        CRACKS,
        ROCKS,
        VINES,
        DARK,
        cobwebs,
        ruins,

    }
}
