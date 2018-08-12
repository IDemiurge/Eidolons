package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ArrayMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster.DEFAULT_ENTRANCE_SIDE;

/**
 * Created by JustMe on 2/16/2018.
 */
public class Room extends RoomModel {
    private FACING_DIRECTION[] exits;
    private FACING_DIRECTION entrance;
    private Coordinates point; //upper-left!!!
    private Coordinates offset = new AbstractCoordinates(0, 0);
    private Coordinates entranceCoordinates;
    private LevelZone zone;
    private List<Coordinates> exitCoordinates = new ArrayList<>();
    private List<String> exitCells = new ArrayList<>();
    private List<FACING_DIRECTION> usedExits = new ArrayList<>();
    private String entranceCell;
    private boolean sheared;

    public Room(Coordinates roomCoordinates, RoomModel model, FACING_DIRECTION roomEntrance) {
        super(model.cells, model.type, model.exitTemplate);
        setCoordinates(roomCoordinates);
        this.rotations = model.getRotations();
        rotateExits(model.getRotations());
        this.entrance = roomEntrance;
        for (int i = 0; i < exits.length; i++) {
            if (exits[i] == entrance) {
                exits[i] = RotationMaster.rotate(DEFAULT_ENTRANCE_SIDE, rotations);
                break;
            }
        }
        if (entrance != null)
            makeExit(entrance, false, false);
    }

    @Override
    public void setFlip(boolean x, boolean y) {
        super.setFlip(x, y);
        //TODO exit/entrance
    }

    public void makeExit(FACING_DIRECTION exit, boolean door, boolean exitOrEntrance) {
        Coordinates coordinates = RoomAttacher.adjust(
         isExitsOffset() ? new AbstractCoordinates(
          exit.isVertical() ? -offset.x : 0, !exit.isVertical() ? -offset.y : 0)
          : new AbstractCoordinates(0, 0), (exit), this, true, isCanAdjustEvenExit());
        if (coordinates.y == getHeight())
            coordinates.y--;
        if (coordinates.x == getWidth())
            coordinates.x--;

        if (exitOrEntrance) {
            if (!exitCoordinates.contains(coordinates)) {
                this.exitCoordinates.add(coordinates);
                this.usedExits.add(exit);
            }
        } else {
            entranceCoordinates = coordinates;
            if (new ArrayMaster<FACING_DIRECTION>().contains(exits, exit)) {
                exit = RotationMaster.rotate(DEFAULT_ENTRANCE_SIDE, rotations);
            }
            entrance = exit;
        }
        String s = door ? ROOM_CELL.DOOR.getSymbol()
         : ROOM_CELL.ROOM_EXIT.getSymbol();
        if (isExitsLogical()) {
            if (exitOrEntrance) {
                exitCells.add(s);
            } else {
                entranceCell = s;
            }
        } else
            cells[coordinates.x][coordinates.y] = s;
    }

    private boolean isExitsOffset() {
        return true;
    }

    private boolean isCanAdjustEvenExit() {
        return false;
    }

    @Override
    public boolean isDisplaced() {
        return offset.x != 0 || offset.y != 0;
    }

    private boolean isExitsLogical() {
        return false;
    }

    @Override
    public String[][] getCells() {
        if (!isExitsLogical())
            return super.getCells();
        String[][] modified = ArrayMaster.cloneMatrix(cells);
        int i = 0;
        if (isOffsetAlways())
        //         || getType() == ROOM_TYPE.CORRIDOR&&getExitTemplate() == EXIT_TEMPLATE.ANGLE)
        {
            if (entranceCoordinates != null)
                entranceCoordinates = entranceCoordinates.getOffset(offset);
            exitCoordinates.forEach(coordinates ->
             coordinates.offset(offset));
        }

        for (Coordinates exitCoordinate : exitCoordinates) {
            modified[exitCoordinate.x][exitCoordinate.y] =
             exitCells.get(i);
        }
        if (entranceCoordinates != null)
            modified[entranceCoordinates.x][entranceCoordinates.y] = entranceCell;
        return modified;
    }

    public FACING_DIRECTION[] getExits() {
        if (exits == null) {
            FACING_DIRECTION[] exits = ExitMaster.getExits(getExitTemplate());
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

    public Coordinates shearWallsFromSide(FACING_DIRECTION entrance, boolean offsetOnly) {
        int offsetX = 0;
        int offsetY = 0;
        int cropX = 0;
        int cropY = 0;
        switch (entrance) {
            case NORTH:
                offsetY = 1;
                break;
            case EAST:
                cropX = 1;
                break;
            case WEST:
                offsetX = 1;
                break;
            case SOUTH:
                cropY = 1;
                break;
        }
        if (!offsetOnly) {
            sheared = true;
            int w = getWidth() - offsetX - cropX;
            int h = getHeight() - offsetY - cropY;
            String[][] newCells = new String[w][h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    newCells[x][y] = cells[x + offsetX][y + offsetY];
                }
            }
            cells = newCells;
        }
        //only ever increase Y and decrease X
        Coordinates offset = offsetOnly ? new AbstractCoordinates(
         -offsetX + cropX,
         -offsetY + cropY)
         : new AbstractCoordinates(cropX, cropY);

        point.offset(offset);
        this.offset.offset(offset);
        if (offsetOnly) {
            point = new AbstractCoordinates(
             point.x - offsetX + cropX,
             point.y - offsetY + cropY);
        } else {
            if (cropX > 0 || cropY > 0) {
                point = point.getOffset(offset);
                //           TODO is it right?
            }
        }
        return point;
    }

    private boolean isOffsetAlways() {
        return false;
    }

    @Override
    public void setRotations(Boolean[] rotations) {
        if (rotations == null) {
            //TODO reverse = this.rotated; rotate back!
        }
        super.setRotations(rotations);
        rotateExits(rotations);
    }

    private void rotateExits(Boolean[] rotated) {
        exits = RotationMaster.getRotatedExits(rotated, getExits());
        if (entrance != null)
            entrance = RotationMaster.rotate(entrance, rotated);
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

    public List<Coordinates> getExitCoordinates() {
        return exitCoordinates;
    }

    public List<FACING_DIRECTION> getUsedExits() {
        return usedExits;
    }

    public FACING_DIRECTION getRandomUnusedExit() {
        List<FACING_DIRECTION> list = Arrays.stream(getExits()).filter(e -> !getUsedExits().contains(e)).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return new RandomWizard<FACING_DIRECTION>().getRandomListItem(list);
    }

    public boolean isSheared() {
        return sheared;
    }

}
