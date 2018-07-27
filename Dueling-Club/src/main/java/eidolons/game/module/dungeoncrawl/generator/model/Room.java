package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 2/16/2018.
 */
public class Room extends RoomModel {
    private   FACING_DIRECTION[] exits;
    private   FACING_DIRECTION  entrance;
    private Coordinates point; //upper-left?
    private Coordinates entranceCoordinates;
    private LevelZone zone;
    private List<Coordinates> exitCoordinatess;

    public Room(Coordinates roomCoordinates, RoomModel model) {
        super(model.cells, model.type, model.exitTemplate);
        setCoordinates(roomCoordinates);
//        rotateExits(model.getRotated());
        //TODO won't work for non-square!!!

    }
        public void makeExit(FACING_DIRECTION exit, boolean door){
          entranceCoordinates = RoomAttacher.adjust(new AbstractCoordinates(0, 0),(exit), this, true);
        if (entranceCoordinates.y==getHeight())
            entranceCoordinates.y--;
        if (entranceCoordinates.x==getWidth())
            entranceCoordinates.x--;
        //TODO check if this is necessary
        cells[entranceCoordinates.x][entranceCoordinates.y] =door? ROOM_CELL.DOOR.getSymbol()
        : ROOM_CELL.EXIT.getSymbol();

        if (exitCoordinatess==null )
            exitCoordinatess = new ArrayList<>();
        if (!exitCoordinatess.contains(new AbstractCoordinates(entranceCoordinates.x, entranceCoordinates.y)))
         this.exitCoordinatess.add(new AbstractCoordinates(entranceCoordinates.x, entranceCoordinates.y));
    }
    public FACING_DIRECTION[] getExits() {
        if (exits == null) {
            FACING_DIRECTION[] exits= ExitMaster. getExits( getExitTemplate(), rotations);
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

    public Coordinates setNewEntrance(FACING_DIRECTION entrance) {
        this.entrance = entrance;
//        makeExit(FacingMaster.rotate180(entrance));

         return shearWallsFromSide(entrance);
    }


        public Coordinates shearWallsFromSide(FACING_DIRECTION entrance) {
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
        for (int x = 0; x < w ; x++) {
            for (int y = 0; y < h ; y++) {
                newCells[x][y] = cells[x + offsetX][y + offsetY];
            }
        }
        cells= newCells;
        point = new AbstractCoordinates(point.x - offsetX+cropX, point.y - offsetY+cropY);
         return point;
    }

    @Override
    public void setRotations(Boolean[] rotations) {
        if (rotations ==null){
            //TODO reverse = this.rotated; rotate back!
        }
        super.setRotations(rotations);
        rotateExits(rotations);
        }

    private void rotateExits(Boolean[] rotated) {
        exits = Arrays.stream(exits).map(exit-> {
            FACING_DIRECTION newExit = exit;
            for (Boolean clockwise : rotated) {
                FacingMaster.rotate(exit, clockwise);
            }
            return newExit;
        }).collect(Collectors.toList()).toArray(new FACING_DIRECTION[rotated.length]);
    }

    public Coordinates getCoordinates() {
        return point;
    }

    public void setCoordinates(Coordinates point) {
        this.point = point;
    }

    public void setExitTemplate(EXIT_TEMPLATE exitTemplate) {
        this.exitTemplate = exitTemplate;
    }

    public Coordinates getEntranceCoordinates() {
        return entranceCoordinates;
    }

    public void setEntranceCoordinates(Coordinates entranceCoordinates) {
        this.entranceCoordinates = entranceCoordinates;
    }

    public LevelZone getZone() {
        return zone;
    }

    public void setZone(LevelZone zone) {
        this.zone = zone;
    }

    public List<Coordinates> getExitCoordinatess() {
        return exitCoordinatess;
    }

    public void setExitCoordinatess(List<Coordinates> exitCoordinatess) {
        this.exitCoordinatess = exitCoordinatess;
    }
}
