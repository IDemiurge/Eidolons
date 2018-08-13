package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.RestoredDungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.fill.RngOverlayManager;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.LinkedHashMap;
import java.util.Map;

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
        if (level instanceof RestoredDungeonLevel) {
            assignBlockTileMaps(level);
            return;
        }
        for (LevelZone levelZone : level.getSubParts()) {
            for (LevelBlock block : levelZone.getSubParts()) {
                initTileMapBlock(block);
            }
        }
        initVoid();
        new RngOverlayManager().initDirectionMap(level);
        //        level.addCustomValue(G_PROPS.BACKGROUND, bgImagePath);


    }

    private void assignBlockTileMaps(DungeonLevel level) {
        TileMap map = level.getTileMap();
        ROOM_CELL[][] cells = TileMapper.getCells(map);
        for (LevelZone zone : level.getSubParts()) {
            for (LevelBlock block : zone.getSubParts()) {
                int w = CoordinatesMaster.getWidth(block.getCoordinatesList());
                int h = CoordinatesMaster.getHeight(block.getCoordinatesList());
                TileMap subMap = new TileMap(w, h);
                Map<Coordinates, ROOM_CELL> cellMap = new LinkedHashMap<>();
                for (Coordinates coordinates : block.getCoordinatesList()) {
                    try {
                        cellMap.put(coordinates, cells[coordinates.x][coordinates.y]);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }

                subMap.setMap(cellMap);
                block.setTileMap(subMap);
                                block.setWidth(w);
                                block.setHeight(h);
                                block.setCoordinates(CoordinatesMaster.
                                 getUpperLeftCornerCoordinates(block.getCoordinatesList()));
            }
        }
    }

    private void initVoid() {
    }

    public void initTileMapBlock(LevelBlock block) {
        for (Coordinates coordinates : block.getTileMap().getMap().keySet()) {

            createEntity(coordinates, block.getTileMap().getMap().get(coordinates), block);
        }

        //        addLocks();
        //        setupAiGroups();
        //        saveLevel();
    }

    private void createEntity(Coordinates c, ROOM_CELL value, LevelBlock block) {
        //just write into new xml?
        if (value == null)
            return;
        ObjType type = chooseType(c, value, block);
        if (type == null)
            return;
        addObj(block, type, c);
        if (block.getBoundCells().get(c) != null) {
            addObj(block, type, block.getBoundCells().get(c));
        }
    }

    private void addObj(LevelBlock block, ObjType type, Coordinates c) {
        ObjAtCoordinate objAt = new ObjAtCoordinate(type, c);
        block.getObjects().add(0, objAt);
        dungeonLevel.getObjects().add(objAt);

    }

    private ObjType chooseType(Coordinates c, ROOM_CELL value, LevelBlock block) {
        return RngTypeChooser.chooseType(c, value, block, dungeonLevel);
    }


}
