package main.content.enums.entity;

/**
 * Created by JustMe on 2/14/2017.
 */
public class BfObjEnums {
    public enum BF_OBJECT_GROUP {
        WALL, COLUMNS, RUINS, CONSTRUCT, GATEWAY, GRAVES,

        WINDOWS, MAGICAL, HANGING, INTERIOR, STATUES,

        LOCK, ENTRANCE, TRAP, DOOR, LIGHT_EMITTER, CONTAINER, TREASURE,

        DUNGEON, WATER, TREES, ROCKS, VEGETATION, REMAINS, CRYSTAL,;
    }

    public enum BF_OBJECT_SIZE {

        TINY, SMALL, MEDIUM, LARGE, HUGE
    }

    public enum BF_OBJECT_TAGS {
        INDESTRUCTIBLE,
        PASSABLE,
        SUMMONED,
        COLLAPSABLE,
        ASSYMETRICAL,
        LANDSCAPE,
        OVERLAYING,
        WATER,
        ITEM,
        HUGE,
        LARGE
    }

    public enum BF_OBJECT_TYPE {
        NATURAL, STRUCTURE, PROP, SPECIAL
    }

    public enum BF_OBJ_MATERIAL {
        RED_OAK,
        IRONWOOD,
        BLACKWOOD,
        PALEWOOD,
        BILEWOOD,
        WAILWOOD,
        FEYWOOD,
        COTTON,
        SILK,
        IVORY,
        BLACK_BONE,
        MAN_BONE,
        DRAGON_BONE,
        THIN_LEATHER,
        TOUGH_LEATHER,
        THICK_LEATHER,
        FUR,
        LIZARD_SKIN,
        TROLL_SKIN,
        DRAGONHIDE,
        GRANITE,
        MARBLE,
        ONYX,
        OBSIDIAN,
        CRYSTAL,
        SOULSTONE,
        STAR_EMBER,
        SILVER,
        GOLD,
        COPPER,
        BRASS,
        BRONZE,
        IRON,
        STEEL,
        MITHRIL,
        PLATINUM,
        ADAMANTIUM,
        METEORITE,
        BRIGHT_STEEL,
        DEFILED_STEEL,
        DARK_STEEL,
        WRAITH_STEEL,
        PALE_STEEL,
        WARP_STEEL,
        DEMON_STEEL,
        MOON_SILVER,
        ELDRITCH_STEEL
    }

    public enum BF_OBJ_QUALITY {
        TOUGH,
        CRUMBLING,
        BRITTLE,
        DURABLE,
        RESISTANT,
        ARMORED,
        THICK,
        TOUGH_II,
        CRUMBLING_II,
        BRITTLE_II,
        DURABLE_II,
        RESISTANT_II,
        ARMORED_II,
        THICK_II,
        TOUGH_III,
        CRUMBLING_III,
        BRITTLE_III,
        DURABLE_III,
        RESISTANT_III,
        ARMORED_III,
        THICK_III,
    }

    public enum BF_OBJ_WEIGHT {
        TINY,

        COLOSSAL,
    }
}
