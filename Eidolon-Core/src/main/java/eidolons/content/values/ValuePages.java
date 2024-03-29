package eidolons.content.values;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;

import java.lang.reflect.Field;

import static eidolons.content.PARAMS.*;
import static main.content.values.parameters.G_PARAMS.DURATION;
import static main.content.values.parameters.G_PARAMS.RADIUS;
import static main.content.values.properties.G_PROPS.ACTIVES;
import static main.content.values.properties.G_PROPS.VERSION;

public class ValuePages {
    public static final VALUE[] DESCRIPTION = {G_PROPS.DESCRIPTION};
    public static final VALUE[] LORE = {G_PROPS.LORE};
    public static final VALUE[] GENERIC_AV_HEADER = {G_PROPS.NAME, G_PROPS.IMAGE, G_PROPS.GROUP,

            G_PROPS.WORKSPACE_GROUP, G_PROPS.DEV_NOTES, G_PROPS.UNIT_GROUP, G_PROPS.ASPECT, G_PROPS.DEITY,
            G_PROPS.PRINCIPLES, ACTIVES, G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES,
            LEVEL, POWER, FORMULA, G_PROPS.DESCRIPTION, G_PROPS.LORE,
            G_PROPS.FLAVOR,
    }; // POWER?
    public static final VALUE[] ARENA_HEADER = {
            G_PROPS.UNIQUE_ID,
            G_PROPS.NAME, VERSION,
            G_PROPS.IMAGE,
            G_PROPS.ASPECT,
            G_PROPS.PARENT_TYPE, G_PROPS.GROUP,
            G_PROPS.DEV_NOTES, G_PROPS.UNIT_GROUP,
            ACTIVES, G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES,
            LEVEL, POWER, FORMULA, G_PROPS.DESCRIPTION, G_PROPS.LORE,

    };
    public static final VALUE[] CHARS_HEADER = {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.BACKGROUND,
            G_PROPS.RACE, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,};
    public static final VALUE[] DC_SPELLS_HEADER = {G_PROPS.ASPECT, G_PROPS.SPELL_TYPE,
            G_PROPS.SPELL_GROUP, G_PROPS.TARGETING_MODE, G_PROPS.SPELL_TAGS,};
    public static final VALUE[] ANIM_VALUES = VisualEnums.anim_vals;

    public static final VALUE[] UNITS_HEADER = {G_PROPS.ASPECT, G_PROPS.DEITY,
            // G_PROPS.GROUP,
            G_PROPS.CLASSIFICATIONS, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,};
    public static final VALUE[] OUTLINE_VALUES = {
            // G_PROPS.DISPLAYED_NAME,
            // PROPS.VISIBILITY_STATUS,
            PROPS.HINTS, PROPS.LAST_SEEN,
            // OUTLINE_DESCRIPTION

    };
    public static final VALUE[] BF_OBJ_HEADER = {G_PROPS.BF_OBJECT_TYPE, // magical,
            // natural,
            // structure,
            // trap,
            // container,
            G_PROPS.BF_OBJECT_GROUP, // conjuration, plant,
            G_PROPS.BF_OBJECT_TAGS, // destructible, passible, summoned
            G_PROPS.STATUS, // can also be set ablaze!
            G_PROPS.STANDARD_PASSIVES, // non-obstructing, transparent,
            GOLD_TOTAL, PROPS.CONTAINER_CONTENTS, PROPS.CONTAINER_CONTENT_VALUE,};
    public static final VALUE[] GENERIC_DC_HEADER = {
            // G_PROPS.ASPECT,
            // LEVEL,
            // G_PROPS.PRINCIPLES,
            // G_PROPS.DESCRIPTION,
            // G_PROPS.PASSIVES,
            // G_PROPS.ACTIVES,
            // FORMULA,

            // G_PROPS.PASSIVES,
            // PROPS.SKILLS,
            // G_PROPS.STANDARD_PASSIVES,
            // PROPS.VISIBILITY_STATUS,
            // PROPS.DETECTION_STATUS,
            // PROPS.FACING_DIRECTION,
    };
    public static final VALUE[] DC_TRAILING_PAGE = {G_PROPS.DESCRIPTION, G_PROPS.LORE,

    };
    public static final VALUE[] AV_TRAILING_PAGE = {G_PROPS.DESCRIPTION, G_PROPS.LORE,

    };
    public static final PARAMETER[] BOSS_PARAMETERS = {
            GRID_WIDTH, GRID_HEIGHT,
            TOUGHNESS, ENDURANCE,
            ARMOR, ATTACK, DEFENSE, INITIATIVE,
            ATB_START_MOD, ATB_START_PRESET,
            BASE_DAMAGE,
            CONCEALMENT, DETECTION, SIGHT_RANGE,
            ENDURANCE_REGEN,
            WEIGHT,
    };
    public static final PARAMETER[] UNIT_PARAMETERS = {
            TOUGHNESS, ENDURANCE, ESSENCE, FOCUS,
            DAMAGE, OFF_HAND_DAMAGE,

            ARMOR, ATTACK, DEFENSE, INITIATIVE,
            STARTING_FOCUS, SPIRIT, RESISTANCE,
            ATB_START_MOD, ATB_START_PRESET,

            BASE_DAMAGE,

            MIN_DAMAGE, MAX_DAMAGE, OFF_HAND_MIN_DAMAGE,
            OFF_HAND_MAX_DAMAGE,

            WEIGHT, CARRYING_CAPACITY, C_CARRYING_WEIGHT, SIGHT_RANGE,
            ENDURANCE_REGEN, ESSENCE_REGEN, FOCUS_REGEN,
            ARMOR_PENETRATION,  CONCEALMENT, DETECTION,
            STEALTH,
            UNIT_LEVEL, //
            QUICK_SLOTS,
            TOUGHNESS_COST_MOD, ESSENCE_COST_MOD, ATB_COST_MOD,
            FOCUS_COST_MOD,};
    public static final PARAMETER[] UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT = {
            C_ATB,
            C_ENDURANCE,
            C_TOUGHNESS,
            C_FOCUS,
            C_ESSENCE,
    };
    public static final PARAMS[] UNIT_DYNAMIC_PARAMETERS_RESTORABLE = {
            C_FOCUS,
            C_ESSENCE,
    };
    public static final PARAMETER[] UNIT_DYNAMIC_PARAMETERS_CORE = {
            ENDURANCE,
            TOUGHNESS,
            FOCUS,
            ESSENCE,
    };
    public static final PARAMETER[] UNIT_DYNAMIC_PARAMETERS = {
            C_ATB, C_ENDURANCE, C_TOUGHNESS,
            C_FOCUS, C_ESSENCE,
            C_CARRYING_WEIGHT,

    };
    public static final VALUE[] CHAR_PROPERTIES = {
            PROPS.MASTERY_RANKS_1,
            PROPS.MASTERY_RANKS_2,
            PROPS.MASTERY_RANKS_3,
            PROPS.MASTERY_RANKS_4,
            PROPS.MASTERY_RANKS_5,

            PROPS.PERKS,
    };
    // INFO LEVELS
    public static final VALUE[] BOSS_VALUES = {
            G_PROPS.BOSS_TYPE,
            G_PROPS.BOSS_GROUP,
            ACTIVES,
            G_PROPS.STANDARD_PASSIVES,
            G_PROPS.ARMOR_ITEM,
            G_PROPS.MAIN_HAND_ITEM,
            G_PROPS.OFF_HAND_ITEM,
            PROPS.NATURAL_WEAPON,
            PROPS.OFFHAND_NATURAL_WEAPON,

            //width / height?
            //emblem
    };

