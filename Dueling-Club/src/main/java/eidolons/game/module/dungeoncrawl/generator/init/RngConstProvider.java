package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.enums.entity.BfObjEnums.BF_OBJ_TYPES_DUNGEON;
import main.content.enums.entity.BfObjEnums.BF_OBJ_TYPES_STRUCTURES;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/20/2018.
 */
public class RngConstProvider {

    public static String getWeightString(ROOM_CELL cell, DUNGEON_STYLE style) {
        //objGroups like unitGroups?
        WeightMap<String> map = getWeightMap(cell, style);

        return map.toString();
    }

    private static WeightMap<String> getWeightMap(ROOM_CELL cell, DUNGEON_STYLE style) {

        switch (cell) {
            case WALL:
                return getWallWeightMap(style);
            case FLOOR:
                break;
            case ENTRANCE:
                break;
            case EXIT:
                break;
            case CONTAINER:
                return getWallWeightMap(style);
            case DOOR:
                return getWallWeightMap(style);
            case ART_OBJ:
                return getWallWeightMap(style);
            case DESTRUCTIBLE_WALL:
                return getWallWeightMap(style);

            case LIGHT_EMITTER:
                return getWallWeightMap(style);
            case WALL_WITH_LIGHT_OVERLAY:
                break;
            case WALL_WITH_DECOR_OVERLAY:
                break;

            case SECRET_DOOR:
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


    private static String getDecorWeightString(DUNGEON_STYLE style) {
        WeightMap<String> map = new WeightMap<>();

        map.put(BF_OBJ_TYPES_DUNGEON.GIANT_MUSHROOM.getName(), 1);

        return map.toString();
    }

    private static WeightMap<String> getWallWeightMap(DUNGEON_STYLE style) {
        switch (style) {
            case Brimstone:
                break;
            case Survivor:
                break;
            case DarkElegance:
                break;
            case Grimy:
                break;
            case Castle:
                break;
        }
       return new WeightMap<String>().putChain(BF_OBJ_TYPES_STRUCTURES.
        DELAPIDATED_FENCE.getName(), 1);
    }
}
