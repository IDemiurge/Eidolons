package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.enums.entity.BfObjEnums.*;
import main.system.datatypes.WeightMap;

import static main.content.enums.entity.BfObjEnums.BF_OBJ_SUB_TYPES_LIGHT_EMITTER.*;
import static main.content.enums.entity.BfObjEnums.BF_OBJ_SUB_TYPES_WALL.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class RngBfObjProvider {

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
                return getEntranceWeightMap(style, false);
            case EXIT:
                return getEntranceWeightMap(style, true);
            case SPECIAL_CONTAINER:
                return getContainerWeightMap(style, true);
            case CONTAINER:
                return getContainerWeightMap(style, false);
            case DOOR:
                return getDoorWeightMap(style, false);
            case SPECIAL_DOOR:
                return getDoorWeightMap(style, true);
            case SPECIAL_ART_OBJ:
                return getSpecDecorWeightMap(style, false);
            case ART_OBJ:
                return getDecorWeightMap(style, false);
            case WALL_WITH_DECOR_OVERLAY:
                return getDecorWeightMap(style, true);
            case DESTRUCTIBLE_WALL:
                return getWallWeightMap(style);

            case LIGHT_EMITTER:
                return getLightEmitterWeightMap(style, false);
            case WALL_WITH_LIGHT_OVERLAY:
                return getLightEmitterWeightMap(style, true);

        }
        return null;
    }

    private static WeightMap<String> getLightEmitterWeightMap(
     DUNGEON_STYLE style, boolean overlaying) {
        switch (style) {
            case DarkElegance:
                return overlaying
                 ? new WeightMap<String>().
                 putChain(HANGING_WITCHFIRE_BRAZIER.getName(), 10).
                 putChain(ELVEN_LANTERN.getName(), 1).
                 putChain(HANGING_NETHERFLAME_BRAZIER.getName(), 1).
                 putChain(HANGING_COLDFIRE_BRAZIER.getName(), 1).
                 putChain(AMETHYST_LANTERN.getName(), 1).
                 putChain(SAPPHIRE_LANTERN.getName(), 1).
                 putChain(EMERALD_LANTERN.getName(), 1)
                 : new WeightMap<String>().
                 putChain(WITCHFIRE_BRAZIER.getName(), 10).
                 putChain(ELVEN_BRAZIER.getName(), 1).
                 putChain(NETHERFLAME_BRAZIER.getName(), 1).
                 putChain(COLDFIRE_BRAZIER.getName(), 1);
            case Somber:
                return overlaying
                 ? new WeightMap<String>().
                 putChain(HANGING_NETHERFLAME_BRAZIER.getName(), 10).
                 putChain(HANGING_BRAZIER.getName(), 1).
                 putChain(HANGING_WITCHFIRE_BRAZIER.getName(), 1).
                 putChain(HANGING_COLDFIRE_BRAZIER.getName(), 1).
                 putChain(AMETHYST_LANTERN.getName(), 1).
                 putChain(SAPPHIRE_LANTERN.getName(), 1).
                 putChain(EMERALD_LANTERN.getName(), 1)
                 : new WeightMap<String>().
                 putChain(NETHERFLAME_BRAZIER.getName(), 10).
                 putChain(ELDRITCH_ROD.getName(), 1).
                 putChain(WITCHFIRE_BRAZIER.getName(), 1).
                 putChain(COLDFIRE_BRAZIER.getName(), 1);

            case Arcane:
                return overlaying
                 ? new WeightMap<String>().
                 putChain(HANGING_WITCHFIRE_BRAZIER.getName(), 10).
                 putChain(ELVEN_LANTERN.getName(), 1).
                 putChain(HANGING_NETHERFLAME_BRAZIER.getName(), 1).
                 putChain(HANGING_COLDFIRE_BRAZIER.getName(), 1)
                 : new WeightMap<String>().
                 putChain(WITCHFIRE_BRAZIER.getName(), 10).
                 putChain(ELVEN_BRAZIER.getName(), 1).
                 putChain(NETHERFLAME_BRAZIER.getName(), 1).
                 putChain(COLDFIRE_BRAZIER.getName(), 1);
            case Cold:
                return overlaying
                 ? new WeightMap<String>().
                 putChain(HANGING_COLDFIRE_BRAZIER.getName(), 10).
                 putChain(HANGING_BRAZIER.getName(), 1).
                 putChain(HANGING_WITCHFIRE_BRAZIER.getName(), 1).
                 putChain(HANGING_NETHERFLAME_BRAZIER.getName(), 1)
                 : new WeightMap<String>().
                 putChain(COLDFIRE_BRAZIER.getName(), 10).
                 putChain(BRAZIER.getName(), 1).
                 putChain(WITCHFIRE_BRAZIER.getName(), 1).
                 putChain(NETHERFLAME_BRAZIER.getName(), 1);
        }
        return null;
        /*

         */
    }

    private static WeightMap<String> getSpecDecorWeightMap(DUNGEON_STYLE style, boolean overlaying) {
        return getDecorWeightMap(style, overlaying);
    }
    //TODO

    private static WeightMap<String> getDecorWeightMap(DUNGEON_STYLE style, boolean overlaying) {
        switch (style) {
            case Somber:
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_HANGING.HANGING_SHIELD, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_STATUES.ANGEL_STATUE, 6).
                 chain(BF_OBJ_SUB_TYPES_STATUES.ELVEN_STATUE, 4).
                 chain(BF_OBJ_SUB_TYPES_STATUES.TWILIGHT_ANGEL, 1).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.MARBLE_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.OBELISK, 5).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 10)
                 ;
        }
        return null;
    }

    private static WeightMap<String> getContainerWeightMap(DUNGEON_STYLE style, boolean special) {
        switch (style) {
            case Somber:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_GRAVES.COFFIN, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SEALED_SARCOPHAGUS, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SARCOPHAGUS, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.ASH_URN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.ENCHANTED_ASH_URN, 5).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.OLD_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.RUSTY_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.TREASURE_PILE, 5)
                 ;
        }
        return null;
    }

    private static WeightMap<String> getDoorWeightMap(DUNGEON_STYLE style, boolean special) {
        switch (style) {
            case Somber:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DOOR.BONE_DOOR_ENCHANTED, 10).
                 chain(BF_OBJ_SUB_TYPES_DOOR.CRIMSON_DOOR, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DOOR.ANCIENT_DOOR, 10).
                 chain(BF_OBJ_SUB_TYPES_DOOR.DARK_DOOR, 10).
                 chain(BF_OBJ_SUB_TYPES_DOOR.STONE_DOOR, 10).
                 chain(BF_OBJ_SUB_TYPES_DOOR.VAULT_DOOR, 10).
                 chain(BF_OBJ_SUB_TYPES_DOOR.SKULL_DOOR, 10)
                 ;
        }
        return null;
    }
    private static WeightMap<String> getEntranceWeightMap(DUNGEON_STYLE style, boolean exit) {
        switch (style) {
            case Somber:
                return exit
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.WINDING_DOWNWARD_STAIRS, 2).
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.DARK_WINDING_DOWNWARD_STAIRS, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.WINDING_UPWARD_STAIRS, 2).
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.DARK_WINDING_UPWARD_STAIRS, 10)
                 ;
        }
        return null;
    }
    private static WeightMap<String> getWallWeightMap(DUNGEON_STYLE style) {
        //TODO check surface! cemetery etc
        switch (style) {
            case Brimstone:
                return new WeightMap<String>().
                 putChain(VOLCANIC_WALL.getName(), 10).
                 putChain(JAGGED_STONE_WALL.getName(), 1).
                 putChain(WALL_OF_SKULLS.getName(), 1).
                 putChain(BONE_WALL.getName(), 1);
            case Holy:
                break;
            case Pagan:
                return new WeightMap<String>().
                 putChain(WOODEN_WALL.getName(), 10).
                 putChain(WOODEN_PLANKS.getName(), 1).
                 putChain(ROTTEN_PLANKS.getName(), 1);
            case Survivor:
                return new WeightMap<String>().
                 putChain(CAVE_WALL.getName(), 10).
                 putChain(VOLCANIC_WALL.getName(), 1).
                 putChain(SOLID_ROCK.getName(), 1).
                 putChain(INSCRIBED_WALL.getName(), 1);
            case DarkElegance:
                return new WeightMap<String>().
                 putChain(ANCIENT_WALL.getName(), 10).
                 putChain(CRUMBLING_WALL.getName(), 1).
                 putChain(OLD_STONE_WALL.getName(), 1);
            case Grimy:
                return new WeightMap<String>().
                 putChain(CRUMBLING_WALL.getName(), 10).
                 putChain(ANCIENT_WALL.getName(), 1).
                 putChain(OLD_STONE_WALL.getName(), 1);
            case Castle:
                return new WeightMap<String>().
                 putChain(STONE_WALL.getName(), 10).
                 putChain(BRICK_WALL.getName(), 1).
                 putChain(OLD_STONE_WALL.getName(), 1);
            case PureEvil:
                return new WeightMap<String>().
                 putChain(BONE_WALL.getName(), 10).
                 putChain(WALL_OF_SKULLS.getName(), 1).
                 putChain(BLACK_MARBLE_WALL.getName(), 1);
            case Somber:
                return new WeightMap<String>().
                 putChain(OLD_STONE_WALL.getName(), 10).
                 putChain(CRUMBLING_WALL.getName(), 1).
                 putChain(ANCIENT_WALL.getName(), 1);
            case Arcane:
                break;
            case Cold:
                break;
        }
        return new WeightMap<String>().
         putChain(BF_OBJ_TYPES_STRUCTURES.
          DELAPIDATED_FENCE.getName(), 1);
    }
}
