package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.game.bf.Coordinates;

import java.util.Collections;
import java.util.Map;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMap {

    Map<Coordinates, ROOM_CELL> map ;
    private int width;
    private int height;

    public TileMap(Map<Coordinates, ROOM_CELL> map) {
        setMap( map);
    }

    public Map<Coordinates, ROOM_CELL> getMap() {
        return  Collections.unmodifiableMap(map);
    }

    private Map<Coordinates, ROOM_CELL> getMapModifiable() {
        return map;
    }

    public ROOM_CELL put(Coordinates key, ROOM_CELL value) {
        ROOM_CELL old = getMapModifiable().put(key, value);
        recalcDimensions();
        return old;
    }

    private void setMap(Map<Coordinates, ROOM_CELL> map) {
        this.map = map;
        recalcDimensions();
    }

    private void recalcDimensions() {
        width = CoordinatesMaster.getWidth(map.keySet());
        height = CoordinatesMaster.getHeight(map.keySet());
    }

    @Override
    public String toString() {
        return TileMapper.toASCII_String(TileMapper.getCells(this), false);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    //zones/blocks?
    //ready to transform into Level / DungeonPlan / Dungeon
}
