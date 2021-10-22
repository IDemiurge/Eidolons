package eidolons.game.exploration.dungeons.struct;

import eidolons.game.exploration.dungeons.generator.tilemap.TileMap;
import eidolons.game.exploration.dungeons.generator.tilemap.TileMapper;

public class BlockTemplate  {

    private final TileMap tileMap;

    public BlockTemplate(String stringData) {
        tileMap = TileMapper.createTileMap(stringData);
    }

    public TileMap getTileMap() {
        return tileMap;
    }
}
