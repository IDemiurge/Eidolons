package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.fill.RngOverlayManager;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

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
    DungeonLevel dungeonLevel;
    public   void init(DungeonLevel level) {
        this.dungeonLevel = level;
        for (LevelZone levelZone : level.getSubParts()) {
            for (LevelBlock block : levelZone.getSubParts()) {
                initTileMapBlock( block );

            }
        }
        new RngOverlayManager().initDirectionMap(level);
//        level.addCustomValue(G_PROPS.BACKGROUND, bgImagePath);


    }

    public  void initTileMapBlock( LevelBlock block ) {
        for (Coordinates coordinates : block.getTileMap().getMap().keySet()) {

            createEntity(coordinates, block.getTileMap().getMap().get(coordinates), block);
        }

        addLightEmitters();
//        addLocks();
//        setupAiGroups();
//        saveLevel();
    }
    private  void createEntity(Coordinates c, ROOM_CELL value, LevelBlock block) {
        //just write into new xml?
        ObjType type = chooseType(c, value, block);
        ObjAtCoordinate objAt=  new ObjAtCoordinate(type, c);
        block.getObjects().add( 0, objAt);
        dungeonLevel.getObjects().add(new ObjAtCoordinate(type,
         c.getOffset(block.getCoordinates())));
    }

    private  ObjType chooseType(Coordinates c, ROOM_CELL value, LevelBlock block) {
        return RngTypeChooser.chooseType(c, value, block, dungeonLevel);
    }

    private  void addLightEmitters() {
    }


}
