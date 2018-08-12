package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;

import java.util.Map;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMap {
    Tile[][] tiles;

    Map<Coordinates, ROOM_CELL> map = new XLinkedMap<>();
    private int width;
    private int height;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Map<Coordinates, ROOM_CELL> getMap() {
        return map;
    }

    public void setMap(Map<Coordinates, ROOM_CELL> map) {
        this.map = map;
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
