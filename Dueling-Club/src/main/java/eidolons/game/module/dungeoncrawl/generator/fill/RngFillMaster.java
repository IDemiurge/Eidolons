package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngFillMaster {
    public static final boolean BOUND_SUPPORTED = false;

    public static void fill(LevelModel model, LevelData data) {


        if (isFillVoid())
            if (data.isSurface()) {
                fillSurfaceVoid(model);
            }
        WeightMap<ROOM_CELL> weightMap = getMap(FILLER_TYPE.OVERLAYING_LIGHT_EMITTERS, data);
        new RngWallLightFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.LIGHT_EMITTERS, data);
        new RngLightFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.CONTAINER, data);
        new RngContainerFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.SPECIAL_CONTAINER, data);
        new RngSpecialContainerFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.DECOR, data);
        new RngDecorFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.DESTRUCTIBLE, data);
        new RngDestructibleFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.OVERLAYING_DECOR, data);
        new RngWallDecorFiller(weightMap).fill(model);

        if (!data.getBooleanValue(GeneratorEnums.LEVEL_VALUES.CLEAN_DISABLED))
        try {
            cleanUp(model);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        checkBoundFillers(model);
        model.rebuildCells();
        main.system.auxiliary.log.LogMaster.log(1, " " + TileMapper.createTileMap(model));

    }

    private static boolean isFillVoid() {
        return false;
    }

    private static void checkBoundFillers(LevelModel model) {
        for (LevelBlock block : model.getBlocks().values()) {
            Map<Coordinates, ROOM_CELL> map = block.getTileMap().getMap();
            List<Coordinates> candidates = block.getTileMap().getMap().keySet().stream().filter(
             c -> canBeBoundFiller(map.get(c))
            ).collect(Collectors.toList());

            for (Coordinates c : candidates) {
                for (Coordinates c1 : candidates) {
                    if (map.get(c) == map.get(c1))
                        if (c.dst_(c1) == 2) {
                            //check no wall between
                            Coordinates between = c.getAdjacentCoordinate(FacingMaster.getRelativeFacing(c, c1).getDirection());
                            if (TilesMaster.isPassable(map.get(between)))
                                bindCoordinates(block, c, c1);
                            //clear other adjacent?
                            //will it stack for 2+
                        }
                }

            }
        }
    }

    public static void bindCoordinates(LevelBlock block, Coordinates c1, Coordinates c2) {
        if (c1==null )
            return;
        if (c2==null )
            return;
//        if (!block.getBoundCells().containsKey(c1)
//         && !block.getBoundCells().containsKey(c2))
//            main.system.auxiliary.log.LogMaster.log(1, block + " has " + c1 + " BOUND TO " + c2);
        //overwrite though...
        block.getBoundCells().put(c2, c1);
        block.getBoundCells().put(c1, c2);
    }

    private static boolean canBeBoundFiller(ROOM_CELL cell) {
        if (cell == null) {
            return false;
        }
        switch (cell) {
            case ART_OBJ:
            case LIGHT_EMITTER:
            case DESTRUCTIBLE:
            case SPECIAL_CONTAINER:
            case SPECIAL_ART_OBJ:
                return true;

        }
        return false;
    }


    private static void cleanUp(LevelModel model) {

        for (LevelBlock block : model.getBlocks().values()) {
            float minFloorPercentage = model.getData().getMinFloorPercentage(block.getRoomType());

            Map<Coordinates, ROOM_CELL> map = block.getTileMap().getMap();
            List<Coordinates> filledCells = map.keySet().stream().
             filter(c -> block.getOriginalTileMap().
              getMap().get(c)==ROOM_CELL.FLOOR
              && isClearable(map.get(c)) && TilesMaster.getAdjacentCount(map, c, ROOM_CELL.DOOR) ==0).
             collect(Collectors.toList());

            float wallCells = 0;
            float freeCells = block.getSquare() - wallCells - filledCells.size();
            float ratio = freeCells / (block.getWidth() * block.getHeight());
            float ratioDif = minFloorPercentage - ratio;

            int toClear = Math.round(block.getSquare() * ratioDif);

            filledCells.removeIf(c -> !isClearable(map.get(c)));
            filledCells.removeIf(c -> block.getOriginalTileMap().
             getMap().get(c)!=ROOM_CELL.FLOOR);

            Collections.shuffle(filledCells);

            for (Coordinates c : filledCells) {
                if (toClear == 0)
                    break;
                if (RandomWizard.chance(getClearChanceForCell(map.get(c)))) {
                    clearFill(model, block, c);
                    toClear--;
                }
            }

        }
//        for (int i = 0; i < toClear && filledCells.size()>0; ) {
//            Coordinates c = filledCells.getVar(RandomWizard.getRandomIndex(filledCells));
    }

    private static boolean isClearable(ROOM_CELL cell) {
        return getClearChanceForCell(cell) != 0;
    }

    private static int getClearChanceForCell(
     ROOM_CELL c) {
        if (c == null) {
            return 0;
        }
        switch ((c)) {
            case CONTAINER:
                return 45;
            case LIGHT_EMITTER:
                return 35;
            case DESTRUCTIBLE:
                return 75;
            case SPECIAL_CONTAINER:
                return 65;
            case SPECIAL_ART_OBJ:
            case ART_OBJ:
                return 55;

        }
        return 0;


    }

    private static void clearFill(LevelModel model, LevelBlock block, Coordinates c) {
        model.placeCell(c, block, ROOM_CELL.FLOOR);
    }

    private static WeightMap<ROOM_CELL> getMap(FILLER_TYPE type, LevelData data) {
        WeightMap<ROOM_CELL> map = new WeightMap<>("", ROOM_CELL.class);
        switch (type) {
            case LIGHT_EMITTERS:
                map.put(ROOM_CELL.LIGHT_EMITTER, 1);
                break;
            case OVERLAYING_LIGHT_EMITTERS:
                map.put(ROOM_CELL.WALL_WITH_LIGHT_OVERLAY, 1);
                break;
            case DECOR:
                map.put(ROOM_CELL.ART_OBJ, 3);
                map.put(ROOM_CELL.SPECIAL_ART_OBJ, 1);
                break;
            case OVERLAYING_DECOR:
                map.put(ROOM_CELL.WALL_WITH_DECOR_OVERLAY, 1);
                break;
            case CONTAINER:
                map.put(ROOM_CELL.CONTAINER, 3);
                map.put(ROOM_CELL.SPECIAL_CONTAINER, 1);
                break;
            case SPECIAL_CONTAINER:
                map.put(ROOM_CELL.SPECIAL_CONTAINER, 5);
                break;
            case GUARDS:
            case TRAPS:
                break;
            case DESTRUCTIBLE:
                map.put(ROOM_CELL.DESTRUCTIBLE, 1);
                break;
        }
        return map;
    }

    private static void fillSurfaceVoid(LevelModel model) {
        //        String fill = model.getData().getValue(LEVEL_VALUES.VOID_CELL_TYPE);
        for (LevelZone zone : model.getZones()) {
            if (zone.getSubParts().isEmpty()) {
                continue;
            }
            List<Coordinates> list = new ArrayList<>();
            for (LevelBlock block : zone.getSubParts()) {
                list.addAll(block.getCoordinatesSet());
            }
            Coordinates c = CoordinatesMaster.getFarmostCoordinateInDirection(DIRECTION.UP_LEFT, list);
            int w = CoordinatesMaster.getWidth(list);
            int h = CoordinatesMaster.getHeight(list);
            Map<Coordinates, ROOM_CELL> map = new LinkedHashMap<>();
            for (int x = c.x; x < c.x + w; x++) {
                for (int y = c.y; y < c.y + h; y++) {
                    AbstractCoordinates c1 = new AbstractCoordinates(x, y);
                    if (!list.contains(c1))
                        map.put(c1, ROOM_CELL.FLOOR);
                }

            }
            w = CoordinatesMaster.getWidth(map.keySet());
            h = CoordinatesMaster.getHeight(map.keySet());
            LevelBlock outside = new LevelBlock(c, zone,
             ROOM_TYPE.OUTSIDE, w, h, new TileMap(map));
            zone.getSubParts().add(outside);
            model.getBlocks().put(new Room(), outside);

        }
    }

    public enum FILLER_TYPE {
        LIGHT_EMITTERS,
        OVERLAYING_LIGHT_EMITTERS,
        DECOR,
        OVERLAYING_DECOR,
        CONTAINER,
        GUARDS,
        TRAPS,
        DESTRUCTIBLE,
        ENTRANCE,
        EXIT, SPECIAL_CONTAINER,
    }

}
