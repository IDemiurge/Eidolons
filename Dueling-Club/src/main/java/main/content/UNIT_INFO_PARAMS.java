package main.content;

import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;

import static main.content.PARAMS.*;

public interface UNIT_INFO_PARAMS {

    PARAMS[] WEAPON_DC_INFO_PARAMS = {
            DAMAGE_BONUS, DAMAGE_MOD,
            DICE, DIE_SIZE,
            ATTACK_BONUS, ATTACK_MOD,
            DEFENSE_BONUS, DEFENSE_MOD,
            C_DURABILITY, HARDNESS,
            STR_DMG_MODIFIER, AGI_DMG_MODIFIER,
            SP_DMG_MODIFIER, INT_DMG_MODIFIER,
            IMPACT_AREA, WEIGHT,
            ARMOR_MOD, ARMOR_PENETRATION,
            DURABILITY_DAMAGE_MOD, DURABILITY_SELF_DAMAGE_MOD,
    };

    //                                   <><><><><>

    PARAMS[][] UNIT_INFO_PARAMS_GENERAL = {
            {
                    INITIATIVE_MODIFIER, INITIATIVE,
                    STARTING_FOCUS,
//      BASE_MORALE,
                    ENDURANCE_REGEN, STAMINA_REGEN,
                    TOUGHNESS_RECOVERY,
//      UNCONSCIOUS_THRESHOLD
            },
            {
                    SIGHT_RANGE, SIDE_SIGHT_PENALTY,
                    HEIGHT, GIRTH,
                    WEIGHT, CARRYING_CAPACITY,
                    ILLUMINATION, CONCEALMENT
            },
            {
                    FOCUS_RETAINMENT, FOCUS_RESTORATION,
                    MORALE_RETAINMENT, MORALE_RESTORATION,
                    INTERRUPT_DAMAGE, FORCE_PROTECTION

//     FATIGUE, CONFUSION,
//      FEAR_RESISTANCE, WOUNDS_RESISTANCE,
//      BASH_RESISTANCE,
            },

    };
    PARAMS[][] UNIT_INFO_PARAMS_COMBAT = {
            {
                    ATTACK_AP_PENALTY, ATTACK_STA_PENALTY,
                    MOVE_AP_PENALTY, MOVE_STA_PENALTY,

                    DIAGONAL_ATTACK_MOD, DIAGONAL_DAMAGE_MOD,
                    SIDE_ATTACK_MOD, SIDE_DAMAGE_MOD,
            },
            {
                    CRITICAL_MOD, AUTO_CRIT_CHANCE,
                    CLOSE_QUARTERS_ATTACK_MOD, CLOSE_QUARTERS_DAMAGE_MOD,
                    LONG_REACH_ATTACK_MOD, LONG_REACH_DAMAGE_MOD,
                    COUNTER_MOD, LONG_REACH_DAMAGE_MOD,
            },
            {
                    ARMOR_PENETRATION, BLOCK_PENETRATION,
                    DEFENSE_PENETRATION, PARRY_PENETRATION,
                    ACCURACY, EVASION,
                    BLOCK_CHANCE, PARRY_CHANCE,


            },
    };
    PARAMS[][] UNIT_INFO_PARAMS_MISC = {
            {
                    QUICK_SLOTS, ITEM_COST_MOD,
                    DETECTION, STEALTH,
                    SNEAK_DEFENSE_PENETRATION, SNEAK_ARMOR_PENETRATION,
                    SNEAK_ATTACK_MOD, SNEAK_DAMAGE_MOD,
            },
            {
                    CADENCE_AP_MOD, CADENCE_ATTACK_MOD,
                    CADENCE_BONUS, CADENCE_DAMAGE_MOD,
                    CADENCE_DEFENSE_MOD, CADENCE_FOCUS_BOOST,
                    CADENCE_STA_MOD, CADENCE_RETAINMENT_CHANCE,
            },
            {
                    WATCH_DEFENSE_MOD, WATCH_ATTACK_MOD,
                    WATCH_AP_PENALTY_MOD, WATCH_DETECTION_MOD,
                    WATCH_ATTACK_OTHERS_MOD, WATCH_DEFENSE_OTHERS_MOD,
                    WATCHED_ATTACK_MOD,
            },

    };

