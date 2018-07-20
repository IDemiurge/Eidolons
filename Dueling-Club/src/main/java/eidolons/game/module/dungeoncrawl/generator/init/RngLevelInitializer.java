package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.content.OBJ_TYPE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.Tile;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 7/20/2018.
 *
 * blocks and zones?
 * scripts
 *
 * more complex links
 * dependencies
 *
 * string - placeholder? variable? definition? group alias?
 */
public class RngLevelInitializer {

    public void initTileMap(TileMap tileMap) {
        DungeonLevel level = createLevel(tileMap);

    }

    private DungeonLevel createLevel(TileMap tileMap) {
        return null;
    }

    public void initTileMapBlock(TileMap tileMap, LevelBlock block, LevelZone zone) {


        for (int i = 0; i < tileMap.getTiles().length; i++) {
            for (int j = 0; j < tileMap.getTiles()[i].length; j++) {
                Tile tile = tileMap.getTiles()[i][j];
                for (Pair<String, OBJ_TYPE> pair : tile.getData()) {
                    createEntity(pair.getKey(), pair.getValue());

                }
            }
        }
        addLightEmitters();
//        addLocks();
//        setupAiGroups();
//        saveLevel();
    }

    private void addLightEmitters() {
    }

    private void createEntity(String key, OBJ_TYPE value) {
//just write into new xml?
    }
}
