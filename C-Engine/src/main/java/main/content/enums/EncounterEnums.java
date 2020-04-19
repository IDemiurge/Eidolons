package main.content.enums;

/**
 * Created by JustMe on 2/14/2017.
 */
public class EncounterEnums {
    public enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum ENCOUNTER_GROUP {
        LIGHT, DARK, UNDEAD, ARCANE, DUNGEON, MONSTERS, DEMONS, HUMANS, MISC, MECHANICUM,
    }

    public enum ENCOUNTER_SETS {
        BANDITS,
        MERCENARIES,
        GREENSKINS,
        UNDEAD_HORDES,
        DUNGEON_MONSTERS,
        FOREST_GUARDIANS,
        DARK_WOOD,
        AVIANS,
        DEMONS_OF_ABYSS,

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

    //    PRE_BATTLE_EVENT("encounters", null),
//    AFTER_BATTLE_EVENT(
    public enum REINFORCEMENT_CHANCE {
none, low, normal, high, always
    }

    public enum REINFORCEMENT_TYPE {
        call_help, portal, patrol, ambush,
    }

    public enum REINFORCEMENT_STRENGTH {
        low, normal, high
    }

    public enum LOOT_TYPE {
        gold, artifact, treasure, arcane, items, random, junk, none
    }

    public enum GROWTH_PRIORITIES {
        GROUP, LEVEL, FILL, EXTEND
    }

    /*
        Guard Ai - stand still, only turn this way or that
        > alerted - goes to investigate
        ++ voice comments
        ++ chance to Sleep
        Idlers/Crowd - chaotic wandering within the Block
        > alerted - hold still and enters Alert Mode
        ++ periodic Rest
        Patrol Ai - orderly traversal of the Block
        > alerted - investigate
        Stalker Ai - moves in stealth mode within the Zone, follows enemy until they enter combat, then add to the AggroGroup
        > alerted -
        Boss Ai - walks in small circles
         */
    public enum UNIT_GROUP_TYPE {
        //this is gonna be used for fill probably too
        GUARDS(0.05f),
        PATROL(1f),
        AMBUSH(0.15f),
        CROWD(0.3f),
        IDLERS(0.2f),
        STALKER(1.25f),
        BOSS(0.1f),
        ;
        //determines what? Except AI behavior -
        // N preference, power level, placement,

        private float speedMod = 0;

        UNIT_GROUP_TYPE(float speedMod) {
            this.speedMod = speedMod;
        }

        public float getSpeedMod() {
            return speedMod;
        }
    }
}
