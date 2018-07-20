package eidolons.game.module.dungeoncrawl.generator.tilemap;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMap {
    Tile[][] tiles;

    public TileMap(int width, int height) {
        tiles = new Tile[width][height];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    //zones/blocks?
    //ready to transform into Level / DungeonPlan / Dungeon
}
