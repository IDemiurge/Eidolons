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
            LevelBlock block =  null   ;

            if (model.getMerged() != null) {
//TODO
            }
            block = new LevelBlock(room.getCoordinates(), room.getZone(),
                 room.getType(), room.getWidth(), room.getHeight(), TileMapper.createTileMap(room));
            model.getBlocks().put(room, block);
            room.getZone().getSubParts().add(block);

        }
        return;
    }
}
