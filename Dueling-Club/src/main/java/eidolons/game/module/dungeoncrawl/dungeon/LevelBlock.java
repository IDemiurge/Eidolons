package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
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
    private TileMap tileMap;
    private LevelZone zone;

    public LevelBlock(LevelZone zone,ROOM_TYPE roomType, int width, int height, TileMap tileMap) {
        this.roomType = roomType;
        this.width = width;
        this.height = height;
        this.tileMap = tileMap;
        this.zone = zone;
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

    public DUNGEON_STYLE getStyle() {
        return zone.getStyle();
    }


    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }
}