    PARAMS[][] UNIT_INFO_PARAMS_MAGIC = {
            {
                    SPELL_ESS_PENALTY,
                    SPELL_STA_PENALTY,
                    SPELL_AP_PENALTY,
                    SPELL_FOC_PENALTY,

                    RESISTANCE_PENETRATION,
                    ESSENCE_REGEN,
                    MEMORIZATION_CAP,
                    MEMORY_REMAINING,
            },
            {
                    DIVINATION_CAP,
                    INTERRUPT_DAMAGE,
                    REST_BONUS,
                    MEDITATION_BONUS,
                    CONCENTRATION_BONUS,
                    REST_MOD,
                    MEDITATION_MOD,
                    CONCENTRATION_MOD,
            },
    };

    String[] INFO_TABLE_NAMES = {
            "Physical", "Combat", "Magic", "Misc", "Rolls", "Mods"
    };

    VALUE[][] UNIT_INFO_PARAMS_MODS = {
            {
                    COUNTER_MOD,
                    THROW_ATTACK_MOD,
                    THROW_DAMAGE_MOD,
                    COOLDOWN_MOD,
                    BLEEDING_MOD,
            },
            {
                    INSTANT_DAMAGE_MOD,
                    INSTANT_ATTACK_MOD,
                    INSTANT_DEFENSE_MOD,
                    AOO_DAMAGE_MOD,
                    AOO_ATTACK_MOD,
                    AOO_DEFENSE_MOD,
                    COUNTER_ATTACK_MOD,
                    COUNTER_DEFENSE_MOD,
            },
            {
                    FORCE_MOD,
                    FORCE_KNOCK_MOD, FORCE_PUSH_MOD
                    , FORCE_PROTECTION, FORCE_DAMAGE_MOD
                    , FORCE_SPELL_DAMAGE_MOD, FORCE_MOD_SOURCE_WEIGHT
                    , FORCE_SPELLPOWER_MOD
            },
            {
                    PASSAGE_ATTACK_MOD,
                    ENGAGEMENT_ATTACK_MOD,
                    FLIGHT_ATTACK_MOD,
                    STUMBLE_ATTACK_MOD,
                    PASSAGE_DEFENSE_MOD,
                    ENGAGEMENT_DEFENSE_MOD,
                    FLIGHT_DEFENSE_MOD,
                    STUMBLE_DEFENSE_MOD,

                    DISENGAGEMENT_ATTACK_MOD
                    ,
                    DISENGAGEMENT_DEFENSE_MOD,
            },
            {
                    STOP_DISENGAGEMENT_CHANCE_MOD,
                    PASS_DISENGAGEMENT_CHANCE_MOD,
                    STOP_ENGAGEMENT_CHANCE_MOD,
                    PASS_ENGAGEMENT_CHANCE_MOD,
                    STOP_FLIGHT_CHANCE_MOD,
                    PASS_FLIGHT_CHANCE_MOD,
                    STOP_PASSAGE_CHANCE_MOD,
                    PASS_PASSAGE_CHANCE_MOD,
            },
    };

