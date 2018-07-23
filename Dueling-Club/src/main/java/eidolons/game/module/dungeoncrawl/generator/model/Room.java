package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.PointX;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 2/16/2018.
 */
public class Room extends RoomModel {
    private   FACING_DIRECTION[] exits;
    private   FACING_DIRECTION  entrance;
    private Point point; //upper-left?
    private Point entrancePoint;

    public Room(FACING_DIRECTION  entrance, RoomModel model,FACING_DIRECTION... exits ) {
        this(model);
        this.entrance = entrance;
        this.exits = exits;
    }
    public Room(RoomModel model) {
        super(model.cells, model.type, model.exitTemplate);
        //TODO won't work for non-square!!!

    }
        public void makeExit(FACING_DIRECTION exit, boolean door){
          entrancePoint = RoomAttacher.adjust(new Point(0, 0),(exit), this, true);
        if (entrancePoint.y==getHeight())
            entrancePoint.y--;
        if (entrancePoint.x==getWidth())
            entrancePoint.x--;
        //TODO check if this is necessary
        cells[entrancePoint.x][entrancePoint.y] =door? ROOM_CELL.DOOR.getSymbol()
        : ROOM_CELL.FLOOR.getSymbol()
        ;
    }
    public FACING_DIRECTION[] getExits() {
        if (exits == null) {
            FACING_DIRECTION[] exits= ExitMaster. getExits( getExitTemplate(), rotated);
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
            case EAST:
                cropX=1;
                break;
            case WEST:
                offsetX=1;
                break;
            case SOUTH:
                cropY=1;
                break;
        }
        int w=getWidth()-offsetX-cropX;
        int h=getHeight()-offsetY-cropY;
        String[][]  newCells = new String[w][h];
        for (int x = 0; x < w-cropX-offsetX ; x++) {
            for (int y = 0; y < h-cropY-offsetY ; y++) {
                newCells[x][y] = cells[x + offsetX][y + offsetY];
            }
        }
        cells= newCells;
        point = new PointX(point.x - offsetX+cropX, point.y - offsetY+cropY);
         return point;
    }

    @Override
    public void setRotated(Boolean[] rotated) {
        if (rotated==null){
            //TODO reverse = this.rotated; rotate back!
        }
        super.setRotated(rotated);
        exits = Arrays.stream(exits).map(exit-> {
            FACING_DIRECTION newExit = exit;
            for (Boolean clockwise : rotated) {
                FacingMaster.rotate(exit, clockwise);
            }
            return newExit;
        }).collect(Collectors.toList()).toArray(new FACING_DIRECTION[rotated.length]);
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

    public Point getEntrancePoint() {
        return entrancePoint;
    }

    public void setEntrancePoint(Point entrancePoint) {
        this.entrancePoint = entrancePoint;
    }
}
