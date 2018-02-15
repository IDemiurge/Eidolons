package main.game.battlecraft.logic.dungeon.generator.model;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.bf.Coordinates.FACING_DIRECTION;

import java.awt.*;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomModel {
    private final EXIT_TEMPLATE exitTemplate;
    private   FACING_DIRECTION[] exits;
    private final ROOM_TYPE type;
    private final String[][] cells;
    private Point point; //upper-left?
    Boolean[] rotated;
    private String cellsString;
    //allow other rooms to carve into this room's wall space

    public RoomModel(String[][] cells, ROOM_TYPE type, EXIT_TEMPLATE exitTemplate) {
        this.cells = cells;
        this.type = type;
        this.exitTemplate = exitTemplate;
    }

    public FACING_DIRECTION[] getExits() {
        if (exits == null) {
        FACING_DIRECTION[] exits=RoomAttacher. getExits( getExitTemplate(), rotated);
         setExits(exits);
        }
        return exits;
    }

    public void setRotated(Boolean[] rotated) {
        this.rotated = rotated;
    }

    public void setExits(FACING_DIRECTION[] exits) {
        this.exits = exits;
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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getHeight() {
        return cells.length;
    }
    public int getWidth() {
        return cells[0].length;
    }

    public String getCellsString() {
        cellsString="";
        for (String[] sub : cells) {
            for (String sub1 : sub) {
                cellsString+=sub1;
            }
            cellsString+="\n";
        }
        return cellsString;
    }

    @Override
    public String toString() {
        return type + " Model with exit "
         + exitTemplate + ": " + getCellsString();
    }
}
