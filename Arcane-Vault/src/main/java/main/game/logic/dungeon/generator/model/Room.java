package main.game.logic.dungeon.generator.model;

import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.logic.dungeon.generator.GeneratorEnums.ROOM_CELL;
import main.swing.PointX;
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
        cells[p.x][p.y] = ROOM_CELL.DOOR.getSymbol();
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

    public Point setNewEntrance(FACING_DIRECTION entrance) {
        this.entrance = entrance;
//        makeExit(FacingMaster.rotate180(entrance));

         return shearWallsFromSide(entrance);
    }

    private Point shearWallsFromSide(FACING_DIRECTION entrance) {
        int offsetX=0;
        int offsetY=0;
        int cropX=0;
        int cropY=0;
        switch (entrance) {
            case NORTH:
                offsetY=1;
                break;
            case WEST:
                offsetX=1;
                break;
            case EAST:
                cropX=1;
                break;
            case SOUTH:
                cropY=1;
                break;
        }
        int w=getWidth()-offsetX;
        int h=getHeight()-offsetY;
        String[][]  newCells = new String[w][h];
        for (int x = 0; x < w-cropX ; x++) {
            for (int y = 0; y < h-cropY ; y++) {
                newCells[x][y] = cells[x + offsetX][y + offsetY];
            }
        }
        cells= newCells;
        point = new PointX(point.x - offsetX, point.y - offsetY);
         return point;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setExitTemplate(EXIT_TEMPLATE exitTemplate) {
        this.exitTemplate = exitTemplate;
    }
}
