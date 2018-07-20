package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DECOR_STYLE;
import main.content.enums.entity.BfObjEnums.BF_OBJ_TYPES;
import main.content.enums.entity.BfObjEnums.BF_OBJ_TYPES_STRUCTURES;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/20/2018.
 */
public class RngConstProvider {
    public static String getWeightMap(ROOM_CELL cell, DECOR_STYLE style) {
//objGroups like unitGroups?
        switch (cell) {
            case WALL:
                return getWallWeightMap(style);
            case ENTRANCE:
                break;
            case EXIT:
                break;
            case CONTAINER:
                break;
            case DOOR:
                break;
            case ART_OBJ:
                break;
            case DESTRUCTIBLE_WALL:
                break;
            case SECRET_DOOR:
                break;
            case TRAP:
                break;
            case GUARD:
                break;
            case LIGHT_EMITTER:
                break;
            case WALL_WITH_LIGHT_OVERLAY:
                break;
            case WALL_WITH_DECOR_OVERLAY:
                break;
            case LOCAL_KEY:
                break;
            case GLOBAL_KEY:
                break;
            case DESTRUCTIBLE:
                break;
            case SPECIAL_CONTAINER:
                break;
            case SPECIAL_DOOR:
                break;
            case SPECIAL_ART_OBJ:
                break;
        }
        return null;
    }

    private static String getWallWeightMap(DECOR_STYLE style) {
        WeightMap<String> map = new WeightMap<String>();

        map.put(BF_OBJ_TYPES_STRUCTURES.DELAPIDATED_FENCE.getName(), 1);

        return map.toString();
    }
}
