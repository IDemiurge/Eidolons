package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.RestoredDungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.fill.RngOverlayManager;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
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

    private void initEntrances() {
        boolean upward; //towers!..
        LevelBlock entrance = dungeonLevel.getBlocks().stream().filter(block -> block.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM).collect(Collectors.toList()).get(0);
        dungeonLevel.setEntranceType(RngBfObjProvider.getWeightMap(ROOM_CELL.ENTRANCE,
         entrance.getZone().getStyle()).getRandomByWeight());


        entrance = dungeonLevel.getBlocks().stream().filter(block -> block.getRoomType()
         == ROOM_TYPE.EXIT_ROOM).collect(Collectors.toList()).get(0);
        dungeonLevel.setExitType(RngBfObjProvider.getWeightMap(ROOM_CELL.EXIT,
         entrance.getZone().getStyle()).getRandomByWeight());
    }

    private void assignBlockTileMaps(DungeonLevel level) {
        TileMap map = level.getTileMap();
        ROOM_CELL[][] cells = TileMapper.getCells(map);
        for (LevelZone zone : level.getSubParts()) {
            for (LevelBlock block : zone.getSubParts()) {
                int w = CoordinatesMaster.getWidth(block.getCoordinatesList());
                int h = CoordinatesMaster.getHeight(block.getCoordinatesList());
                Map<Coordinates, ROOM_CELL> cellMap = new LinkedHashMap<>();
                for (Coordinates coordinates : block.getCoordinatesList()) {
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
        if (!isCellTranslated(value))
            return;
        ObjType type = chooseType(c, value, block);
        if (type == null) {
            main.system.auxiliary.log.LogMaster.log(1, value + " at " + c +
             " can't be translated to obj!");
            return;
        }
        if (EntityCheckMaster.isOverlaying(type)) {
            createEntity(c, ROOM_CELL.WALL, block);
        }
        addObj(block, type, c);
        if (dungeonLevel.isBoundObjectsSupported())
        if (block.getBoundCells().get(c) != null) {
            clear(block, block.getBoundCells().get(c));
            addObj(block, type, block.getBoundCells().get(c));
        }
        main.system.auxiliary.log.LogMaster.log(1, value + " at " + c +
         " translated to obj: " + type);
    }


    private boolean isCellTranslated(ROOM_CELL value) {
        switch (value) {
            case VOID:
            case TRAP:
            case GUARDS:
            case PATROL:
            case AMBUSH:
            case CROWD:
            case IDLERS:
            case STALKER:
            case MINI_BOSS:
            case BOSS:
            case LOCAL_KEY:
            case GLOBAL_KEY:
            case RANDOM_PASSAGE:
            case RANDOM_SPAWN_GROUP:
            case RANDOM_OBJECT:
            case FLOOR:
            case ROOM_EXIT:
            case TREASURE_ROOM:
            case THRONE_ROOM:
            case DEATH_ROOM:
            case GUARD_ROOM:
            case COMMON_ROOM:
            case EXIT_ROOM:
            case SECRET_ROOM:
            case ENTRANCE_ROOM:
            case CORRIDOR:
                return false;

        }
        return true;
    }

    private void clear(LevelBlock block, Coordinates coordinates) {
        dungeonLevel.getObjects().removeIf(at -> at.getCoordinates().equals(coordinates));
        block.getObjects().removeIf(at -> at.getCoordinates().equals(coordinates));
    }
    private void addObj(LevelBlock block, ObjType type, Coordinates c) {
        ObjAtCoordinate objAt = new ObjAtCoordinate(type, c);
        block.getObjects().add(0, objAt);
     if (!EntityCheckMaster.isOverlaying(type))
         if (dungeonLevel.getObjects().stream().anyMatch(at -> at.getCoordinates().equals(c))) {
            if (EntityCheckMaster.isWall(type)) {
                return;
            }
            else {
                main.system.auxiliary.log.LogMaster.log(1,">>> DUPLICATE OBJ ON " +c+
                "; last = " + type);
            }
        }
        dungeonLevel.getObjects().add(objAt);


    }

    private ObjType chooseType(Coordinates c, ROOM_CELL value, LevelBlock block) {
        return RngTypeChooser.chooseType(c, value, block, dungeonLevel);
    }


}
