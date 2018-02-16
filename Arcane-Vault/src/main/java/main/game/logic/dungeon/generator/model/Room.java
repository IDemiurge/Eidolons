package main.game.logic.dungeon.generator.model;

import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.generator.GeneratorEnums.ROOM_CELL;
import main.system.auxiliary.data.ArrayMaster;

import java.awt.*;

/**
 * Created by JustMe on 2/16/2018.
 */
public class Room extends RoomModel {
    private   FACING_DIRECTION[] exits;
    private   FACING_DIRECTION  entrance;
    private Point point; //upper-left?

    public Room(FACING_DIRECTION  entrance, RoomModel model,FACING_DIRECTION... exits ) {
        this(model);
        this.entrance = entrance;
        this.exits = exits;
    }
    public Room(RoomModel model) {
        super(model.cells, model.type, model.exitTemplate);
        //TODO won't work for non-square!!!
        if (getRotated()!=null )
        for (Boolean sub : getRotated()) {
            ArrayMaster.rotateMatrix_(model.getCells(), sub);
            main.system.auxiliary.log.LogMaster.log(1,model+" rotated: "
             +model.getCellsString() );
        }
    }
    public void makeExit(FACING_DIRECTION exit){
        Point p = RoomAttacher.adjust(new Point(0, 0),(exit), this, true);
        if (p.y==getHeight())
            p.y--;
        if (p.x==getWidth())
            p.x--;
        cells[p.x][p.y] = ROOM_CELL.FLOOR.getSymbol();
    }
    public FACING_DIRECTION[] getExits() {
        if (exits == null) {
            FACING_DIRECTION[] exits=RoomAttacher. getExits( getExitTemplate(), rotated);
            setExits(exits);
        }
        return exits;
    }

    public void setExits(FACING_DIRECTION[] exits) {
        this.exits = exits;
    }

    public FACING_DIRECTION getEntrance() {
        return entrance;
    }

    public void setEntrance(FACING_DIRECTION entrance) {
        this.entrance = entrance;
        makeExit(FacingMaster.rotate180(entrance));
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

}
