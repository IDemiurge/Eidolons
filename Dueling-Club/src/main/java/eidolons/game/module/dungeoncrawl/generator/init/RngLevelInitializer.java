package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.fill.RngOverlayManager;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 7/20/2018.
 * <p>
 * blocks and zones?
 * scripts
 * <p>
 * more complex links
 * dependencies
 * <p>
 * string - placeholder? variable? definition? group alias?
 */
public class RngLevelInitializer {
    DungeonLevel dungeonLevel;

    public void init(DungeonLevel level) {
        this.dungeonLevel = level;
        offsetCoordinates();
        for (LevelZone levelZone : level.getSubParts()) {
            for (LevelBlock block : levelZone.getSubParts()) {
                initTileMapBlock(block);

            }
        }
        new RngOverlayManager().initDirectionMap(level);
        //        level.addCustomValue(G_PROPS.BACKGROUND, bgImagePath);


    }

    private void offsetCoordinates() {
        AbstractCoordinates offset = new AbstractCoordinates(
         -dungeonLevel.getModel().getLeftMost(),
         -dungeonLevel.getModel().getTopMost());
        for (LevelZone zone : dungeonLevel.getSubParts()) {
            for (LevelBlock block : zone.getSubParts()) {
                block.getCoordinates().offset(offset);
            }
        }
    }

    public void initTileMapBlock(LevelBlock block) {
        for (Coordinates coordinates : block.getTileMap().getMap().keySet()) {

            createEntity(coordinates, block.getTileMap().getMap().get(coordinates), block);
        }

        addLightEmitters();
        //        addLocks();
        //        setupAiGroups();
        //        saveLevel();
    }

    private void createEntity(Coordinates c, ROOM_CELL value, LevelBlock block) {
        //just write into new xml?
        if (value==null )
            return;
        ObjType type = chooseType(c, value, block);
        if (type == null)
            return;
        addObj(block, type, c);
        if (block.getBoundCells().get(c)!=null ){
            addObj(block, type, block.getBoundCells().get(c));
        }
    }

    private void addObj(LevelBlock block, ObjType type, Coordinates c) {
        c = c.getOffset(block.getCoordinates());
        ObjAtCoordinate objAt = new ObjAtCoordinate(type, c);
        block.getObjects().add(0, objAt);
        dungeonLevel.getObjects().add(objAt);

    }

    private ObjType chooseType(Coordinates c, ROOM_CELL value, LevelBlock block) {
        return RngTypeChooser.chooseType(c, value, block, dungeonLevel);
    }

    private void addLightEmitters() {
    }


}
