package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.game.bf.Coordinates;

import java.util.Map;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMap {
    Tile[][] tiles;

    Map<Coordinates, ROOM_CELL> map;

    public TileMap(int width, int height) {
        tiles = new Tile[width][height];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setMap(Map<Coordinates, ROOM_CELL> map) {
        this.map = map;
    }

    public Map<Coordinates, ROOM_CELL> getMap() {
        return map;
    }
    //zones/blocks?
    //ready to transform into Level / DungeonPlan / Dungeon
}
