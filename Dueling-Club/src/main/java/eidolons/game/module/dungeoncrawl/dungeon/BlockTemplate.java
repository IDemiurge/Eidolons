package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.module.generator.tilemap.TileMap;
import eidolons.game.module.generator.tilemap.TileMapper;

public class BlockTemplate  {

    private final TileMap tileMap;

    public BlockTemplate(String stringData) {
        tileMap = TileMapper.createTileMap(stringData);
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
