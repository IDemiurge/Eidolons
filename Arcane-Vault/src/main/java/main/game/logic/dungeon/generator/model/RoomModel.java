package main.game.logic.dungeon.generator.model;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.game.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.system.auxiliary.data.ArrayMaster;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomModel {
    protected   EXIT_TEMPLATE exitTemplate;
    protected final ROOM_TYPE type;
    protected   String[][] cells;
    protected String cellsString;
    protected Boolean[] rotated;

    public RoomModel(String[][] cells, ROOM_TYPE type, EXIT_TEMPLATE exitTemplate) {
        this.cells = cells;
        this.type = type;
        this.exitTemplate = exitTemplate;
    }

    public Boolean[] getRotated() {
        return rotated;
    }
    //allow other rooms to carve into this room's wall space

    public void setRotated(Boolean[] rotated) {
        this.rotated = rotated;
        if (getRotated()!=null )
            for (Boolean sub : getRotated()) {
                main.system.auxiliary.log.LogMaster.log(1, sub+ "wise rotating: "
                 +getCellsString() );
                cells = (String[][])
                 new ArrayMaster<String>().rotateMatrix ( getCells(), sub);

                main.system.auxiliary.log.LogMaster.log(1, sub+"wise rotated: "
                 +getCellsString() );
            }
    }

    public EXIT_TEMPLATE getExitTemplate() {
        return exitTemplate;
    }

    public ROOM_TYPE getType() {
        return type;
    }

    public String[][] getCells() {
        return cells;
    }


    public int getHeight() {
        return cells[0].length;
    }

    public int getWidth() {
        return cells.length;
    }

    public String getCellsString() {
        cellsString = "";
        for (String[] sub : cells) {
            for (String sub1 : sub) {
                cellsString += sub1;
            }
            cellsString += "\n";
        }
        return cellsString;
    }

    @Override
    public String toString() {
        return type + " Model with exit "
         + exitTemplate + ": " + getCellsString();
    }
}
