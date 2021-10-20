package eidolons.content.values;

import eidolons.content.PROPS;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;

import static eidolons.content.PARAMS.*;
import static eidolons.content.PROPS.*;
import static eidolons.content.values.ValuePages.*;
import static main.content.values.properties.G_PROPS.*;

public class A_ValuePages {

    public static final VALUE[] ITEM_PROPERTIES = {
            G_PROPS.MAIN_HAND_ITEM, G_PROPS.OFF_HAND_ITEM,
            // G_PROPS.RESERVE_MAIN_HAND_ITEM, G_PROPS.RESERVE_OFF_HAND_ITEM,
            // PROPS.INVENTORY,PROPS.JEWELRY,PROPS.FIRST_CLASS, PROPS.SECOND_CLASS,PROPS.KNOWN_SPELLS, PROPS.LEARNED_SPELLS,
            G_PROPS.ARMOR_ITEM, PROPS.QUICK_ITEMS,
    };
    public static final VALUE[] HERO_PROPS = {
            CLASSES_TIER_1,
            CLASSES_TIER_2,
            CLASSES_TIER_3,

            SKILLS_TIER_1,
            SKILLS_TIER_2,
            SKILLS_TIER_3,

            MASTERY_RANKS_1,
            MASTERY_RANKS_2,
            MASTERY_RANKS_3,
            PERKS,
            COMBAT_SPACES,
            VERBATIM_SPACES,
            MEMORIZED_SPACES,
    };

    public static final VALUE[] AV_VALUES = {
            SKILL_POINTS_UNSPENT,
            SPELL_POINTS_UNSPENT,
            MASTERY_RANKS_UNSPENT,
            ATTR_POINTS,
            CLASS_RANKS_UNSPENT,

            POWER,
            POWER_TOTAL ,
            SP_PTS_SPENT ,
            SK_PTS_SPENT
    };
    public static final VALUE[] ADDITIONAL_UNIT_PROPERTIES = {
            SOUNDSET, AI_TYPE,
    };
    public static final VALUE[] A_DERIVED_UNIT_PARAMETERS = {
            MIN_DAMAGE, MAX_DAMAGE, OFF_HAND_MIN_DAMAGE,
            OFF_HAND_MAX_DAMAGE, C_CARRYING_WEIGHT, ARMOR,
    };
    public static final VALUE[] A_BASE_UNIT_PARAMETERS = {
            UNIT_LEVEL,
            TOUGHNESS, ENDURANCE, FOCUS, ESSENCE,

            DAMAGE, OFF_HAND_DAMAGE,

            ATTACK, DEFENSE, EXTRA_ATTACKS,
            EXTRA_MOVES,

            EMPTY_VALUE,
            STARTING_FOCUS, RESISTANCE, RESISTANCE_PENETRATION,

            INITIATIVE,
            ATB_START_MOD, ATB_START_PRESET,

            BASE_DAMAGE,
            ARMOR_PENETRATION,

            EMPTY_VALUE,

            REFLEX,
            SPIRIT,
            GRIT,
            MIGHT,
            WIT,
            LUCK,

            EMPTY_VALUE,
            WEIGHT, CARRYING_CAPACITY,

            SIGHT_RANGE,
            SIDE_SIGHT_PENALTY, BEHIND_SIGHT_BONUS,

            // ENDURANCE_REGEN, ESSENCE_REGEN, FOCUS_REGEN,

            EMPTY_VALUE,
            CONCEALMENT, DETECTION,
            STEALTH, //
            QUICK_SLOTS,

    };

    public static final VALUE[][] unitVals = {

            A_BASE_UNIT_PARAMETERS,
            A_DERIVED_UNIT_PARAMETERS, ATTRIBUTES,
            ITEM_PROPERTIES, BASE_ATTRIBUTES,  HERO_PROPS, AV_VALUES,
            RESISTANCES,
            MASTERIES,
    };

}
