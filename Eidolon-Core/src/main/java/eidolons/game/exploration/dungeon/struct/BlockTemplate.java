package eidolons.game.exploration.dungeon.struct;

import eidolons.game.exploration.dungeon.generator.tilemap.TileMap;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMapper;

public class BlockTemplate  {

    private final TileMap tileMap;

    public BlockTemplate(String stringData) {
        tileMap = TileMapper.createTileMap(stringData);
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
