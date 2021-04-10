package eidolons.game.module.dungeoncrawl.struct;

import eidolons.dungeons.generator.tilemap.TileMap;
import eidolons.dungeons.generator.tilemap.TileMapper;

public class BlockTemplate  {

    private final TileMap tileMap;

    public BlockTemplate(String stringData) {
        tileMap = TileMapper.createTileMap(stringData);
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