    public static final VALUE[] UNIT_PROPERTIES = {G_PROPS.MAIN_HAND_ITEM, G_PROPS.OFF_HAND_ITEM,
            G_PROPS.RESERVE_MAIN_HAND_ITEM, G_PROPS.RESERVE_OFF_HAND_ITEM,
            G_PROPS.ARMOR_ITEM, PROPS.INVENTORY, PROPS.QUICK_ITEMS, PROPS.JEWELRY,
            PROPS.SKILLS, PROPS.CLASSES, PROPS.FIRST_CLASS, PROPS.SECOND_CLASS,

            PROPS.LEARNED_SPELLS,

    };
    public static final VALUE[] ATTRIBUTES_VAL = {STRENGTH, VITALITY,
            AGILITY, DEXTERITY, WILLPOWER, INTELLIGENCE, WISDOM,
            KNOWLEDGE, SPELLPOWER, CHARISMA,};
    public static final PARAMETER[] ATTRIBUTES = {STRENGTH, VITALITY,
            AGILITY, DEXTERITY, WILLPOWER, INTELLIGENCE, WISDOM,
            KNOWLEDGE, SPELLPOWER, CHARISMA,};
    public static final PARAMETER[] DEFAULT_ATTRIBUTES = {DEFAULT_STRENGTH,
            DEFAULT_VITALITY, DEFAULT_AGILITY, DEFAULT_DEXTERITY,
            DEFAULT_WILLPOWER, DEFAULT_INTELLIGENCE, DEFAULT_WISDOM,
            DEFAULT_KNOWLEDGE, DEFAULT_SPELLPOWER, DEFAULT_CHARISMA,};
    public static final PARAMETER[] BASE_ATTRIBUTES = {BASE_STRENGTH, BASE_VITALITY,
            BASE_AGILITY, BASE_DEXTERITY, BASE_WILLPOWER,
            BASE_INTELLIGENCE, BASE_WISDOM, BASE_KNOWLEDGE,
            BASE_SPELLPOWER, BASE_CHARISMA,};

