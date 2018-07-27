package eidolons.game.module.dungeoncrawl.generator.level;

import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;

/**
 * Created by JustMe on 7/25/2018.
 */
public class BlockCreator {

    public void createBlocks(LevelModel model) {
        for (Room room : model.getRoomMap().values()) {

            LevelBlock block = new LevelBlock(room.getZone(),
             room.getType(), room.getWidth(), room.getHeight(),  TileMapper.createTileMap(room));

            room.getZone().getSubParts().add(block);

        }
    }
}
