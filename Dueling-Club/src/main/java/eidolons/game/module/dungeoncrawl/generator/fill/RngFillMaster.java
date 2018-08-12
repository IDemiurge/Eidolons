package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.level.BlockCreator;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngFillMaster {
    public static void fill(LevelModel model, LevelData data) {
        new BlockCreator().createBlocks(model);

        WeightMap<ROOM_CELL> weightMap = getMap(FILLER_TYPE.OVERLAYING_LIGHT_EMITTERS, data);
        new RngWallLightFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.LIGHT_EMITTERS, data);
        new RngLightFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.CONTAINER, data);
        new RngContainerFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.DECOR, data);
        new RngDecorFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.DESTRUCTIBLE, data);
        new RngDestructibleFiller(weightMap).fill(model);

        weightMap = getMap(FILLER_TYPE.OVERLAYING_DECOR, data);
        new RngWallDecorFiller(weightMap).fill(model);

        main.system.auxiliary.log.LogMaster.log(1, " " + model);
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
            case GUARDS:
                break;
            case TRAPS:
                break;
            case DESTRUCTIBLE:
                map.put(ROOM_CELL.DESTRUCTIBLE, 1);
                break;
        }
        return map;
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
    }

}
