package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.libgdx.bf.overlays.WallMap;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.entity.BfObjEnums.*;
import main.system.auxiliary.RandomWizard;
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
            if (style != DEFAULT_STYLE)
                map = getWeightMap(cell, DEFAULT_STYLE);
        if (map == null) {
            throw new RuntimeException();
        }
        return map.toString();
    }

    public static WeightMap<String> getWeightMap(ROOM_CELL cell, DUNGEON_STYLE style) {

        switch (cell) {
            case SECRET_DOOR:
                return getWallWeightMap(style, null);
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
                WeightMap<String> map = getSpecDecorWeightMap(style, false);
                if (map != null)
                    return map;
                return getDecorWeightMap(style, false);
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
        WeightMap<String> map = new WeightMap<>();
        switch (style) {
            case Holy:
                if (overlaying) {
                    map
                     .chain(HANGING_HOLY_FIRE_BRAZIER, 30);
                } else
                    map
                     .chain(HOLY_FLAME_BRAZIER, 30)
                     .chain(FIERY_SKULL, 10);
            case Knightly:
                return overlaying
                 ? new WeightMap<String>().
                 chain(TORCH, 10).
                 chain(HANGING_BRAZIER, 10)
                 : new WeightMap<String>().
                 chain(BRAZIER, 12).
                 chain(FIREPIT, 2).
                 chain(OFFERING_FIRE, 3)
                 ;
            case Brimstone:
                if (overlaying) {
                    map
                     .chain(BURNING_SKULL, 10)
                     .chain(FIERY_SKULL, 20)
                     .chain(HANGING_HELLFIRE_BRAZIER, 40);
                } else
                    map
                     .chain(HELLFIRE_BRAZIER, 30)
                     .chain(FIERY_SKULL, 20)
                     ;
                break;
            case Pagan:
                map.
                 chain(GLOWING_RUNES, 5);
            case Grimy:
                return overlaying
                 ? new WeightMap<String>().
                 chain(TORCH, 10).
                 chain(HANGING_BRAZIER, 10)
                 : new WeightMap<String>().
                 chain(BRAZIER, 10).
                 chain(OFFERING_FIRE, 5).
                 chain(FIREPIT, 1)
                 ;

            case PureEvil:
                map
                 .chain(BURNING_SKULL, 10)
                 .chain(FIERY_SKULL, 30)
                ;
            case DarkElegance:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_LANTERN, 4).
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
                 chain(ELVEN_LANTERN, 4).
                 chain(HANGING_BRAZIER, 1).
                 chain(HANGING_WITCHFIRE_BRAZIER, 1).
                 chain(HANGING_COLDFIRE_BRAZIER, 1).
                 chain(AMETHYST_LANTERN, 1).
                 chain(SAPPHIRE_LANTERN, 1).
                 chain(EMERALD_LANTERN, 1)
                 : new WeightMap<String>().
                 chain(NETHERFLAME_BRAZIER, 10).
                 chain(WITCHFIRE_BRAZIER, 5).
                 chain(ELVEN_BRAZIER, 3).
                 chain(COLDFIRE_BRAZIER, 1);

            case Arcane:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_LANTERN, 6).
                 chain(HANGING_NETHERFLAME_BRAZIER, 1).
                 chain(HANGING_COLDFIRE_BRAZIER, 1)
                 : new WeightMap<String>().
                 chain(WITCHFIRE_BRAZIER, 10).
                 chain(ELVEN_BRAZIER, 3).
                 chain(NETHERFLAME_BRAZIER, 1).
                 chain(COLDFIRE_BRAZIER, 1);
            case Cold:
                return overlaying
                 ? new WeightMap<String>().
                 chain(HANGING_COLDFIRE_BRAZIER, 10).
                 chain(ELVEN_LANTERN, 4).
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
                 chain(BF_OBJ_SUB_TYPES_CRYSTAL.LUCENT_CRYSTAL, 3);
        }
        return null;
    }

    private static WeightMap<String> getDestructibleWeightMap(DUNGEON_STYLE style) {
        WeightMap<String> map = new WeightMap<>();
        switch (style) {
            case Cold:
                map.chain(BF_OBJ_SUB_TYPES_ROCKS.ICE_SPIKE, 50).
                 chain(BF_OBJ_SUB_TYPES_RUINS.SNOWCOVERED_RUINS, 40)
                ;
            case DarkElegance:
                map.chain(BF_OBJ_SUB_TYPES_CONSTRUCT.OBELISK, 15).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 5).
                 chain(BF_OBJ_SUB_TYPES_STATUES.GARGOYLE_GUARDIAN, 10).
                 chain(BF_OBJ_SUB_TYPES_STATUES.GARGOYLE_STATUE, 10).
                 chain(BF_OBJ_SUB_TYPES_STATUES.MARBLE_GARGOYLE, 10);
            case PureEvil:
            case Somber:
                map.
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.MARBLE_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.FALLEN_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_STRUCTURE, 10).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.ROCKS, 10).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.RUNESTONE, 10)
                ;
                break;
            case Arcane:
                map.
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.OBELISK, 15).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 15).
                 chain(BF_OBJ_SUB_TYPES_STATUES.GARGOYLE_GUARDIAN, 20).
                 chain(BF_OBJ_SUB_TYPES_STATUES.GARGOYLE_STATUE, 20).
                 chain(BF_OBJ_SUB_TYPES_STATUES.MARBLE_GARGOYLE, 20).
                 chain(BF_OBJ_SUB_TYPES_MECHANICAL.CLOCKWORK_DEVICE, 20).
                 chain(BF_OBJ_SUB_TYPES_MECHANICAL.ARCANE_APPARATUS, 20).
                 chain(BF_OBJ_SUB_TYPES_MECHANICAL.ARCANE_MACHINES, 20).
                 chain(BF_OBJ_SUB_TYPES_MECHANICAL.CHARGER, 20);
            case Holy:
                map.
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.WOODEN_BENCH, 35).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.MARBLE_COLUMN, 30).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.COLUMN, 20).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.RACK, 15).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.GOLDEN_FOUNTAIN, 15).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.OBELISK, 15).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.ANCIENT_FONTAIN, 15).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.TORTURE_DEVICE, 15).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.TORTURE_CHAIR, 15)
                ;
                break;
            case Knightly:
                map.
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.FOUNDATION, 15).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.RACK, 15).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.GAMBLING_TABLE, 15).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.ARMOR_SUIT, 25).
                 chain(BF_OBJ_SUB_TYPES_STATUES.IRON_KNIGHT, 20).
                 chain(BF_OBJ_SUB_TYPES_STATUES.STONE_KNIGHT, 20).
                 chain(BF_OBJ_SUB_TYPES_INTERIOR.ARMOR_STAND, 20)
                ;
                break;
            case Brimstone:
                map.
                 chain(BF_OBJ_SUB_TYPES_CRYSTAL.CHAOS_CRYSTAL, 20)
                ;
            case Stony:
                map.
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.STALAGMITE, 15).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.STALACTITE, 15).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.NATURAL_COLUMN, 20).
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.GIANT_MUSHROOM, 5);

            case Pagan:
            case Grimy:
                map.
                 chain(BF_OBJ_SUB_TYPES_STATUES.DWARF_STATUE, 10).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.RUNE_COLUMN, 10).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.FALLEN_COLUMN, 15).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_COLUMN, 15).
                 chain(BF_OBJ_SUB_TYPES_RUINS.RUINED_STRUCTURE, 5).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.ROCKS, 15).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.RUNESTONE, 5)
                ;
                break;
            default:
                return null;
        }
        return map;
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
                 chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SHRINE, 4).
                 chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SPHERE, 4).
                 chain(BF_OBJ_SUB_TYPES_MAGICAL.ALTAR, 4).
                 chain(BF_OBJ_SUB_TYPES_STATUES.TWILIGHT_ANGEL, 1)
                 ;

            case Stony:
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_DUNGEON.YELLOW_LIMINESCENT_FUNGI, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CRYSTAL.DARK_CRYSTAL, 6).
                 chain(BF_OBJ_SUB_TYPES_ROCKS.ROCKS, 4).
                 chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SHRINE, 4).
                 chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SPHERE, 4).
                 chain(BF_OBJ_SUB_TYPES_MAGICAL.ALTAR, 4)
                 ;
        }
        return null;
    }
    //TODO

    private static WeightMap<String> getDecorWeightMap(DUNGEON_STYLE style, boolean overlaying) {
        WeightMap<String> map = new WeightMap<>();
        switch (style) {
            case Brimstone:
                if (overlaying) {

                } else
                    map.
                     chain(BF_OBJ_SUB_TYPES_STATUES.DEVIL_STATUE, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.DEMON_STATUE, 20)
                     ;
            case PureEvil:
                if (overlaying) {

                } else
                    map.
                     chain(BF_OBJ_SUB_TYPES_STATUES.DARK_ONE, 6).
                     chain(BF_OBJ_SUB_TYPES_STATUES.DEVIL_STATUE, 4).
                     chain(BF_OBJ_SUB_TYPES_STATUES.OCCULT_STATUE, 10).
                     chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SHRINE, 14).
                     chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SPHERE, 14).
                     chain(BF_OBJ_SUB_TYPES_MAGICAL.ALTAR, 15).
                     chain(BF_OBJ_SUB_TYPES_STATUES.DECEIVER, 8)
                     ;
                break;
            case DarkElegance:
                if (overlaying) {

                } else
                    map.
                     chain(BF_OBJ_SUB_TYPES_STATUES.DARK_ANGEL_STATUE, 5)
                     ;
                break;
                case Somber:
                return overlaying
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_HANGING.ELDRITCH_RUNE, 10).
                 chain(BF_OBJ_SUB_TYPES_HANGING.ANCIENT_RUNE, 10).
                 chain(BF_OBJ_SUB_TYPES_HANGING.MAGIC_CIRCLES, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.ANCIENT_FONTAIN, 10).
                 chain(BF_OBJ_SUB_TYPES_STATUES.OCCULT_STATUE, 13).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.DARK_FONTAIN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONSTRUCT.OBELISK, 5).
                 chain(BF_OBJ_SUB_TYPES_COLUMNS.FALLEN_COLUMN, 5).
                 chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SHRINE, 4).
                 chain(BF_OBJ_SUB_TYPES_CONJURATE.ELDRITCH_SPHERE, 4).
                 chain(BF_OBJ_SUB_TYPES_MAGICAL.ALTAR, 4).
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
            case Holy:
                if (overlaying) {

                } else
                    map.
                     chain(BF_OBJ_SUB_TYPES_CONSTRUCT.GOLDEN_FOUNTAIN, 15).
                     chain(BF_OBJ_SUB_TYPES_STATUES.TWILIGHT_ANGEL, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.ANGEL_STATUE, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.MARBLE_GARGOYLE, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.GARGOYLE_STATUE, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.GARGOYLE_GUARDIAN, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.CATHEDRAL_GARGOYLE, 20)
                     ;
                break;
            case Knightly:
                if (overlaying) {

                } else
                    map.
                     chain(BF_OBJ_SUB_TYPES_INTERIOR.ARMOR_SUIT, 25).
                     chain(BF_OBJ_SUB_TYPES_CONSTRUCT.FOUNDATION, 15).
                     chain(BF_OBJ_SUB_TYPES_CONSTRUCT.LIONHEAD_FONTAIN, 15).

                     chain(BF_OBJ_SUB_TYPES_STATUES.GOLDEN_KNIGHT, 10).
                     chain(BF_OBJ_SUB_TYPES_STATUES.IRON_KNIGHT, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.SILVER_KNIGHT, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.STONE_KNIGHT, 20).
                     chain(BF_OBJ_SUB_TYPES_INTERIOR.ARMOR_STAND, 20)
                     ;
                break;

            case Pagan:
                if (overlaying) {

                } else
                    map.
                     chain(BF_OBJ_SUB_TYPES_STATUES.DWARF_STATUE, 20).
                     chain(BF_OBJ_SUB_TYPES_STATUES.DWARF_STATUE, 20)
                     ;
                break;
            default:
                return null;
        }
        return map;
    }

    private static WeightMap<String> getContainerWeightMap(DUNGEON_STYLE style, boolean special) {
        switch (style) {
            case Knightly:
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
            case Holy:
            case DarkElegance:
                if (RandomWizard.random())
                    return special
                     ? new WeightMap<String>().
                     chain(BF_OBJ_SUB_TYPES_GRAVES.SEALED_SARCOPHAGUS, 10).
                     chain(BF_OBJ_SUB_TYPES_GRAVES.TOMB_NICHE, 10).
                     chain(BF_OBJ_SUB_TYPES_GRAVES.LORDUNICODE39CODEENDS_TOMB, 10).
                     chain(BF_OBJ_SUB_TYPES_GRAVES.NOBLE_GRAVESTONE, 10).
                     chain(BF_OBJ_SUB_TYPES_GRAVES.SARCOPHAGUS, 10)
                     :
                     null;
            case Somber:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_SKULL, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.OLD_BONES, 15).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.COFFIN, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SEALED_SARCOPHAGUS, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.TOMB_NICHE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.GRAVESTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.TOMBSTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.LORDUNICODE39CODEENDS_TOMB, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.NOBLE_GRAVESTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.OVERGROWN_GRAVE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.OVERGROWN_TOMBSTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SARCOPHAGUS, 10)
                 : new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.ASH_URN, 10).
                 chain(BF_OBJ_SUB_TYPES_CONTAINER.ENCHANTED_ASH_URN, 5).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.OLD_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.RUSTY_CHEST, 10).
                 chain(BF_OBJ_SUB_TYPES_TREASURE.TREASURE_PILE, 5)
                 ;
            case Brimstone:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_REMAINS.SHATTERED_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_SKULL, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.OLD_BONES, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.DECOMPOSING_CORPSE, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.CHARRED_REMAINS, 40)
                 :
                 null;
            case PureEvil:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_REMAINS.SHATTERED_REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_SKULL, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.DECOMPOSING_CORPSE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.COFFIN, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SEALED_SARCOPHAGUS, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.TOMB_NICHE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.GRAVESTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SARCOPHAGUS, 10)
                 :
                 null;
            case Cold:
                return special
                 ? new WeightMap<String>().
                 chain(BF_OBJ_SUB_TYPES_REMAINS.ANCIENT_SKULL, 10).
                 chain(BF_OBJ_SUB_TYPES_REMAINS.REMAINS, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.COFFIN, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SEALED_SARCOPHAGUS, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.TOMB_NICHE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.GRAVESTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.TOMBSTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.LORDUNICODE39CODEENDS_TOMB, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.NOBLE_GRAVESTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.OVERGROWN_GRAVE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.OVERGROWN_TOMBSTONE, 10).
                 chain(BF_OBJ_SUB_TYPES_GRAVES.SARCOPHAGUS, 10)
                 :
                 null;
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
        Boolean b = indestructible_nullForSecret;
        switch (style) {
            case Brimstone:
                return new WeightMap<String>().
                 chain(VOLCANIC_WALL.getName() + WallMap.v(b), 10).
                 chain(JAGGED_STONE_WALL.getName() + WallMap.v(b), 1).
                 chain(WALL_OF_SKULLS.getName() + WallMap.v(b), 1).
                 chain(BONE_WALL.getName() + WallMap.v(b), 1);

            case Pagan:
                return new WeightMap<String>().
                 chain(WOODEN_WALL.getName() + WallMap.v(b), 10).
                 chain(WOODEN_PLANKS.getName() + WallMap.v(b), 1).
                 chain(ROTTEN_PLANKS.getName() + WallMap.v(b), 1);
            case Stony:
                return new WeightMap<String>().
                 chain(CAVE_WALL.getName() + WallMap.v(b), 10).
                 chain(VOLCANIC_WALL.getName() + WallMap.v(b), 1).
                 chain(SOLID_ROCK.getName() + WallMap.v(b), 1).
                 chain(INSCRIBED_WALL.getName() + WallMap.v(b), 1);
            case DarkElegance:
                return new WeightMap<String>().
                 chain(ANCIENT_WALL.getName() + WallMap.v(b), 10).
                 chain(CRUMBLING_WALL.getName() + WallMap.v(b), 1).
                 chain(OLD_STONE_WALL.getName() + WallMap.v(b), 1);
            case Grimy:
                return new WeightMap<String>().
                 chain(CRUMBLING_WALL.getName() + WallMap.v(b), 10).
                 chain(ANCIENT_WALL.getName() + WallMap.v(b), 1).
                 chain(OLD_STONE_WALL.getName() + WallMap.v(b), 1);
            case Holy:
            case Knightly:
                return new WeightMap<String>().
                 chain(STONE_WALL.getName() + WallMap.v(b), 10).
                 chain(BRICK_WALL.getName() + WallMap.v(b), 1).
                 chain(OLD_STONE_WALL.getName() + WallMap.v(b), 1);
            case Cold:
                return getWallWeightMap(DUNGEON_STYLE.Somber, indestructible_nullForSecret).
                 chain(ICE_BLOCK.getName() + WallMap.v(b), 5).
                 chain(ICE_WALL.getName() + WallMap.v(b), 5).
                 chain(IRON_FENCE.getName() + WallMap.v(b), 3);
            case PureEvil:
                return new WeightMap<String>().
                 chain(BONE_WALL.getName() + WallMap.v(b), 10).
                 chain(WALL_OF_SKULLS.getName() + WallMap.v(b), 1).
                 chain(BLACK_MARBLE_WALL.getName() + WallMap.v(b), 1);

            case Somber:
                return new WeightMap<String>().
                 chain(OLD_STONE_WALL.getName() + WallMap.v(b), 10).
                 chain(CRUMBLING_WALL.getName() + WallMap.v(b), 1).
                 chain(ANCIENT_WALL.getName() + WallMap.v(b), 1);
            case Arcane:
                return getWallWeightMap(DUNGEON_STYLE.Somber, indestructible_nullForSecret).chain(
                 IRON_WALL.getName() + WallMap.v(b), 10
                );
        }
        return null;
    }
}