    VALUE[][] UNIT_INFO_PARAMS_ROLLS = {
            {
                    REACTION_ROLL_SAVE_BONUS, REACTION_ROLL_BEAT_BONUS,
                    REFLEX_ROLL_SAVE_BONUS, REFLEX_ROLL_BEAT_BONUS,
                    DEFENSE_ROLL_SAVE_BONUS, DEFENSE_ROLL_BEAT_BONUS,
                    DISARM_ROLL_SAVE_BONUS, DISARM_ROLL_BEAT_BONUS,
            },
            {
                    FORTITUDE_ROLL_SAVE_BONUS, FORTITUDE_ROLL_BEAT_BONUS,
                    BODY_STRENGTH_ROLL_SAVE_BONUS, BODY_STRENGTH_ROLL_BEAT_BONUS,
                    MASS_ROLL_SAVE_BONUS, MASS_ROLL_BEAT_BONUS,
                    FORCE_ROLL_SAVE_BONUS, FORCE_ROLL_BEAT_BONUS,
            },
            {
                    MIND_AFFECTING_ROLL_SAVE_BONUS, MIND_AFFECTING_ROLL_BEAT_BONUS,
                    QUICK_WIT_ROLL_SAVE_BONUS, QUICK_WIT_ROLL_BEAT_BONUS,
                    FAITH_ROLL_SAVE_BONUS, FAITH_ROLL_BEAT_BONUS,
                    DETECTION_ROLL_SAVE_BONUS, DETECTION_ROLL_BEAT_BONUS,
            },
    };
    VALUE[][][] UNIT_INFO_PARAMS = {
            UNIT_INFO_PARAMS_GENERAL,
            UNIT_INFO_PARAMS_COMBAT,
            UNIT_INFO_PARAMS_MAGIC,
            UNIT_INFO_PARAMS_MISC,
            UNIT_INFO_PARAMS_ROLLS,
            UNIT_INFO_PARAMS_MODS,
    };

    //                                   <><><><><>


    VALUE[] ACTION_TOOLTIP_PARAMS_TABLE_HEADER = {
            G_PROPS.NAME,
            DAMAGE_BONUS,
            ATTACK,
    };

    VALUE[] ACTION_TOOLTIP_PARAMS_TABLE_ROW_NAMES = {
            BASE_DAMAGE,
            COUNTER_MOD,
            INSTANT_DAMAGE_MOD,
            AOO_DAMAGE_MOD,
    };

    VALUE[] ACTION_TOOLTIP_PARAMS_TABLE_TEXT_ROW_NAMES = {
            SIDE_DAMAGE_MOD,
            DIAGONAL_DAMAGE_MOD,
    };

    PARAMS[] ACTION_TOOLTIP_PARAMS_TABLE = {
            DAMAGE, ATTACK,
            COUNTER_MOD, COUNTER_ATTACK_MOD,
            INSTANT_DAMAGE_MOD, INSTANT_ATTACK_MOD,
            AOO_DAMAGE_MOD, AOO_ATTACK_MOD,
            SIDE_DAMAGE_MOD, SIDE_ATTACK_MOD,
            DIAGONAL_DAMAGE_MOD, DIAGONAL_ATTACK_MOD,
    };

    PARAMS[] ACTION_TOOLTIP_PARAMS_RANGE_ROW_NAMES = {
            CLOSE_QUARTERS_DAMAGE_MOD,
            DAMAGE_MOD,
            LONG_REACH_DAMAGE_MOD,
    };
    PARAMS[] ACTION_TOOLTIP_PARAMS_RANGE = {
            CLOSE_QUARTERS_DAMAGE_MOD, CLOSE_QUARTERS_ATTACK_MOD,
            DAMAGE_MOD, ATTACK_MOD,
            LONG_REACH_DAMAGE_MOD, LONG_REACH_ATTACK_MOD,
    };
    PARAMS[] ACTION_TOOLTIP_PARAMS_TEXT = {
            CRITICAL_MOD,
            ACCURACY,
            ARMOR_MOD,
            IMPACT_AREA,

            SNEAK_DEFENSE_MOD,
            BLEEDING_MOD,
            DURABILITY_DAMAGE_MOD,

            FORCE,
            FORCE_KNOCK_MOD,
            FORCE_PUSH_MOD,
            FORCE_DAMAGE_MOD,

    };
    PARAMETER[] ACTION_TOOLTIP_PARAMS_ATTR_DMG_MODS = {
            STR_DMG_MODIFIER,
            AGI_DMG_MODIFIER,
            INT_DMG_MODIFIER,
            SP_DMG_MODIFIER,
    };
    PARAMETER[] ACTION_TOOLTIP_PARAMS_COSTS = {
            AP_COST,
            CP_COST,
            STA_COST,
            FOC_REQ,
            FOC_COST,
    };

}