    public static final PARAMETER[] MASTERIES_MAGIC = {
            WIZARDRY_MASTERY,
            SORCERY_MASTERY,
            SPELLCRAFT_MASTERY,
            ENCHANTMENT_MASTERY,
            DIVINATION_MASTERY,
            CONJURATION_MASTERY,

            AIR_MASTERY,
            CELESTIAL_MASTERY,
            WATER_MASTERY, BENEDICTION_MASTERY,
            FIRE_MASTERY, REDEMPTION_MASTERY,

            EARTH_MASTERY,
            DESTRUCTION_MASTERY,
            SYLVAN_MASTERY, DEMONOLOGY_MASTERY,
            SAVAGE_MASTERY, WARP_MASTERY,

            SHADOW_MASTERY, BLOOD_MAGIC_MASTERY, WITCHERY_MASTERY,
            AFFLICTION_MASTERY, PSYCHIC_MASTERY, NECROMANCY_MASTERY

    };
    public static final PARAMETER[] MASTERIES_MAGIC_DEFAULT_SORTED = {WIZARDRY_MASTERY,
            SPELLCRAFT_MASTERY, DIVINATION_MASTERY, WARCRY_MASTERY,

            SORCERY_MASTERY, CONJURATION_MASTERY, ENCHANTMENT_MASTERY,
            FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY,
            SYLVAN_MASTERY, SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY, WARP_MASTERY,
            CELESTIAL_MASTERY, BENEDICTION_MASTERY, REDEMPTION_MASTERY,
            SHADOW_MASTERY, WITCHERY_MASTERY, PSYCHIC_MASTERY,
            BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY, NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_SKILL_DISPLAY = {

            WIZARDRY_MASTERY, SPELLCRAFT_MASTERY, DIVINATION_MASTERY,
            WARCRY_MASTERY, ATHLETICS_MASTERY, MOBILITY_MASTERY,
            DISCIPLINE_MASTERY, MEDITATION_MASTERY, DEFENSE_MASTERY,
            ARMORER_MASTERY, SHIELD_MASTERY, TACTICS_MASTERY,
            LEADERSHIP_MASTERY, ITEM_MASTERY, DETECTION_MASTERY,
            STEALTH_MASTERY,

            BLADE_MASTERY, BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY, MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY,

            SORCERY_MASTERY, TRANSMUTATION_MASTERY, CONJURATION_MASTERY,
            SYLVAN_MASTERY, ENCHANTMENT_MASTERY, SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, CELESTIAL_MASTERY, DEMONOLOGY_MASTERY,
            BENEDICTION_MASTERY, WARP_MASTERY, REDEMPTION_MASTERY,
            SHADOW_MASTERY, BLOOD_MAGIC_MASTERY, WITCHERY_MASTERY,
            AFFLICTION_MASTERY, PSYCHIC_MASTERY, NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_MAGIC_DISPLAY = {
            FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY, SORCERY_MASTERY,
            TRANSMUTATION_MASTERY, CONJURATION_MASTERY, SYLVAN_MASTERY,
            ENCHANTMENT_MASTERY, SAVAGE_MASTERY, DESTRUCTION_MASTERY,
            CELESTIAL_MASTERY, DEMONOLOGY_MASTERY, BENEDICTION_MASTERY,
            WARP_MASTERY, REDEMPTION_MASTERY, SHADOW_MASTERY,
            BLOOD_MAGIC_MASTERY, WITCHERY_MASTERY, AFFLICTION_MASTERY,
            PSYCHIC_MASTERY, NECROMANCY_MASTERY,};
    public static final PARAMETER[] MASTERIES_WEAPONS_DISPLAY = {BLADE_MASTERY,
            BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY, MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY,};
    public static final PARAMETER[] MASTERIES_COMBAT_DISPLAY = {DEFENSE_MASTERY,
            ARMORER_MASTERY, WARCRY_MASTERY, TACTICS_MASTERY,
            LEADERSHIP_MASTERY,

            SHIELD_MASTERY, ATHLETICS_MASTERY, MOBILITY_MASTERY,};
    public static final PARAMETER[] MASTERIES_MISC_DISPLAY = {

            ITEM_MASTERY,

            DISCIPLINE_MASTERY, MEDITATION_MASTERY,

            DETECTION_MASTERY, STEALTH_MASTERY,

            WIZARDRY_MASTERY, SPELLCRAFT_MASTERY, DIVINATION_MASTERY,};
    public static final PARAMETER[] MASTERIES_COMBAT = {BLADE_MASTERY,
            BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY, MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY, DEFENSE_MASTERY, ARMORER_MASTERY,
            WARCRY_MASTERY,

            SHIELD_MASTERY,};
    public static final PARAMETER[] MASTERIES_MISC = {ATHLETICS_MASTERY,
            MOBILITY_MASTERY, ARMORER_MASTERY, ITEM_MASTERY,
            TACTICS_MASTERY, LEADERSHIP_MASTERY,

            DISCIPLINE_MASTERY,

            MEDITATION_MASTERY,
            DIVINATION_MASTERY, WARCRY_MASTERY,
            DETECTION_MASTERY, STEALTH_MASTERY,
            // ENCHANTER_MASTERY,
            // JEWELER_MASTERY,
    };
    public static final PARAMETER[] MASTERIES = {
            ATHLETICS_MASTERY,
            MOBILITY_MASTERY,
            BLADE_MASTERY,
            BLUNT_MASTERY,
            AXE_MASTERY,
            POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY,
            TWO_HANDED_MASTERY,
            UNARMED_MASTERY,
            MARKSMANSHIP_MASTERY,

            SHIELD_MASTERY,
            DEFENSE_MASTERY,
            ARMORER_MASTERY,
            ITEM_MASTERY,
            DISCIPLINE_MASTERY,
            // MARKSMANSHIP_MASTERY,
            // SHIELD_MASTERY,
            STEALTH_MASTERY, DETECTION_MASTERY,

            TACTICS_MASTERY, LEADERSHIP_MASTERY,

            WARCRY_MASTERY,
            DIVINATION_MASTERY,

            MEDITATION_MASTERY, WIZARDRY_MASTERY,
            SPELLCRAFT_MASTERY,
            SORCERY_MASTERY, CONJURATION_MASTERY,
            ENCHANTMENT_MASTERY,
            AIR_MASTERY,
            WATER_MASTERY,
            EARTH_MASTERY,
            FIRE_MASTERY,
            SYLVAN_MASTERY,
            SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY,
            WARP_MASTERY,
            CELESTIAL_MASTERY, BENEDICTION_MASTERY,
            REDEMPTION_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY,
            PSYCHIC_MASTERY, BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY,
            NECROMANCY_MASTERY,


    };
    // public static final VALUE[] MASTERIES_2 = {
    //
    // };
    public static final PARAMETER[] NATURAL_RESISTANCES = {SLASHING_RESISTANCE,
            PIERCING_RESISTANCE, BLUDGEONING_RESISTANCE, POISON_RESISTANCE,
            FIRE_RESISTANCE, COLD_RESISTANCE, ACID_RESISTANCE,
            LIGHTNING_RESISTANCE, SONIC_RESISTANCE, LIGHT_RESISTANCE,};

    public static final PARAMETER[] ELEMENTAL_RESISTANCES = {FIRE_RESISTANCE,
            COLD_RESISTANCE, ACID_RESISTANCE, LIGHTNING_RESISTANCE,
            SONIC_RESISTANCE, LIGHT_RESISTANCE,};
    public static final PARAMETER[] ASTRAL_RESISTANCES = {ARCANE_RESISTANCE, CHAOS_RESISTANCE, HOLY_RESISTANCE, SHADOW_RESISTANCE,
            PSIONIC_RESISTANCE, DEATH_RESISTANCE,};

    public static final PARAMETER[] ASTRAL_AND_ELEMENTAL_RESISTANCES = {
            ARCANE_RESISTANCE, CHAOS_RESISTANCE, HOLY_RESISTANCE,
            PSIONIC_RESISTANCE, DEATH_RESISTANCE, FIRE_RESISTANCE,
            COLD_RESISTANCE, ACID_RESISTANCE, LIGHTNING_RESISTANCE,
            SHADOW_RESISTANCE, SONIC_RESISTANCE, LIGHT_RESISTANCE,};

    public static final PARAMETER[] MAGIC_RESISTANCES = {DEATH_RESISTANCE,
            FIRE_RESISTANCE, CHAOS_RESISTANCE, ARCANE_RESISTANCE, HOLY_RESISTANCE, SHADOW_RESISTANCE, POISON_RESISTANCE,};
    public static final PARAMETER[] PHYSICAL_RESISTANCES = {PIERCING_RESISTANCE,
            BLUDGEONING_RESISTANCE, SLASHING_RESISTANCE,};

    public static final PARAMETER[] RESISTANCES = {
            FIRE_RESISTANCE, COLD_RESISTANCE, ACID_RESISTANCE,
            LIGHTNING_RESISTANCE, SONIC_RESISTANCE, LIGHT_RESISTANCE,

            CHAOS_RESISTANCE, ARCANE_RESISTANCE, HOLY_RESISTANCE, SHADOW_RESISTANCE, PSIONIC_RESISTANCE, DEATH_RESISTANCE,

            PIERCING_RESISTANCE,
            BLUDGEONING_RESISTANCE,
            SLASHING_RESISTANCE,
            POISON_RESISTANCE,

    };

    public static final PARAMETER[] ARMOR_VS_DAMAGE_TYPES = {

            FIRE_ARMOR, COLD_ARMOR, ACID_ARMOR,
            LIGHTNING_ARMOR, SONIC_ARMOR, LIGHT_ARMOR,

            CHAOS_ARMOR, ARCANE_ARMOR, HOLY_ARMOR,
            SHADOW_ARMOR, PSIONIC_ARMOR, DEATH_ARMOR,

            PIERCING_ARMOR,
            BLUDGEONING_ARMOR,
            SLASHING_ARMOR,

    };

    public static final VALUE[] UNIT_LEVEL_PARAMETERS = {PROPS.ATTRIBUTE_PROGRESSION,
            PROPS.MASTERY_PROGRESSION, PROPS.LVL_PLAN, PROPS.MAIN_HAND_REPERTOIRE,
            PROPS.OFF_HAND_REPERTOIRE, PROPS.ARMOR_REPERTOIRE, PROPS.JEWELRY_ITEM_TRAIT_REPERTOIRE,
            PROPS.QUICK_ITEM_REPERTOIRE, PROPS.JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE,
            PROPS.QUALITY_LEVEL_RANGE, PROPS.ALLOWED_MATERIAL, JEWELRY_GOLD_PERCENTAGE,
            QUICK_ITEM_GOLD_PERCENTAGE, ARMOR_GOLD_PERCENTAGE,
            MAIN_HAND_GOLD_PERCENTAGE, PROPS.MEMORIZATION_PRIORITY, PROPS.VERBATIM_PRIORITY,};
    public static final VALUE[] LEVEL_PARAMETERS = {HERO_LEVEL,
            GOLD, MASTERY_POINTS, ATTR_POINTS,
            ATTR_POINTS_PER_LEVEL, MASTERY_RANKS_PER_LEVEL,
            GOLD_MOD,
            // STRENGTH_PER_LEVEL,
            // VITALITY_PER_LEVEL,
            // AGILITY_PER_LEVEL,
            // DEXTERITY_PER_LEVEL,
            // WILLPOWER_PER_LEVEL,
            // INTELLIGENCE_PER_LEVEL,
            // WISDOM_PER_LEVEL,
            // KNOWLEDGE_PER_LEVEL,
            // SPELLPOWER_PER_LEVEL,
            // CHARISMA_PER_LEVEL,

    };
    public static final PARAMETER[] SNEAK_MODS = {SNEAK_DAMAGE_MOD,
            SNEAK_ATTACK_MOD,
            // SNEAK_DAMAGE_BONUS,
            // SNEAK_ATTACK_BONUS, SNEAK_DEFENSE_PENETRATION,
            // SNEAK_ARMOR_PENETRATION

    };
    // else?

    //
    public static final VALUE[] ACTION_PROPS = {
            FORMULA,
            PROPS.DAMAGE_TYPE,
            ACTIVES,
            G_PROPS.ACTION_TYPE,
            G_PROPS.ACTION_TYPE_GROUP,
            G_PROPS.ACTION_TAGS,
            PROPS.ROLL_TYPES_TO_SAVE, PROPS.TARGETING_MODIFIERS, G_PROPS.TARGETING_MODE,
            PROPS.EFFECTS_WRAP, G_PROPS.CUSTOM_SOUNDSET,};
    public static final VALUE[] ACTION_PROPS2 = {PROPS.RESISTANCE_MODIFIERS,
            PROPS.RESISTANCE_TYPE, PROPS.ON_ACTIVATE, PROPS.ON_HIT, PROPS.ON_KILL, PROPS.AI_LOGIC,
            PROPS.AI_PRIORITY_FORMULA,};
    public static final VALUE[] ACTION_PROPS_DC = {G_PROPS.ACTION_TYPE, G_PROPS.ACTION_TAGS,
            PROPS.DAMAGE_TYPE };
    public static final VALUE[] ACTION_PARAMS_DC = {ATTACK_MOD, DEFENSE_MOD,
            DAMAGE_MOD, DAMAGE_BONUS, ARMOR_PENETRATION,
            BLEEDING_MOD, COUNTER_MOD,              RANGE,

    };
    public static final VALUE[] ACTION_PARAMS_DC2 = {STR_DMG_MODIFIER,
            AGI_DMG_MODIFIER, INT_DMG_MODIFIER, SP_DMG_MODIFIER,
            CRITICAL_MOD,   RADIUS, AUTO_ATTACK_RANGE,
            SIDE_DAMAGE_MOD, DIAGONAL_DAMAGE_MOD, SIDE_ATTACK_MOD,
            DIAGONAL_ATTACK_MOD
            // G_DURATION

    };
    // attack/defense/damage/str/agi... mods, costs, damage type,
    // bleeding modifiers, bash on focus/initiative/stamina,
    // plus some roll effects like knockdown (mass, strength, reflex...).
    public static final VALUE[] ACTION_ATTACK_PARAMS = {ATTACK_MOD, DEFENSE_MOD,
            DAMAGE_MOD, DAMAGE_BONUS, ARMOR_PENETRATION,
            BLEEDING_MOD, COUNTER_MOD,  STR_DMG_MODIFIER,
            AGI_DMG_MODIFIER, INT_DMG_MODIFIER, SP_DMG_MODIFIER,
            DURABILITY_DAMAGE_MOD, CRITICAL_MOD, ACCURACY,
            AUTO_ATTACK_RANGE, SIDE_DAMAGE_MOD, DIAGONAL_DAMAGE_MOD,
            SIDE_ATTACK_MOD, DIAGONAL_ATTACK_MOD

    };
    public static final VALUE[] ACTION_PARAMS = {RADIUS, RANGE,
            DURATION};
    public static final VALUE[] SPELL_VALUES = {
            G_PROPS.VARIABLES,
            FORMULA,
            ACTIVES,
            CIRCLE, SPELL_DIFFICULTY,
            DURATION, RADIUS, RANGE,
            SPELLPOWER_MOD ,

    };
    public static final VALUE[] QUICK_ITEM_PARAMETERS = {CHARGES,
            AP_COST, TOU_COST, ESS_COST, FOC_COST, FOC_REQ,
            ENDURANCE_COST, DURATION, RANGE, RADIUS,
            // SPELLPOWER_BONUS,
            // SPELLPOWER_MOD,
    };
    public static final VALUE[] ITEM_PARAMETERS = {WEIGHT, GOLD_COST,

    };
    public static final VALUE[] ITEM_PROPERTIES = {  G_PROPS.ITEM_TYPE,            G_PROPS.ITEM_GROUP};
    public static final VALUE[] QUICK_ITEM_PROPERTIES = {

            G_PROPS.TARGETING_MODE, PROPS.EFFECTS_WRAP, PROPS.TARGETING_MODIFIERS, PROPS.DAMAGE_TYPE,
            G_PROPS.SOUNDSET, G_PROPS.CUSTOM_SOUNDSET, G_PROPS.IMPACT_SPRITE,};
    public static final VALUE[] DC_SPELL_PROPERTIES = {G_PROPS.SPELL_TYPE, G_PROPS.SPELL_GROUP,
            G_PROPS.SPELL_TAGS, PROPS.DAMAGE_TYPE,};
    public static final VALUE[] SPELL_PROPERTIES = {G_PROPS.SPELL_GROUP, G_PROPS.SPELL_TYPE,
            G_PROPS.SPELL_TAGS, G_PROPS.SPELL_SUBGROUP,

            G_PROPS.TARGETING_MODE, PROPS.EFFECTS_WRAP, PROPS.RETAIN_CONDITIONS,
            PROPS.TARGETING_MODIFIERS, PROPS.DAMAGE_TYPE, G_PROPS.SPELL_UPGRADE_GROUPS,

            G_PROPS.SOUNDSET, G_PROPS.CUSTOM_SOUNDSET, G_PROPS.IMPACT_SPRITE};
    public static final PARAMETER[] COSTS = {AP_COST, TOU_COST, ESS_COST,
            FOC_COST, FOC_REQ, ENDURANCE_COST,
            ATK_PTS_COST,
            MOVE_PTS_COST,

    };
    public static final VALUE[] SKILL_PARAMETERS = {CIRCLE,

    };
    public static final VALUE[] SKILL_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.SKILL_GROUP,
            G_PROPS.BASE_TYPE,

    };
    public static final VALUE[] SKILL_ADDITIONAL = {

            PROPS.ATTRIBUTE_BONUSES, PROPS.PARAMETER_BONUSES,

    };
    public static final VALUE[] CLASS_HEADER2 = {
    };
    public static final VALUE[] PERK_HEADER = {
            G_PROPS.PERK_GROUP,
            G_PROPS.PERK_CLASS_REQUIREMENTS,
            PROPS.PERK_FOR_CLASSES,
    };
    public static final VALUE[] CLASS_HEADER = {ACTIVES, PROPS.SKILL_OR_REQUIREMENTS,
            PROPS.SKILL_REQUIREMENTS, PROPS.REQUIREMENTS,

            G_PROPS.CLASS_TYPE, G_PROPS.CLASS_GROUP, PROPS.CLASS_PERK_GROUP,

            G_PROPS.BASE_TYPE,  PROPS.ALT_BASE_TYPES, PROPS.ATTRIBUTE_BONUSES,
            PROPS.PARAMETER_BONUSES, CIRCLE,

            PROPS.BASE_CLASSES_ONE, PROPS.BASE_CLASSES_TWO,

    };
    public static final VALUE[] REQUIREMENTS = {PROPS.SKILL_REQUIREMENTS,
            PROPS.SKILL_OR_REQUIREMENTS, PROPS.CLASSES,
            PROPS.REQUIREMENTS,

    };
    public static final PARAMETER[] PENALTIES_MAIN = {
            TOUGHNESS_COST_MOD, ATB_COST_MOD,
            FOCUS_COST_MOD, ESSENCE_COST_MOD,
    };
    public static final PARAMETER[] PENALTIES_MOVE = {
            MOVE_TOU_COST_MOD,
            MOVE_ATB_COST_MOD,
    };
    public static final PARAMETER[] PENALTIES_ATK = {
            ATTACK_TOUGHNESS_COST_MOD,
            ATTACK_ATB_COST_MOD,
    };
    public static final PARAMETER[] PENALTIES_SPELL = {
            SPELL_ESS_COST_MOD,
            SPELL_FOC_COST_MOD, SPELL_ATB_COST_MOD,
    };
    public static final PARAMETER[] PENALTIES = {TOUGHNESS_COST_MOD, ATB_COST_MOD,
            FOCUS_COST_MOD, ESSENCE_COST_MOD, ATTACK_TOUGHNESS_COST_MOD,
            ATTACK_ATB_COST_MOD, SPELL_ESS_COST_MOD,
            SPELL_FOC_COST_MOD, SPELL_ATB_COST_MOD, MOVE_TOU_COST_MOD,
            MOVE_ATB_COST_MOD,

    };
    public static final VALUE[] ARMOR_PARAMETERS = {MATERIAL_QUANTITY, ARMOR_LAYERS,
            COVER_PERCENTAGE, HARDNESS, ARMOR_MODIFIER,
            DURABILITY_MODIFIER, COST_MODIFIER, ATTACK_MOD,
            DEFENSE_MOD, DEFENSE_BONUS, C_DURABILITY, DURABILITY, WEIGHT,
            GOLD_COST,

    };


    public static final VALUE[] WEAPON_PARAMETERS = {
            MATERIAL_QUANTITY,
            HARDNESS, DICE, DIE_SIZE, BASE_DAMAGE_MODIFIER, DURABILITY_MODIFIER,
            STR_DMG_MODIFIER, AGI_DMG_MODIFIER, SP_DMG_MODIFIER,
            INT_DMG_MODIFIER,

            COST_MODIFIER,  ATTACK_MOD, DAMAGE_MOD,
            DEFENSE_MOD, ARMOR_PENETRATION, ATTACK_BONUS, DAMAGE_BONUS,
            DEFENSE_BONUS, C_DURABILITY, DURABILITY, WEIGHT, GOLD_COST,

    };
    public static final VALUE[] JEWELRY_PROPERTIES = {G_PROPS.MATERIAL, G_PROPS.JEWELRY_GROUP,
            PROPS.MAGICAL_ITEM_TRAIT, PROPS.MAGICAL_ITEM_LEVEL,
            G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES};
    public static final VALUE[][] JEWELRY_PAGES = {DESCRIPTION, JEWELRY_PROPERTIES, ATTRIBUTES,};
    public static final VALUE[] ARMOR_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.ARMOR_TYPE,
            G_PROPS.ARMOR_GROUP, G_PROPS.ITEM_MATERIAL_GROUP, G_PROPS.MATERIAL,};
    public static final VALUE[] WEAPON_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.WEAPON_TYPE,
            G_PROPS.WEAPON_GROUP, G_PROPS.WEAPON_CLASS, G_PROPS.WEAPON_SIZE,
            G_PROPS.ITEM_MATERIAL_GROUP, PROPS.DAMAGE_TYPE, G_PROPS.MATERIAL,
            G_PROPS.STANDARD_PASSIVES, PROPS.WEAPON_ATTACKS,

    };
    public static final VALUE[][] SKILL_PAGES = {SKILL_PROPERTIES, SKILL_PARAMETERS, REQUIREMENTS,
            SKILL_ADDITIONAL, ATTRIBUTES, RESISTANCES,};
    public static final VALUE[][] ACTION_PAGES = {ACTION_PROPS, ACTION_ATTACK_PARAMS,
            ACTION_PARAMS, COSTS, SNEAK_MODS, ACTION_PROPS2,};
    public static final VALUE[][] ACTION_PAGES_DC = {DESCRIPTION, COSTS, ACTION_PROPS_DC,
            ACTION_PARAMS_DC, ACTION_PARAMS_DC2};
    public static final VALUE[][] ARMOR_PAGES = {ARMOR_PROPERTIES, ARMOR_PARAMETERS, PENALTIES};
    public static final VALUE[][] ALT_QUICK_ITEM_PAGES = {DESCRIPTION, QUICK_ITEM_PARAMETERS,
            COSTS};
    public static final VALUE[][] QUICK_ITEM_PAGES = {QUICK_ITEM_PROPERTIES, ITEM_PROPERTIES,
            QUICK_ITEM_PARAMETERS, ITEM_PARAMETERS, COSTS};
    public static final VALUE[][] WEAPON_PAGES = {WEAPON_PROPERTIES, WEAPON_PARAMETERS, PENALTIES};
    public static final VALUE[][] ALT_CHAR_PAGES = {CHARS_HEADER, ATTRIBUTES, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES, LEVEL_PARAMETERS};
    public static final VALUE[][] ALT_UNIT_PAGES = {UNITS_HEADER, ATTRIBUTES, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES, DESCRIPTION};
    public static final VALUE[][] ALT_BF_OBJ_PAGES = {BF_OBJ_HEADER, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES,}; // DIFFERENT

    public static final VALUE[][] BOSS_PAGES = {
            BOSS_VALUES,
            BOSS_PARAMETERS,
            BASE_ATTRIBUTES,
            RESISTANCES,
    };

    public static final VALUE[][] UNIT_PAGES = {UNIT_PARAMETERS, UNIT_DYNAMIC_PARAMETERS,
            UNIT_PROPERTIES, ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, RESISTANCES,
            UNIT_LEVEL_PARAMETERS, LEVEL_PARAMETERS};
    public static final VALUE[][] CHAR_PAGES = {UNIT_PARAMETERS, UNIT_DYNAMIC_PARAMETERS,
            CHAR_PROPERTIES, UNIT_PROPERTIES, ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, RESISTANCES, CHAR_PROPERTIES, LEVEL_PARAMETERS,
    };
    public static final VALUE[][] PERK_PAGES = {
            PERK_HEADER,
    };
    public static final VALUE[][] CLASS_PAGES = {
            CLASS_HEADER, CLASS_HEADER2,
            // ATTRIBUTES_VAL,
            // MA
            UNIT_PARAMETERS,};
    public static final VALUE[][] SPELL_PAGES = {SPELL_VALUES, COSTS, SPELL_PROPERTIES,
            ANIM_VALUES,
    };
    // VALUE
    // ICONS?
    public static final PARAMETER[] BACKGROUND_PARAMS = {
            // TOUGHNESS,
            // ENDURANCE, STAMINA, ESSENCE,
            // STARTING_FOCUS, SPIRIT,
            WEIGHT, CARRYING_CAPACITY, SIGHT_RANGE,
            DETECTION, STEALTH,};
    public static final PARAMETER[] BACKGROUND_PARAMS_ADDITIONAL = {
            GOLD, ATTR_POINTS, MASTERY_POINTS,
            MASTERY_RANKS_PER_LEVEL, ATTR_POINTS_PER_LEVEL,
            // MEMORIZATION_CAP,
            // XP_GAIN_MOD,
            // XP_LEVEL_MOD,
            // XP_COST_REDUCTION,
            // GOLD_COST_REDUCTION,

    };
    public static final PROPERTY[] BACKGROUND_PROPS = {G_PROPS.BACKGROUND,
            G_PROPS.RACE,
            G_PROPS.ASPECT,
            G_PROPS.DEITY, // allowed deities?
            G_PROPS.PRINCIPLES, G_PROPS.STANDARD_PASSIVES, PROPS.OFFHAND_NATURAL_WEAPON,
            PROPS.NATURAL_WEAPON, PROPS.MASTERY_GROUPS_MAGIC, PROPS.MASTERY_GROUPS_MISC,
            PROPS.MASTERY_GROUPS_WEAPONS, G_PROPS.SOUNDSET,};
    public static final VALUE[][] BACKGROUND_VALUES = {DESCRIPTION, BACKGROUND_PROPS,
            DEFAULT_ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, BACKGROUND_PARAMS, RESISTANCES,
            BACKGROUND_PARAMS_ADDITIONAL, LORE,  };
    // IN-GAME
    public static final String[] ALT_PAGE_NAMES = {
            "Units Header;Attributes;Natural Resistances;Astral Resistances"
            // + "Resistances;Masteries;" +
            // "Parameters;Properties;Additional"
            , // UNITS
            "Spell Header;Spell Parameters;Spell Properties;Upgrades", // SPELLS
            "Chars Header;Attributes;Natural Resistances;Astral Resistances;",
            "", // ABILS
            "Bf Obj Header;Natural Resistances;Astral Resistances;", // BF_OBJ
            "", // BUFFS
            "Description;Costs;Action Properties;Action Parameters;Action Parameters2;", // ACTION
            "Armor properties;Armor parameters;Penalties", // ARMOR
            "Weapon properties;Weapon parameters;Penalties", // WEAPONS
            "SKILL_PROPERTIES;SKILL_PARAMETERS;REQUIREMENTS;ATTRIBUTES;Resistances;UNIT_PARAMETERS", // SKILLS
            "Usable Item Properties;Usable Item Parameters;", "Description;", // garb
            "Garb Properties;Garb Parameters;", // g
            "Jewelry Properties;Jewelry Parameters;", // J
            "Header;Params;Props;" // CLASSES

    };
    // ARCANE VAULT
    // this system is stupid...
    public static final String[] PAGE_NAMES = {
            "Parameters;Dynamic Parameters;Properties;Attributes;Base Attributes;Masteries;Resistances;Additional;Additional2", // UNITS
            "Spell Properties;Spell Parameters;Costs;Upkeep;Upgrades", // SPELLS
            // this

            "Parameters;Dynamic Parameters;Properties;Attributes;Masteries;Resistances;Additional;Additional2", // CHARS

            "", // ABILS
            "", // BF_OBJ
            "", // BUFFS
            "Action Properties;Attack Parameters;Action Parameters;Costs;Sneak;Action Properties2;", // ACTIONS

            "Armor properties;Armor parameters", // ARMOR
            "Weapon properties;Weapon parameters", // WEAPONS
            "SKILL_PROPERTIES;SKILL_PARAMETERS;REQUIREMENTS;ATTRIBUTES;RESISTANCES;UNIT_PARAMETERS;Auto Test", // SKILLS
            "Usable Item Properties;Usable Item Parameters;Item Properties;Item Parameters;", // usables
            "Description;", // garb
            "Description;Jewelry Properties;Jewelry Parameters;", // j

            "Header;Params;Props;Auto Test" // CLASSES
            ,
            "Header;Params;Props;Auto Test" // perks

    };
    public static final PARAMETER[] MASTERIES_MAGIC_SCHOOLS = {

            PSYCHIC_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY,
            NECROMANCY_MASTERY, AFFLICTION_MASTERY, BLOOD_MAGIC_MASTERY,
            SAVAGE_MASTERY, SYLVAN_MASTERY,
            TRANSMUTATION_MASTERY,
            FIRE_MASTERY, AIR_MASTERY, WATER_MASTERY, EARTH_MASTERY,

            CONJURATION_MASTERY, SORCERY_MASTERY, ENCHANTMENT_MASTERY,
            REDEMPTION_MASTERY, CELESTIAL_MASTERY, BENEDICTION_MASTERY,
            WARP_MASTERY, DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY,};


    private static final VALUE[] DC_SPELL_PARAMETERS = {

            AP_COST, TOU_COST, ESS_COST, ENDURANCE_COST, FOC_COST,
            FOC_REQ, RANGE, RADIUS, SPELLPOWER_MOD,
            DURATION,

    };
    public static final VALUE[][] ALT_SPELL_PAGES = {DESCRIPTION, DC_SPELL_PARAMETERS,
            DC_SPELLS_HEADER };

    public static VALUE[] ARMOR_GRADES;

    static {
        // TODO revamp value_page usage?
        // List<MultiParameter> armorGradeMultiParams =
        // DC_ContentManager.getArmorGradeMultiParams();
        // ARMOR_GRADES = armorGradeMultitoArray(new
        // VALUE[armorGradeMultisize()]);
    }

    static {
        for (Field f : ValuePages.class.getFields()) {
            if (f.getType().isArray()) {

            }
        }
    }

    public enum PAGE_NAMES {
        ARENA_HEADER,
        HEADER,
        PARAMETERS,
        PROPERTIES,
        MASTERIES,
        ATTRIBUTES,
        BASE_ATTRIBUTES,
        RESISTANCES,
        DYNAMIC_PARAMETERS,
        COSTS,
        ADDITIONAL,
        DEBUG,
        AV_HEADER,
        AV_BOTTOM,
        AV_TRAILING_PAGE,
        DC_TRAILING_PAGE,
        ;
    }

}
