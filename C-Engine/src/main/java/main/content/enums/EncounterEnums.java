package main.content.enums;

/**
 * Created by JustMe on 2/14/2017.
 */
public class EncounterEnums {
    public static enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum ENCOUNTER_GROUP {
        LIGHT, DARK, UNDEAD, ARCANE, DUNGEON, MONSTERS, DEMONS, HUMANS, MISC, MECHANICUM,
    }

    public enum ENCOUNTER_SUBGROUP {
        AVIANS,
        NOCTURNAL,
        WULFEN,
        SWAMP,
        DUNGEON_MONSTERS,
        CRITTERS,
        COLONY,
        ORCS,
        GOBLINS,
        GIANTS,
        WARP_DEMONS,
        DEMONS,
        FIRE_DEMONS,
        CHAOS_CULTISTS,
        DARK_CULTISTS,
        DEATH_CULTISTS,
        ARCANE_APOSTATES,
        SHADOW_APOSTATES,
        WIZARDS,
        BANDITS,
        DESERTERS,
        PIRATES,
        MAGICAL,
        RAVENGUARD,
        RED_DAWN,
        SILVERLANCE,
        PUTRID_UNDEAD,
        UNDEAD,
        GORY_UNDEAD,
        WRAITHS,
        VAMPIRES,
        TWILIGHT_FORCES,
        GHOSTS,
        MECHANICUM,
        POSSESSED,
        WOOD_ANIMALS,
        SPIDERS,

    }

    public enum ENCOUNTER_TYPE {
        CONTINUOUS, REGULAR, ELITE, BOSS
    }

    public enum GROWTH_PRIORITIES {
        GROUP, LEVEL, FILL, EXTEND
    }
}