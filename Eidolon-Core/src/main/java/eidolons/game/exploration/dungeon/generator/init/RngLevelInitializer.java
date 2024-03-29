package eidolons.game.exploration.dungeon.generator.init;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.RestoredDungeonLevel;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.exploration.dungeon.struct.DungeonLevel;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import eidolons.game.exploration.dungeon.struct.LevelZone;
import eidolons.game.exploration.dungeon.generator.fill.RngOverlayManager;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMap;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMapper;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        initEntrances();
        if (level instanceof RestoredDungeonLevel) {
            assignBlockTileMaps(level);
            if (!level.getObjects().isEmpty())
                return; //no need to translate symbols to objects, already done
        }
        //DUNGEON LEVEL FIX
//        for (LevelZone levelZone : level.getSubParts()) {
//            for (LevelBlock block : levelZone.getSubParts()) {
//                initTileMapBlock(block);
//            }
//        }
        initVoid();
        new RngOverlayManager().initDirectionMap(level);
        //        level.addCustomValue(G_PROPS.BACKGROUND, bgImagePath);


    }

    private void initEntrances() {
        boolean upward; //towers!..
        LevelBlock entrance = dungeonLevel.getBlocks().stream().filter(block -> block.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM).collect(Collectors.toList()).get(0);
        dungeonLevel.setEntranceType(RngBfObjProvider.getWeightMap(GeneratorEnums.ROOM_CELL.ENTRANCE,
                entrance.getZone().getStyle()).getRandomByWeight());


        entrance = dungeonLevel.getBlocks().stream().filter(block -> block.getRoomType()
                == ROOM_TYPE.EXIT_ROOM).collect(Collectors.toList()).get(0);
        dungeonLevel.setExitType(RngBfObjProvider.getWeightMap(GeneratorEnums.ROOM_CELL.EXIT,
                entrance.getZone().getStyle()).getRandomByWeight());
    }

    @Deprecated
    private void assignBlockTileMaps(DungeonLevel level) {
        TileMap map = level.getTileMap();
        GeneratorEnums.ROOM_CELL[][] cells = TileMapper.getCells(map);
        for (Module module : level.getSubParts()) {
            for (LevelZone zone : module.getSubParts()) {
                for (LevelBlock block : zone.getSubParts()) {
                    int w = CoordinatesMaster.getWidth(block.getCoordinatesSet());
                    int h = CoordinatesMaster.getHeight(block.getCoordinatesSet());
                    Map<Coordinates, GeneratorEnums.ROOM_CELL> cellMap = new LinkedHashMap<>();
                    for (Coordinates coordinates : block.getCoordinatesSet()) {
                        try {
                            cellMap.put(coordinates, cells[coordinates.x][coordinates.y]);
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                    TileMap subMap = new TileMap(cellMap);

                    block.setTileMap(subMap);
                    block.setWidth(w);
                    block.setHeight(h);
                    block.setOrigin(CoordinatesMaster.
                            getUpperLeftCornerCoordinates(block.getCoordinatesSet()));
                }
            }
        }
    }

    private void initVoid() {
    }

    public void initTileMapBlock(LevelBlock block) {
        for (Coordinates coordinates : block.getTileMap().getMap().keySet()) {
            createEntity(coordinates, block.getTileMap().getMap().get(coordinates), block);
        }
//        if (dungeonLevel.isBoundObjectsSupported()) {
//            for (Coordinates c : block.getBoundCells().keySet()) {
//                Coordinates bound = block.getBoundCells().get(c);
//                Coordinates random = RandomWizard.random() ? c : bound;
//                ObjAtCoordinate type = block.getObjects().stream().filter(at -> at.getCoordinates().equals(random)).findFirst().orElse(null);
//                if (type == null)
//                    continue;
//                clear(block, block.getBoundCells().get(c));
//                addObj(block, type.getType(), block.getBoundCells().get(c));
//            }
//        }
        //        addLocks();
        //        setupAiGroups();
        //        saveLevel();
    }

    private void createEntity(Coordinates c, GeneratorEnums.ROOM_CELL value, LevelBlock block) {
        //just write into new xml?
        if (value == null)
            return;

        ObjType type = chooseType(c, value, block);
        if (type == null) {
            return;
        }
        if (EntityCheckMaster.isOverlaying(type)) {
            createEntity(c, GeneratorEnums.ROOM_CELL.WALL, block);
        }
        addObj(block, type, c);
        main.system.auxiliary.log.LogMaster.log(1, value + " at " + c +
                " translated to obj: " + type);
    }


    private void clear(LevelBlock block, Coordinates coordinates) {
        dungeonLevel.getObjects().removeIf(at -> at.getCoordinates().equals(coordinates));
        block.getObjects().removeIf(at -> at.getCoordinates().equals(coordinates));
    }

    private void addObj(LevelBlock block, ObjType type, Coordinates c) {
        ObjAtCoordinate objAt = new ObjAtCoordinate(type, c);
        block.getObjects().add(0, objAt);
        if (!EntityCheckMaster.isOverlaying(type)) {
            if (dungeonLevel.getObjects().stream().anyMatch(at -> at.getCoordinates().equals(c))) {
                if (EntityCheckMaster.isWall(type)) {
                    return;
                } else {
                    main.system.auxiliary.log.LogMaster.log(1, ">>> DUPLICATE OBJ ON " + c +
                            "; last = " + type);
                }
            }
        }
        dungeonLevel.getObjects().add(objAt);


    }

    private ObjType chooseType(Coordinates c, GeneratorEnums.ROOM_CELL value, LevelBlock block) {
        return RngTypeChooser.chooseType(c, value, block);
    }


}
