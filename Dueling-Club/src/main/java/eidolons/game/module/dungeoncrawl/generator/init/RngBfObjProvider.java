package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import eidolons.libgdx.bf.overlays.WallMap;
import main.content.enums.DungeonEnums;
import main.content.enums.entity.BfObjEnums.*;
import main.system.datatypes.WeightMap;

import static main.content.enums.entity.BfObjEnums.BF_OBJ_SUB_TYPES_LIGHT_EMITTER.*;
import static main.content.enums.entity.BfObjEnums.BF_OBJ_SUB_TYPES_WALL.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class RngBfObjProvider {

    private static final DUNGEON_STYLE DEFAULT_STYLE = DungeonEnums.DUNGEON_STYLE.Somber;

    public static String getWeightString(ROOM_CELL cell, DUNGEON_STYLE style) {
        //objGroups like unitGroups?
        WeightMap<String> map = getWeightMap(cell, style);
        if (map == null)
             if (style !=DEFAULT_STYLE)
                 map= getWeightMap(cell, DEFAULT_STYLE) ;
        if (map == null)
        {
            throw new RuntimeException();
        }
        return map.toString();
    }

    public static WeightMap<String> getWeightMap(ROOM_CELL cell, DUNGEON_STYLE style) {

        switch (cell) {
            case SECRET_DOOR:
                return getWallWeightMap(style, null );
            case WALL:
                return getWallWeightMap(style, false);
            case INDESTRUCTIBLE:
                return getWallWeightMap(style, true);
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

            case LIGHT_EMITTER:
                return getLightEmitterWeightMap(style, false);
            case WALL_WITH_LIGHT_OVERLAY:
                return getLightEmitterWeightMap(style, true);

            case DESTRUCTIBLE:
                return getDestructibleWeightMap(style);
        }

        return null;
    }

    private static WeightMap<String> getLightEmitterWeightMap(
     DUNGEON_STYLE style, boolean overlaying) {
        switch (style) {
            case DarkElegance:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_LANTERN, 1).
                 chain(HANGING_NETHERFLAME_BRAZIER, 1).
                 chain(HANGING_COLDFIRE_BRAZIER, 1).
                 chain(AMETHYST_LANTERN, 1).
                 chain(SAPPHIRE_LANTERN, 1).
                 chain(EMERALD_LANTERN, 1)
                 : new WeightMap<String>().
                 chain(WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_BRAZIER, 1).
                 chain(NETHERFLAME_BRAZIER, 1).
                 chain(COLDFIRE_BRAZIER, 1);
            case Somber:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_NETHERFLAME_BRAZIER, 10).
                 chain(HANGING_BRAZIER, 1).
                 chain(HANGING_WITCHFIRE_BRAZIER, 1).
                 chain(HANGING_COLDFIRE_BRAZIER, 1).
                 chain(AMETHYST_LANTERN, 1).
                 chain(SAPPHIRE_LANTERN, 1).
                 chain(EMERALD_LANTERN, 1)
                 : new WeightMap<String>().
                 chain(NETHERFLAME_BRAZIER, 10).
                 chain(ELDRITCH_ROD, 1).
                 chain(WITCHFIRE_BRAZIER, 1).
                 chain(COLDFIRE_BRAZIER, 1);

            case Arcane:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_LANTERN, 1).
                 chain(HANGING_NETHERFLAME_BRAZIER, 1).
                 chain(HANGING_COLDFIRE_BRAZIER, 1)
                 : new WeightMap<String>().
                 chain(WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_BRAZIER, 1).
                 chain(NETHERFLAME_BRAZIER, 1).
                 chain(COLDFIRE_BRAZIER, 1);
            case Cold:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_COLDFIRE_BRAZIER, 10).
                 chain(HANGING_BRAZIER, 1).
                 chain(HANGING_WITCHFIRE_BRAZIER, 1).
                 chain(HANGING_NETHERFLAME_BRAZIER, 1)
                 : new WeightMap<String>().
                 chain(COLDFIRE_BRAZIER, 10).
                 chain(BRAZIER, 1).
                 chain(WITCHFIRE_BRAZIER, 1).
                 chain(NETHERFLAME_BRAZIER, 1);
            case Stony:
                return overlaying
                 ?
                 new WeightMap<String>().
                  chain(BF_OBJ_SUB_TYPES_DUNGEON.YELLOW_LIMINESCENT_FUNGI, 10).
                  chain(BF_OBJ_SUB_TYPES_DUNGEON.GREEN_LIMINESCENT_FUNGI, 10).
                  chain(BF_OBJ_SUB_TYPES_DUNGEON.PURPLE_LIMINESCENT_FUNGI, 10) 
                 
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.GIANT_LUMINESCENT_MUSHROOM, 10).
                 chain(BF_OBJ_SUB_TYPES_CRYSTAL.LUCENT_CRYSTAL, 3) ;
        }
        return null;
    }

    private static WeightMap<String> getDestructibleWeightMap(DUNGEON_STYLE style) {
        switch (style) {
            case Somber:
               return  new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.MARBLE_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.FALLEN_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_STRUCTURE, 10).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.ROCKS, 10).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.RUNESTONE, 10)
                ;
            case Pagan:
            case Grimy:

            case Stony:

            case DarkElegance:
        }
        return null;
    }

    private static WeightMap<String> getSpecDecorWeightMap(DUNGEON_STYLE style, boolean overlaying) {
        //usable? supposedly rare...
        switch (style) {
            case Somber:
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_HANGING.GLOWING_GLYPH, 10).
                 chain(BF_OBJ_SUB_TYPES_HANGING.RUNE_INSCRIPTION, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_STATUES.OCCULT_STATUE, 10).
                 chain(BF_OBJ_SUB_TYPES_STATUES.ANGEL_STATUE, 6).
                 chain(BF_OBJ_SUB_TYPES_STATUES.ELVEN_STATUE, 4).
                 chain(BF_OBJ_SUB_TYPES_STATUES.TWILIGHT_ANGEL, 1)
                 ;

            case Stony:
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.IMP_STOOL, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.YELLOW_LIMINESCENT_FUNGI, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.IMP_STOOL, 10).
                 chain(BF_OBJ_SUB_TYPES_CRYSTAL.DARK_CRYSTAL, 6).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.ROCKS, 4).
                 chain(BF_OBJ_SUB_TYPES_MAGICAL.ALTAR, 1)
                 ;
        }
        return null;
    }
    //TODO

    private static WeightMap<String> getDecorWeightMap(DUNGEON_STYLE style, boolean overlaying) {
        switch (style) {
            case Somber:
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_HANGING.ELDRITCH_RUNE, 10).
                 chain(BF_OBJ_SUB_TYPES_HANGING.ANCIENT_RUNE, 10).
                 chain(BF_OBJ_SUB_TYPES_HANGING.MAGIC_CIRCLES, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.ANCIENT_FONTAIN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.MEMORIAL_STONE, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.DARK_FONTAIN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.FOUNDATION, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.MONOLITH, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.DARK_FONTAIN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.OBELISK, 5).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 10)
                 ; 
            case Stony:
 
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.AMETHYST, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.TOPAZ, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.RUBY, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.SAPPHIRE, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.TRANSLUCENT_FUNGI, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.YELLOW_LIMINESCENT_FUNGI, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.GREEN_LIMINESCENT_FUNGI, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.PURPLE_LIMINESCENT_FUNGI, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.FUNGI_VERDE, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.NATURAL_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.STALACTITE, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.STALAGMITE, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.GIANT_MUSHROOM, 10).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.UNDERGROUND_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 10)
                 ;
        }
        return null ;
    }

    private static WeightMap<String> getContainerWeightMap(DUNGEON_STYLE style, boolean special) {
        switch (style) {
            case Pagan:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.AXE_RACK, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.HALBERT_RACK, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.HAMMER_RACK, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.SWORD_RACK, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.SPEAR_RACK, 10).
                 //decor too?
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.CRATE, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.COBWEBBED_CRATE, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.BARREL, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.BARRELS, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.GREATSWORD_RACK, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.WEAPONS_RACK, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.OLD_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.RUSTY_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.TREASURE_PILE, 5)
                 ;
            case Grimy:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.CRATE, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.BARREL, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.BARRELS, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.COBWEBBED_CRATE, 10).
                 
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.CHARRED_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.DECOMPOSING_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.DESECRATED_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.OLD_BONES, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_SKULL, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.PUTRID_REMAINS, 10)

                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_TREASURE.OLD_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.RUSTY_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.IRON_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.TREASURE_PILE, 5)
                 ;
            case Stony:

                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.CHARRED_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.DECOMPOSING_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.DESECRATED_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.OLD_BONES, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_SKULL, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.PUTRID_REMAINS, 10) 
                 
                 : new WeightMap<String>(). 
                 chain(BF_OBJ_SUB_TYPES_TREASURE.IRON_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.TREASURE_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.OLD_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.RUSTY_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.TREASURE_PILE, 15)
                 ;
            case DarkElegance:


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
            case Stony:
                return exit
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.CAVE_EXIT, 2).
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.DARK_WINDING_DOWNWARD_STAIRS, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.CAVE_ENTRANCE, 2).
                 chain(BF_OBJ_SUB_TYPES_ENTRANCE.DARK_WINDING_UPWARD_STAIRS, 10)
                 ;
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
        return getEntranceWeightMap(DungeonEnums.DUNGEON_STYLE.Somber, exit);
    }

    private static WeightMap<String> getWallWeightMap(DUNGEON_STYLE style,
                                                      Boolean indestructible_nullForSecret) {
        //TODO check surface! cemetery etc WallMap.getWallVersion(
        Boolean b= indestructible_nullForSecret;
        switch (style) {
            case Brimstone:
                return new WeightMap<String>().
                 chain(VOLCANIC_WALL.getName()+ WallMap.v(b), 10).
                 chain(JAGGED_STONE_WALL.getName()+ WallMap.v(b), 1).
                 chain(WALL_OF_SKULLS.getName()+ WallMap.v(b), 1).
                 chain(BONE_WALL.getName()+ WallMap.v(b), 1);
            case Holy:
                break;
            case Pagan:
                return new WeightMap<String>().
                 chain(WOODEN_WALL.getName()+ WallMap.v(b), 10).
                 chain(WOODEN_PLANKS.getName()+ WallMap.v(b), 1).
                 chain(ROTTEN_PLANKS.getName()+ WallMap.v(b), 1);
            case Stony:
                return new WeightMap<String>().
                 chain(CAVE_WALL.getName()+ WallMap.v(b), 10).
                 chain(VOLCANIC_WALL.getName()+ WallMap.v(b), 1).
                 chain(SOLID_ROCK.getName()+ WallMap.v(b), 1).
                 chain(INSCRIBED_WALL.getName()+ WallMap.v(b), 1);
            case DarkElegance:
                return new WeightMap<String>().
                 chain(ANCIENT_WALL.getName()+ WallMap.v(b), 10).
                 chain(CRUMBLING_WALL.getName()+ WallMap.v(b), 1).
                 chain(OLD_STONE_WALL.getName()+ WallMap.v(b), 1);
            case Grimy:
                return new WeightMap<String>().
                 chain(CRUMBLING_WALL.getName()+ WallMap.v(b), 10).
                 chain(ANCIENT_WALL.getName()+ WallMap.v(b), 1).
                 chain(OLD_STONE_WALL.getName()+ WallMap.v(b), 1);
            case Knightly:
                return new WeightMap<String>().
                 chain(STONE_WALL.getName()+ WallMap.v(b), 10).
                 chain(BRICK_WALL.getName()+ WallMap.v(b), 1).
                 chain(OLD_STONE_WALL.getName()+ WallMap.v(b), 1);
            case PureEvil:
                return new WeightMap<String>().
                 chain(BONE_WALL.getName()+ WallMap.v(b), 10).
                 chain(WALL_OF_SKULLS.getName()+ WallMap.v(b), 1).
                 chain(BLACK_MARBLE_WALL.getName()+ WallMap.v(b), 1);
            case Somber:
                return new WeightMap<String>().
                 chain(OLD_STONE_WALL.getName()+ WallMap.v(b), 10).
                 chain(CRUMBLING_WALL.getName()+ WallMap.v(b), 1).
                 chain(ANCIENT_WALL.getName()+ WallMap.v(b), 1);
            case Arcane:
                break;
            case Cold:
                break;
        }
        return null ;
    }
}
