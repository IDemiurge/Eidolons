package eidolons.game.exploration.dungeons.generator.model;

import eidolons.game.exploration.dungeons.generator.GeneratorEnums;
import eidolons.game.exploration.dungeons.generator.tilemap.TileMapper;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.system.auxiliary.data.ArrayMaster;

import java.io.Serializable;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomModel implements Serializable {
    protected GeneratorEnums.EXIT_TEMPLATE exitTemplate;
    protected final ROOM_TYPE type;
    protected   String[][] cells;
    protected String cellsString;
    protected Boolean[] rotations;
    protected boolean flipX;
    protected boolean flipY;

    public RoomModel(String[][] cells, ROOM_TYPE type, GeneratorEnums.EXIT_TEMPLATE exitTemplate) {
        this.cells = cells;
        this.type = type;
        this.exitTemplate = exitTemplate;
    }

    public boolean isDisplaced() {
        return false;
    }

    public Boolean[] getRotations() {
        return rotations;
    }
    //allow other rooms to carve into this room's wall space

    public void setRotations(Boolean... rotations) {
        this.rotations = rotations;
        if (getRotations()!=null )
            for (Boolean sub : getRotations()) {
//                main.system.auxiliary.log.LogMaster.log(1, sub+ "wise rotating: "
//                 +getCellsString() );
                cells =
                 new ArrayMaster<String>().rotate2dStringArray ( getCells(), sub);
//                main.system.auxiliary.log.LogMaster.log(1, sub+"wise rotated: "
//                 +getCellsString() );
            }
    }

    public GeneratorEnums.EXIT_TEMPLATE getExitTemplate() {
        return exitTemplate;
    }

    public ROOM_TYPE getType() {
        return type;
    }

    public String[][] getCells() {
        return cells;
    }

    public void setCells(String[][] cells) {
        this.cells = cells;
    }

    public int getHeight() {
        return cells[0].length;
    }

    public int getWidth() {
        return cells.length;
    }

    public String getCellsString() {
//        cellsString = "";
//        for (String[] sub : cells) {
//            for (String sub1 : sub) {
//                cellsString += sub1;
//            }
//            cellsString += "\n";
//        }
        return TileMapper.createTileMap(cells).toString();
    }

    @Override
    public String toString() {
        return type + " Model with exit "
         + exitTemplate + ": \n" + getCellsString();
    }

    public void setFlip(boolean x, boolean y) {
        if (flipX==x && flipY==y)
            return;
        flipX=x;
        flipY=y;
        cells = ArrayMaster.flip(cells, x, y);

    }
}
