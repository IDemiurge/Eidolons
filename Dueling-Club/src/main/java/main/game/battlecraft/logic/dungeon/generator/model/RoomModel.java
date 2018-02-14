package main.game.battlecraft.logic.dungeon.generator.model;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TEMPLATE;
import main.game.bf.Coordinates.FACING_DIRECTION;

import java.awt.*;

/**
 * Created by JustMe on 2/13/2018.
 */
public class RoomModel {
    FACING_DIRECTION[] exits;
    ROOM_TEMPLATE template;
    char[][] cells;
    private Point point; //upper-left?
    //allow other rooms to carve into this room's wall space

    public RoomModel(char[][] cells, ROOM_TEMPLATE template, FACING_DIRECTION... exits) {
        this.cells = cells;
        this.template = template;
        this.exits = exits;
    }

    public FACING_DIRECTION[] getExits() {
        return exits;
    }

    public ROOM_TEMPLATE getTemplate() {
        return template;
    }

    public char[][] getCells() {
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
}
