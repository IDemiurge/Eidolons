package eidolons.libgdx.bf.decor;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
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
                    break;
                case COLUMNS:
                    break;
                case RUINS:
                    break;
                case CONSTRUCT:
                    break;
                case GATEWAY:
                    break;
                case GRAVES:
                    break;
                case WINDOWS:
                    break;
                case MAGICAL:
                    break;
                case HANGING:
                    break;
                case INTERIOR:
                    break;
                case STATUES:
                    break;
                case LOCK:
                    break;
                case ENTRANCE:
                    break;
                case TRAP:
                    break;
                case DOOR:
                    break;
                case LIGHT_EMITTER:
                    break;
                case CONTAINER:
                    break;
                case TREASURE:
                    break;
                case DUNGEON:
                    break;
                case WATER:
                    break;
                case TREES:
                    break;
                case ROCKS:
                    break;
                case VEGETATION:
                    break;
                case REMAINS:
                    break;
                case CRYSTAL:
                    break;
            }
        });

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
