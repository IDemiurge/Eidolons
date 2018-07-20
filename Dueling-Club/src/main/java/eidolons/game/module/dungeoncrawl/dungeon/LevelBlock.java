package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelBlock  extends LevelLayer<LevelBlock>{
    private ROOM_TYPE roomType;
    private int width;
    private int height;
    private List<ObjAtCoordinate> units=    new ArrayList<>() ;
    private List<ObjAtCoordinate> objects=    new ArrayList<>() ;
    private List<Coordinates> coordinates;

    public LevelBlock(ROOM_TYPE roomType, int width, int height) {
        this.roomType = roomType;
        this.width = width;
        this.height = height;
    }

    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    @Override
    public String toXml() {
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<ObjAtCoordinate> getUnits() {
        return units;
    }

    public List<ObjAtCoordinate> getObjects() {
        return objects;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }
}
