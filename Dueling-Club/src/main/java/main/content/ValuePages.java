package main.content;

import main.content.parameters.G_PARAMS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;

import java.lang.reflect.Field;

public class ValuePages {
    public static final VALUE[] DESCRIPTION = {G_PROPS.DESCRIPTION};
    public static final VALUE[] LORE = {G_PROPS.LORE};
    public static final VALUE[] GENERIC_AV_HEADER = {G_PROPS.NAME, G_PROPS.IMAGE, G_PROPS.GROUP,

            G_PROPS.WORKSPACE_GROUP, G_PROPS.DEV_NOTES, G_PROPS.UNIT_GROUP, G_PROPS.ASPECT, G_PROPS.DEITY,
            G_PROPS.PRINCIPLES, G_PROPS.ACTIVES, G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES,
            PARAMS.LEVEL, PARAMS.POWER, PARAMS.FORMULA, G_PROPS.DESCRIPTION, G_PROPS.LORE,
            G_PROPS.FLAVOR,

    }; // POWER?
    public static final VALUE[] CHARS_HEADER = {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.BACKGROUND,
            G_PROPS.RACE, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,};
    public static final VALUE[] DC_SPELLS_HEADER = {G_PROPS.ASPECT, G_PROPS.SPELL_TYPE,
            G_PROPS.SPELL_GROUP, G_PROPS.TARGETING_MODE, G_PROPS.SPELL_TAGS,};
    public static final VALUE[] SPELLS_UPGRADES = {PROPS.SPELL_UPGRADES,};
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
    };
    public static final VALUE[] GENERIC_DC_HEADER = {
            // G_PROPS.ASPECT,
            // PARAMS.LEVEL,
            // G_PROPS.PRINCIPLES,
            // G_PROPS.DESCRIPTION,
            // G_PROPS.PASSIVES,
            // G_PROPS.ACTIVES,
            // PARAMS.FORMULA,

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
    public static final PARAMETER[] UNIT_PARAMETERS = {
            // ACTIONS
            PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.STAMINA, PARAMS.ESSENCE, PARAMS.FOCUS,
            PARAMS.MORALE,

            PARAMS.DAMAGE, PARAMS.OFF_HAND_DAMAGE,

            PARAMS.ARMOR, PARAMS.ATTACK, PARAMS.DEFENSE, PARAMS.N_OF_ACTIONS, PARAMS.N_OF_COUNTERS,
            PARAMS.STARTING_FOCUS, PARAMS.SPIRIT, PARAMS.RESISTANCE, PARAMS.SPELL_ARMOR,
            PARAMS.INITIATIVE_MODIFIER, PARAMS.INITIATIVE_BONUS, PARAMS.C_INITIATIVE_BONUS,
            PARAMS.INITIATIVE, PARAMS.C_INITIATIVE,

            PARAMS.BASE_DAMAGE,

            PARAMS.MIN_DAMAGE, PARAMS.MAX_DAMAGE, PARAMS.OFF_HAND_MIN_DAMAGE,
            PARAMS.OFF_HAND_MAX_DAMAGE,

            PARAMS.WEIGHT, PARAMS.CARRYING_CAPACITY, PARAMS.C_CARRYING_WEIGHT, PARAMS.SIGHT_RANGE,
            PARAMS.SIDE_SIGHT_PENALTY, PARAMS.BEHIND_SIGHT_BONUS, PARAMS.STAMINA_REGEN,
            PARAMS.ENDURANCE_REGEN, PARAMS.ESSENCE_REGEN, PARAMS.FOCUS_REGEN,
            PARAMS.ARMOR_PENETRATION, PARAMS.ARMOR_MOD, PARAMS.CONCEALMENT, PARAMS.DETECTION,
            PARAMS.STEALTH,
            PARAMS.UNIT_LEVEL, //
            PARAMS.QUICK_SLOTS, // HEROLT
            PARAMS.STAMINA_PENALTY, PARAMS.ESSENCE_PENALTY, PARAMS.AP_PENALTY,
            PARAMS.FOCUS_PENALTY,};
    public static final VALUE[] UNIT_DYNAMIC_PARAMETERS = {PARAMS.C_N_OF_ACTIONS,
            PARAMS.C_INITIATIVE, PARAMS.C_ENDURANCE, PARAMS.C_TOUGHNESS, PARAMS.C_STAMINA,
            PARAMS.C_FOCUS, PARAMS.C_ESSENCE, PARAMS.C_MORALE, PARAMS.C_INITIATIVE_BONUS,
            PARAMS.C_CARRYING_WEIGHT,

    };
    // INFO LEVELS
    public static final VALUE[] UNIT_PROPERTIES = {G_PROPS.MAIN_HAND_ITEM, G_PROPS.OFF_HAND_ITEM,
            G_PROPS.ARMOR_ITEM, PROPS.INVENTORY, PROPS.QUICK_ITEMS, PROPS.JEWELRY,
            // PARAMS.QUICK_SLOTS_REMAINING,
            PROPS.SKILLS, PROPS.CLASSES, PROPS.FIRST_CLASS, PROPS.SECOND_CLASS,

            PROPS.KNOWN_SPELLS, PROPS.LEARNED_SPELLS, PROPS.MEMORIZED_SPELLS,
            PROPS.VERBATIM_SPELLS,

    };
    public static final PARAMETER[] UPKEEP_PARAMETERS = {PARAMS.ESS_UPKEEP, PARAMS.AP_UPKEEP,
            PARAMS.END_UPKEEP, PARAMS.FOC_UPKEEP, PARAMS.STA_UPKEEP,};
    public static final VALUE[] ATTRIBUTES_VAL = {PARAMS.STRENGTH, PARAMS.VITALITY,
            PARAMS.AGILITY, PARAMS.DEXTERITY, PARAMS.WILLPOWER, PARAMS.INTELLIGENCE, PARAMS.WISDOM,
            PARAMS.KNOWLEDGE, PARAMS.SPELLPOWER, PARAMS.CHARISMA,};
    public static final PARAMETER[] ATTRIBUTES = {PARAMS.STRENGTH, PARAMS.VITALITY,
            PARAMS.AGILITY, PARAMS.DEXTERITY, PARAMS.WILLPOWER, PARAMS.INTELLIGENCE, PARAMS.WISDOM,
            PARAMS.KNOWLEDGE, PARAMS.SPELLPOWER, PARAMS.CHARISMA,};
    public static final PARAMETER[] DEFAULT_ATTRIBUTES = {PARAMS.DEFAULT_STRENGTH,
            PARAMS.DEFAULT_VITALITY, PARAMS.DEFAULT_AGILITY, PARAMS.DEFAULT_DEXTERITY,
            PARAMS.DEFAULT_WILLPOWER, PARAMS.DEFAULT_INTELLIGENCE, PARAMS.DEFAULT_WISDOM,
            PARAMS.DEFAULT_KNOWLEDGE, PARAMS.DEFAULT_SPELLPOWER, PARAMS.DEFAULT_CHARISMA,};
    public static final PARAMETER[] BASE_ATTRIBUTES = {PARAMS.BASE_STRENGTH, PARAMS.BASE_VITALITY,
            PARAMS.BASE_AGILITY, PARAMS.BASE_DEXTERITY, PARAMS.BASE_WILLPOWER,
            PARAMS.BASE_INTELLIGENCE, PARAMS.BASE_WISDOM, PARAMS.BASE_KNOWLEDGE,
            PARAMS.BASE_SPELLPOWER, PARAMS.BASE_CHARISMA,};
    public static final PARAMETER[] MASTERIES_MAGIC = {PARAMS.WIZARDRY_MASTERY,
            PARAMS.SPELLCRAFT_MASTERY, PARAMS.DIVINATION_MASTERY, PARAMS.WARCRY_MASTERY,

            PARAMS.SORCERY_MASTERY,
            PARAMS.AIR_MASTERY,
            PARAMS.WATER_MASTERY,
            PARAMS.EARTH_MASTERY,
            PARAMS.FIRE_MASTERY,
            PARAMS.CONJURATION_MASTERY,
            PARAMS.SYLVAN_MASTERY, PARAMS.ENCHANTMENT_MASTERY, PARAMS.SAVAGE_MASTERY,
            PARAMS.DESTRUCTION_MASTERY, PARAMS.CELESTIAL_MASTERY, PARAMS.DEMONOLOGY_MASTERY,
            PARAMS.BENEDICTION_MASTERY, PARAMS.WARP_MASTERY, PARAMS.REDEMPTION_MASTERY,
            PARAMS.SHADOW_MASTERY, PARAMS.BLOOD_MAGIC_MASTERY, PARAMS.WITCHERY_MASTERY,
            PARAMS.AFFLICTION_MASTERY, PARAMS.PSYCHIC_MASTERY, PARAMS.NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_MAGIC_DEFAULT_SORTED = {PARAMS.WIZARDRY_MASTERY,
            PARAMS.SPELLCRAFT_MASTERY, PARAMS.DIVINATION_MASTERY, PARAMS.WARCRY_MASTERY,

            PARAMS.SORCERY_MASTERY, PARAMS.CONJURATION_MASTERY, PARAMS.ENCHANTMENT_MASTERY,
            PARAMS.ELEMENTAL_MASTERY, PARAMS.SYLVAN_MASTERY, PARAMS.SAVAGE_MASTERY,
            PARAMS.DESTRUCTION_MASTERY, PARAMS.DEMONOLOGY_MASTERY, PARAMS.WARP_MASTERY,
            PARAMS.CELESTIAL_MASTERY, PARAMS.BENEDICTION_MASTERY, PARAMS.REDEMPTION_MASTERY,
            PARAMS.SHADOW_MASTERY, PARAMS.WITCHERY_MASTERY, PARAMS.PSYCHIC_MASTERY,
            PARAMS.BLOOD_MAGIC_MASTERY, PARAMS.AFFLICTION_MASTERY, PARAMS.NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_SKILL_DISPLAY = {

            PARAMS.WIZARDRY_MASTERY, PARAMS.SPELLCRAFT_MASTERY, PARAMS.DIVINATION_MASTERY,
            PARAMS.WARCRY_MASTERY, PARAMS.ATHLETICS_MASTERY, PARAMS.MOBILITY_MASTERY,
            PARAMS.DISCIPLINE_MASTERY, PARAMS.MEDITATION_MASTERY, PARAMS.DEFENSE_MASTERY,
            PARAMS.ARMORER_MASTERY, PARAMS.SHIELD_MASTERY, PARAMS.TACTICS_MASTERY,
            PARAMS.LEADERSHIP_MASTERY, PARAMS.ITEM_MASTERY, PARAMS.DETECTION_MASTERY,
            PARAMS.STEALTH_MASTERY,

            PARAMS.BLADE_MASTERY, PARAMS.BLUNT_MASTERY, PARAMS.AXE_MASTERY, PARAMS.POLEARM_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY, PARAMS.TWO_HANDED_MASTERY, PARAMS.MARKSMANSHIP_MASTERY,
            PARAMS.UNARMED_MASTERY,

            PARAMS.SORCERY_MASTERY, PARAMS.ELEMENTAL_MASTERY, PARAMS.CONJURATION_MASTERY,
            PARAMS.SYLVAN_MASTERY, PARAMS.ENCHANTMENT_MASTERY, PARAMS.SAVAGE_MASTERY,
            PARAMS.DESTRUCTION_MASTERY, PARAMS.CELESTIAL_MASTERY, PARAMS.DEMONOLOGY_MASTERY,
            PARAMS.BENEDICTION_MASTERY, PARAMS.WARP_MASTERY, PARAMS.REDEMPTION_MASTERY,
            PARAMS.SHADOW_MASTERY, PARAMS.BLOOD_MAGIC_MASTERY, PARAMS.WITCHERY_MASTERY,
            PARAMS.AFFLICTION_MASTERY, PARAMS.PSYCHIC_MASTERY, PARAMS.NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_MAGIC_DISPLAY = {PARAMS.SORCERY_MASTERY,
            PARAMS.ELEMENTAL_MASTERY, PARAMS.CONJURATION_MASTERY, PARAMS.SYLVAN_MASTERY,
            PARAMS.ENCHANTMENT_MASTERY, PARAMS.SAVAGE_MASTERY, PARAMS.DESTRUCTION_MASTERY,
            PARAMS.CELESTIAL_MASTERY, PARAMS.DEMONOLOGY_MASTERY, PARAMS.BENEDICTION_MASTERY,
            PARAMS.WARP_MASTERY, PARAMS.REDEMPTION_MASTERY, PARAMS.SHADOW_MASTERY,
            PARAMS.BLOOD_MAGIC_MASTERY, PARAMS.WITCHERY_MASTERY, PARAMS.AFFLICTION_MASTERY,
            PARAMS.PSYCHIC_MASTERY, PARAMS.NECROMANCY_MASTERY,};
    public static final PARAMETER[] MASTERIES_WEAPONS_DISPLAY = {PARAMS.BLADE_MASTERY,
            PARAMS.BLUNT_MASTERY, PARAMS.AXE_MASTERY, PARAMS.POLEARM_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY, PARAMS.TWO_HANDED_MASTERY, PARAMS.MARKSMANSHIP_MASTERY,
            PARAMS.UNARMED_MASTERY,};
    public static final PARAMETER[] MASTERIES_COMBAT_DISPLAY = {PARAMS.DEFENSE_MASTERY,
            PARAMS.ARMORER_MASTERY, PARAMS.WARCRY_MASTERY, PARAMS.TACTICS_MASTERY,
            PARAMS.LEADERSHIP_MASTERY,

            PARAMS.SHIELD_MASTERY, PARAMS.ATHLETICS_MASTERY, PARAMS.MOBILITY_MASTERY,};
    public static final PARAMETER[] MASTERIES_MISC_DISPLAY = {

            PARAMS.ITEM_MASTERY,

            PARAMS.DISCIPLINE_MASTERY, PARAMS.MEDITATION_MASTERY,

            PARAMS.DETECTION_MASTERY, PARAMS.STEALTH_MASTERY,

            PARAMS.WIZARDRY_MASTERY, PARAMS.SPELLCRAFT_MASTERY, PARAMS.DIVINATION_MASTERY,};
    public static final PARAMETER[] MASTERIES_COMBAT = {PARAMS.BLADE_MASTERY,
            PARAMS.BLUNT_MASTERY, PARAMS.AXE_MASTERY, PARAMS.POLEARM_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY, PARAMS.TWO_HANDED_MASTERY, PARAMS.MARKSMANSHIP_MASTERY,
            PARAMS.UNARMED_MASTERY, PARAMS.DEFENSE_MASTERY, PARAMS.ARMORER_MASTERY,
            PARAMS.WARCRY_MASTERY,

            PARAMS.SHIELD_MASTERY,};
    public static final PARAMETER[] MASTERIES_MISC = {PARAMS.ATHLETICS_MASTERY,
            PARAMS.MOBILITY_MASTERY, PARAMS.ARMORER_MASTERY, PARAMS.ITEM_MASTERY,
            PARAMS.TACTICS_MASTERY, PARAMS.LEADERSHIP_MASTERY,

            PARAMS.DISCIPLINE_MASTERY,

            PARAMS.MEDITATION_MASTERY, PARAMS.DETECTION_MASTERY, PARAMS.STEALTH_MASTERY,
            // PARAMS.ENCHANTER_MASTERY,
            // PARAMS.JEWELER_MASTERY,
    };
    public static final PARAMETER[] MASTERIES = {
            PARAMS.ATHLETICS_MASTERY,
            PARAMS.MOBILITY_MASTERY,
            PARAMS.BLADE_MASTERY,
            PARAMS.BLUNT_MASTERY,
            PARAMS.AXE_MASTERY,
            PARAMS.POLEARM_MASTERY,
            PARAMS.DUAL_WIELDING_MASTERY,
            PARAMS.TWO_HANDED_MASTERY,
            PARAMS.UNARMED_MASTERY,
            PARAMS.MARKSMANSHIP_MASTERY,

            PARAMS.SHIELD_MASTERY,
            PARAMS.DEFENSE_MASTERY,
            PARAMS.ARMORER_MASTERY,
            PARAMS.ITEM_MASTERY,
            PARAMS.DISCIPLINE_MASTERY,
            // PARAMS.MARKSMANSHIP_MASTERY,
            // PARAMS.SHIELD_MASTERY,
            PARAMS.STEALTH_MASTERY, PARAMS.DETECTION_MASTERY,

            PARAMS.TACTICS_MASTERY, PARAMS.LEADERSHIP_MASTERY,

            PARAMS.WARCRY_MASTERY,
            PARAMS.DIVINATION_MASTERY,

            PARAMS.MEDITATION_MASTERY,  PARAMS.WIZARDRY_MASTERY,
            PARAMS.SPELLCRAFT_MASTERY,
            PARAMS.SORCERY_MASTERY, PARAMS.CONJURATION_MASTERY,
            PARAMS.ENCHANTMENT_MASTERY,
            PARAMS.AIR_MASTERY,
            PARAMS.WATER_MASTERY,
            PARAMS.EARTH_MASTERY,
            PARAMS.FIRE_MASTERY,
            PARAMS.SYLVAN_MASTERY,
            PARAMS.SAVAGE_MASTERY,
            PARAMS.DESTRUCTION_MASTERY, PARAMS.DEMONOLOGY_MASTERY,
            PARAMS.WARP_MASTERY,
            PARAMS.CELESTIAL_MASTERY, PARAMS.BENEDICTION_MASTERY,
            PARAMS.REDEMPTION_MASTERY, PARAMS.SHADOW_MASTERY, PARAMS.WITCHERY_MASTERY,
            PARAMS.PSYCHIC_MASTERY, PARAMS.BLOOD_MAGIC_MASTERY, PARAMS.AFFLICTION_MASTERY,
            PARAMS.NECROMANCY_MASTERY,


    };
    // public static final VALUE[] MASTERIES_2 = {
    //
    // };
    public static final PARAMETER[] NATURAL_RESISTANCES = {PARAMS.SLASHING_RESISTANCE,
            PARAMS.PIERCING_RESISTANCE, PARAMS.BLUDGEONING_RESISTANCE, PARAMS.POISON_RESISTANCE,
            PARAMS.FIRE_RESISTANCE, PARAMS.COLD_RESISTANCE, PARAMS.ACID_RESISTANCE,
            PARAMS.LIGHTNING_RESISTANCE, PARAMS.SONIC_RESISTANCE, PARAMS.LIGHT_RESISTANCE,};
    public static final PARAMETER[] ELEMENTAL_RESISTANCES = {PARAMS.FIRE_RESISTANCE,
            PARAMS.COLD_RESISTANCE, PARAMS.ACID_RESISTANCE, PARAMS.LIGHTNING_RESISTANCE,
            PARAMS.SONIC_RESISTANCE, PARAMS.LIGHT_RESISTANCE,};
    public static final PARAMETER[] ASTRAL_RESISTANCES = {PARAMS.ARCANE_RESISTANCE,
            PARAMS.CHAOS_RESISTANCE, PARAMS.HOLY_RESISTANCE, PARAMS.SHADOW_RESISTANCE,
            PARAMS.PSIONIC_RESISTANCE, PARAMS.DEATH_RESISTANCE,};
    public static final PARAMETER[] MAGIC_RESISTANCES = {PARAMS.DEATH_RESISTANCE,
            PARAMS.FIRE_RESISTANCE, PARAMS.WATER_RESISTANCE, PARAMS.AIR_RESISTANCE,
            PARAMS.EARTH_RESISTANCE, PARAMS.CHAOS_RESISTANCE, PARAMS.ARCANE_RESISTANCE,
            PARAMS.HOLY_RESISTANCE, PARAMS.SHADOW_RESISTANCE, PARAMS.POISON_RESISTANCE,};
    public static final PARAMETER[] PHYSICAL_RESISTANCES = {PARAMS.PIERCING_RESISTANCE,
            PARAMS.BLUDGEONING_RESISTANCE, PARAMS.SLASHING_RESISTANCE,};
    public static final PARAMETER[] RESISTANCES = {PARAMS.PIERCING_RESISTANCE,
            PARAMS.BLUDGEONING_RESISTANCE, PARAMS.SLASHING_RESISTANCE, PARAMS.POISON_RESISTANCE,
            PARAMS.FIRE_RESISTANCE, PARAMS.COLD_RESISTANCE, PARAMS.ACID_RESISTANCE,
            PARAMS.LIGHTNING_RESISTANCE, PARAMS.SONIC_RESISTANCE, PARAMS.LIGHT_RESISTANCE,

            PARAMS.CHAOS_RESISTANCE, PARAMS.ARCANE_RESISTANCE, PARAMS.HOLY_RESISTANCE,
            PARAMS.SHADOW_RESISTANCE, PARAMS.PSIONIC_RESISTANCE, PARAMS.DEATH_RESISTANCE,

            // PARAMS.WATER_RESISTANCE,
            // PARAMS.AIR_RESISTANCE,
            // PARAMS.EARTH_RESISTANCE,
    };
    public static final PARAMETER[] PRINCIPLE_ALIGNMENTS = {PARAMS.WAR_ALIGNMENT,
            PARAMS.PEACE_ALIGNMENT, PARAMS.HONOR_ALIGNMENT, PARAMS.TREACHERY_ALIGNMENT,
            PARAMS.LAW_ALIGNMENT, PARAMS.FREEDOM_ALIGNMENT, PARAMS.CHARITY_ALIGNMENT,
            PARAMS.AMBITION_ALIGNMENT, PARAMS.TRADITION_ALIGNMENT, PARAMS.PROGRESS_ALIGNMENT,

    };
    public static final PARAMETER[] IDENTITY_PARAMS = {PARAMS.IDENTITY_POINTS_PER_LEVEL,
            PARAMS.STARTING_IDENTITY_POINTS,};
    public static final PARAMETER[] PRINCIPLE_IDENTITIES = {PARAMS.WAR_IDENTITY,
            PARAMS.PEACE_IDENTITY, PARAMS.HONOR_IDENTITY, PARAMS.TREACHERY_IDENTITY,
            PARAMS.LAW_IDENTITY, PARAMS.FREEDOM_IDENTITY, PARAMS.CHARITY_IDENTITY,
            PARAMS.AMBITION_IDENTITY, PARAMS.TRADITION_IDENTITY, PARAMS.PROGRESS_IDENTITY};
    public static final VALUE[] ADDITIONAL = {PARAMS.HERO_LEVEL, PARAMS.TOTAL_XP, PARAMS.XP,
            PARAMS.GOLD, PARAMS.FREE_MASTERIES, PARAMS.ATTR_POINTS, PARAMS.MASTERY_POINTS,

            PARAMS.XP_COST_REDUCTION,

            PARAMS.GOLD_MOD, PARAMS.XP_GAIN_MOD,

    }; // everything
    public static final VALUE[] UNIT_LEVEL_PARAMETERS = {PROPS.ATTRIBUTE_PROGRESSION,
            PROPS.MASTERY_PROGRESSION, PROPS.XP_PLAN, PROPS.SPELL_PLAN, PROPS.MAIN_HAND_REPERTOIRE,
            PROPS.OFF_HAND_REPERTOIRE, PROPS.ARMOR_REPERTOIRE, PROPS.JEWELRY_ITEM_TRAIT_REPERTOIRE,
            PROPS.QUICK_ITEM_REPERTOIRE, PROPS.JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE,
            PROPS.QUALITY_LEVEL_RANGE, PROPS.ALLOWED_MATERIAL, PARAMS.JEWELRY_GOLD_PERCENTAGE,
            PARAMS.QUICK_ITEM_GOLD_PERCENTAGE, PARAMS.ARMOR_GOLD_PERCENTAGE,
            PARAMS.MAIN_HAND_GOLD_PERCENTAGE, PROPS.MEMORIZATION_PRIORITY, PROPS.VERBATIM_PRIORITY,};
    public static final VALUE[] LEVEL_PARAMETERS = {PARAMS.HERO_LEVEL, PARAMS.TOTAL_XP, PARAMS.XP,
            PARAMS.GOLD, PARAMS.FREE_MASTERIES, PARAMS.MASTERY_POINTS, PARAMS.ATTR_POINTS,
            PARAMS.ATTR_POINTS_PER_LEVEL, PARAMS.MASTERY_POINTS_PER_LEVEL,
            PARAMS.XP_COST_REDUCTION,

            PARAMS.GOLD_MOD, PARAMS.XP_GAIN_MOD, PARAMS.XP_LEVEL_MOD,
            PARAMS.IDENTITY_POINTS_PER_LEVEL, PARAMS.STARTING_IDENTITY_POINTS,

            // PARAMS.STRENGTH_PER_LEVEL,
            // PARAMS.VITALITY_PER_LEVEL,
            // PARAMS.AGILITY_PER_LEVEL,
            // PARAMS.DEXTERITY_PER_LEVEL,
            // PARAMS.WILLPOWER_PER_LEVEL,
            // PARAMS.INTELLIGENCE_PER_LEVEL,
            // PARAMS.WISDOM_PER_LEVEL,
            // PARAMS.KNOWLEDGE_PER_LEVEL,
            // PARAMS.SPELLPOWER_PER_LEVEL,
            // PARAMS.CHARISMA_PER_LEVEL,

    };
    public static final PARAMETER[] SNEAK_MODS = {PARAMS.SNEAK_DAMAGE_MOD,
            PARAMS.SNEAK_ATTACK_MOD, PARAMS.SNEAK_DEFENSE_MOD, PARAMS.SNEAK_ARMOR_MOD,
            // PARAMS.SNEAK_DAMAGE_BONUS,
            // PARAMS.SNEAK_ATTACK_BONUS, PARAMS.SNEAK_DEFENSE_PENETRATION,
            // PARAMS.SNEAK_ARMOR_PENETRATION

    };
    // else?

    //
    public static final VALUE[] ACTION_PROPS = {PROPS.DAMAGE_TYPE, G_PROPS.ACTION_TYPE,
            G_PROPS.ACTION_TAGS, PROPS.HOTKEY, PROPS.ALT_HOTKEY, PROPS.ACTION_MODES,
            PROPS.ROLL_TYPES_TO_SAVE, PROPS.TARGETING_MODIFIERS, G_PROPS.TARGETING_MODE,
            PROPS.EFFECTS_WRAP, G_PROPS.CUSTOM_SOUNDSET,};
    public static final VALUE[] ACTION_PROPS2 = {PROPS.RESISTANCE_MODIFIERS,
            PROPS.RESISTANCE_TYPE, PROPS.ON_ACTIVATE, PROPS.ON_HIT, PROPS.ON_KILL, PROPS.AI_LOGIC,
            PROPS.AI_PRIORITY_FORMULA,};
    public static final VALUE[] ACTION_PROPS_DC = {G_PROPS.ACTION_TYPE, G_PROPS.ACTION_TAGS,
            PROPS.DAMAGE_TYPE, PROPS.HOTKEY};
    public static final VALUE[] ACTION_PARAMS_DC = {PARAMS.ATTACK_MOD, PARAMS.DEFENSE_MOD,
            PARAMS.DAMAGE_MOD, PARAMS.DAMAGE_BONUS, PARAMS.ARMOR_PENETRATION, PARAMS.ARMOR_MOD,
            PARAMS.BLEEDING_MOD, PARAMS.COUNTER_MOD, PARAMS.FORCE_MOD, PARAMS.FORCE_DAMAGE_MOD,
            PARAMS.RANGE, PARAMS.COOLDOWN,

    };
    public static final VALUE[] ACTION_PARAMS_DC2 = {PARAMS.STR_DMG_MODIFIER,
            PARAMS.AGI_DMG_MODIFIER, PARAMS.INT_DMG_MODIFIER, PARAMS.SP_DMG_MODIFIER,
            PARAMS.CRITICAL_MOD, PARAMS.IMPACT_AREA, G_PARAMS.RADIUS, PARAMS.AUTO_ATTACK_RANGE,
            PARAMS.SIDE_DAMAGE_MOD, PARAMS.DIAGONAL_DAMAGE_MOD, PARAMS.SIDE_ATTACK_MOD,
            PARAMS.DIAGONAL_ATTACK_MOD
            // G_PARAMS.DURATION

    };
    // attack/defense/damage/str/agi... mods, costs, damage type,
    // bleeding modifiers, bash on focus/initiative/stamina,
    // plus some roll effects like knockdown (mass, strength, reflex...).
    public static final VALUE[] ACTION_ATTACK_PARAMS = {PARAMS.ATTACK_MOD, PARAMS.DEFENSE_MOD,
            PARAMS.DAMAGE_MOD, PARAMS.DAMAGE_BONUS, PARAMS.ARMOR_PENETRATION, PARAMS.ARMOR_MOD,
            PARAMS.BLEEDING_MOD, PARAMS.COUNTER_MOD, PARAMS.FORCE_MOD, PARAMS.FORCE_DAMAGE_MOD,
            PARAMS.FORCE_MAX_STRENGTH_MOD, PARAMS.FORCE_MOD_SOURCE_WEIGHT, PARAMS.STR_DMG_MODIFIER,
            PARAMS.AGI_DMG_MODIFIER, PARAMS.INT_DMG_MODIFIER, PARAMS.SP_DMG_MODIFIER,
            PARAMS.DURABILITY_DAMAGE_MOD, PARAMS.CRITICAL_MOD, PARAMS.ACCURACY, PARAMS.IMPACT_AREA,
            PARAMS.AUTO_ATTACK_RANGE, PARAMS.SIDE_DAMAGE_MOD, PARAMS.DIAGONAL_DAMAGE_MOD,
            PARAMS.SIDE_ATTACK_MOD, PARAMS.DIAGONAL_ATTACK_MOD

            , PARAMS.CLOSE_QUARTERS_ATTACK_MOD, PARAMS.CLOSE_QUARTERS_DAMAGE_MOD,
            PARAMS.LONG_REACH_ATTACK_MOD, PARAMS.LONG_REACH_DAMAGE_MOD,

    };
    public static final VALUE[] ACTION_PARAMS = {G_PARAMS.RADIUS, PARAMS.RANGE, PARAMS.COOLDOWN,
            G_PARAMS.DURATION};
    public static final VALUE[] SPELL_PARAMETERS = {PARAMS.CIRCLE, PARAMS.SPELL_DIFFICULTY,
            G_PARAMS.DURATION, G_PARAMS.RADIUS, PARAMS.RANGE, PARAMS.COOLDOWN,
            PARAMS.SPELLPOWER_MOD, PARAMS.FORCE, PARAMS.FORCE_SPELLPOWER_MOD,
            PARAMS.FORCE_DAMAGE_MOD, PARAMS.FORCE_KNOCK_MOD, PARAMS.FORCE_PUSH_MOD, PARAMS.XP_COST,

    };
    public static final VALUE[] QUICK_ITEM_PARAMETERS = {PARAMS.CHARGES, PARAMS.COOLDOWN,
            PARAMS.AP_COST, PARAMS.STA_COST, PARAMS.ESS_COST, PARAMS.FOC_COST, PARAMS.FOC_REQ,
            PARAMS.ENDURANCE_COST, G_PARAMS.DURATION, PARAMS.RANGE, G_PARAMS.RADIUS,
            // PARAMS.SPELLPOWER_BONUS,
            // PARAMS.SPELLPOWER_MOD,
    };
    public static final VALUE[] ITEM_PARAMETERS = {PARAMS.WEIGHT, PARAMS.GOLD_COST,

    };
    public static final VALUE[] ITEM_PROPERTIES = {PROPS.ITEM_SPELL, G_PROPS.ITEM_TYPE,
            G_PROPS.ITEM_GROUP};
    public static final VALUE[] QUICK_ITEM_PROPERTIES = {

            G_PROPS.TARGETING_MODE, PROPS.EFFECTS_WRAP, PROPS.TARGETING_MODIFIERS, PROPS.DAMAGE_TYPE,
            G_PROPS.SOUNDSET, G_PROPS.CUSTOM_SOUNDSET, G_PROPS.IMPACT_SPRITE,};
    public static final VALUE[] DC_SPELL_PROPERTIES = {G_PROPS.SPELL_TYPE, G_PROPS.SPELL_GROUP,
            G_PROPS.SPELL_TAGS, PROPS.DAMAGE_TYPE,};
    public static final VALUE[] SPELL_PROPERTIES = {G_PROPS.SPELL_GROUP, G_PROPS.SPELL_TYPE,
            G_PROPS.SPELL_TAGS,

            G_PROPS.TARGETING_MODE, PROPS.EFFECTS_WRAP, PROPS.RETAIN_CONDITIONS,
            PROPS.TARGETING_MODIFIERS, PROPS.DAMAGE_TYPE, G_PROPS.SPELL_UPGRADE_GROUPS,

            G_PROPS.SOUNDSET, G_PROPS.CUSTOM_SOUNDSET, G_PROPS.IMPACT_SPRITE};
    public static final PARAMETER[] COSTS = {PARAMS.AP_COST, PARAMS.STA_COST, PARAMS.ESS_COST,
            PARAMS.FOC_COST, PARAMS.FOC_REQ, PARAMS.ENDURANCE_COST, PARAMS.CP_COST,

    };
    public static final VALUE[] UPKEEP_PARAMS = {PARAMS.AP_UPKEEP, PARAMS.STA_UPKEEP,
            PARAMS.ESS_UPKEEP, PARAMS.FOC_UPKEEP, PARAMS.END_UPKEEP,

    };
    public static final VALUE[] SKILL_PARAMETERS = {PARAMS.CIRCLE, PARAMS.SKILL_DIFFICULTY,
            PARAMS.XP_COST, PARAMS.RANK_MAX, PARAMS.RANK_SD_MOD, PARAMS.RANK_FORMULA_MOD,
            PARAMS.RANK_XP_MOD, PARAMS.RANK_REQUIREMENT,

    };
    public static final VALUE[] SKILL_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.SKILL_GROUP,
            G_PROPS.BASE_TYPE,

    };
    public static final VALUE[] SKILL_ADDITIONAL = {

            PROPS.ATTRIBUTE_BONUSES, PROPS.PARAMETER_BONUSES, PARAMS.RANK_MAX, PARAMS.RANK_FORMULA_MOD,
            PARAMS.RANK_XP_MOD, PARAMS.RANK_REQUIREMENT,

            PROPS.LINK_VARIANT, PROPS.TREE_NODE_GROUP, PROPS.TREE_NODE_PRIORITY,
            PARAMS.TREE_LINK_OFFSET_X, PARAMS.TREE_LINK_OFFSET_Y,

            PARAMS.TREE_NODE_OFFSET_X, PARAMS.TREE_NODE_OFFSET_Y,

            PARAMS.HT_CUSTOM_POS_X, PARAMS.HT_CUSTOM_POS_Y,

    };
    public static final VALUE[] CLASS_HEADER2 = {PROPS.LINK_VARIANT, PROPS.TREE_NODE_GROUP,
            PROPS.TREE_NODE_PRIORITY, PARAMS.TREE_LINK_OFFSET_X, PARAMS.TREE_LINK_OFFSET_Y,

            PARAMS.TREE_NODE_OFFSET_X, PARAMS.TREE_NODE_OFFSET_Y,};
    public static final VALUE[] CLASS_HEADER = {G_PROPS.ACTIVES, PROPS.SKILL_OR_REQUIREMENTS,
            PROPS.SKILL_REQUIREMENTS, PROPS.REQUIREMENTS,

            G_PROPS.CLASS_TYPE, G_PROPS.CLASS_GROUP,

            G_PROPS.BASE_TYPE, PROPS.ALT_BASE_LINKS, PROPS.ALT_BASE_TYPES, PROPS.ATTRIBUTE_BONUSES,
            PROPS.PARAMETER_BONUSES, PARAMS.CIRCLE, PARAMS.XP_COST,

            PARAMS.RANK_MAX, PARAMS.RANK_FORMULA_MOD, PARAMS.RANK_XP_MOD, PARAMS.RANK_REQUIREMENT,
            PROPS.BASE_CLASSES_ONE, PROPS.BASE_CLASSES_TWO,

    };
    public static final VALUE[] REQUIREMENTS = {PROPS.SKILL_REQUIREMENTS,
            PROPS.SKILL_OR_REQUIREMENTS, PROPS.CLASSES, PROPS.KNOWN_SPELLS, PROPS.VERBATIM_SPELLS,
            PROPS.REQUIREMENTS,

    };
    public static final PARAMETER[] PENALTIES = {PARAMS.STAMINA_PENALTY, PARAMS.AP_PENALTY,
            PARAMS.FOCUS_PENALTY, PARAMS.ESSENCE_PENALTY, PARAMS.ATTACK_STA_PENALTY,
            PARAMS.ATTACK_AP_PENALTY, PARAMS.SPELL_STA_PENALTY, PARAMS.SPELL_ESS_PENALTY,
            PARAMS.SPELL_FOC_PENALTY, PARAMS.SPELL_AP_PENALTY, PARAMS.MOVE_STA_PENALTY,
            PARAMS.MOVE_AP_PENALTY,

    };
    public static final VALUE[] ARMOR_PARAMETERS = {PARAMS.MATERIAL_QUANTITY, PARAMS.ARMOR_LAYERS,
            PARAMS.COVER_PERCENTAGE, PARAMS.HARDNESS, PARAMS.ARMOR_MODIFIER,
            PARAMS.DURABILITY_MODIFIER, PARAMS.COST_MODIFIER, PARAMS.ATTACK_MOD,
            PARAMS.DEFENSE_MOD, PARAMS.DEFENSE_BONUS, PARAMS.C_DURABILITY, PARAMS.WEIGHT,
            PARAMS.GOLD_COST,

    };
    public static final VALUE[] WEAPON_PARAMETERS = {PARAMS.MATERIAL_QUANTITY, PARAMS.IMPACT_AREA,
            PARAMS.HARDNESS, PARAMS.DICE, PARAMS.BASE_DAMAGE_MODIFIER, PARAMS.DURABILITY_MODIFIER,
            PARAMS.STR_DMG_MODIFIER, PARAMS.AGI_DMG_MODIFIER, PARAMS.SP_DMG_MODIFIER,
            PARAMS.INT_DMG_MODIFIER,

            PARAMS.COST_MODIFIER, PARAMS.ARMOR_MOD, PARAMS.ATTACK_MOD, PARAMS.DAMAGE_MOD,
            PARAMS.DEFENSE_MOD, PARAMS.ARMOR_PENETRATION, PARAMS.ATTACK_BONUS, PARAMS.DAMAGE_BONUS,
            PARAMS.DEFENSE_BONUS, PARAMS.C_DURABILITY, PARAMS.WEIGHT, PARAMS.GOLD_COST,

    };
    public static final VALUE[] JEWELRY_PROPERTIES = {G_PROPS.MATERIAL, G_PROPS.JEWELRY_GROUP,
            PROPS.MAGICAL_ITEM_TRAIT, PROPS.MAGICAL_ITEM_LEVEL, PROPS.ENCHANTMENT,
            G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES};
    public static final VALUE[][] JEWELRY_PAGES = {DESCRIPTION, JEWELRY_PROPERTIES, ATTRIBUTES,};
    public static final VALUE[] ARMOR_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.ARMOR_TYPE,
            G_PROPS.ARMOR_GROUP, G_PROPS.ITEM_MATERIAL_GROUP, G_PROPS.MATERIAL,};
    public static final VALUE[] WEAPON_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.WEAPON_TYPE,
            G_PROPS.WEAPON_GROUP, G_PROPS.WEAPON_CLASS, G_PROPS.WEAPON_SIZE,
            G_PROPS.ITEM_MATERIAL_GROUP, PROPS.DAMAGE_TYPE, G_PROPS.MATERIAL,
            G_PROPS.STANDARD_PASSIVES,

    };
    public static final VALUE[] AUTO_TEST_VALUES = {PROPS.AUTO_TEST_TYPE, PROPS.AUTO_TEST_GROUP,
            PARAMS.AUTO_TEST_ID, PROPS.AUTO_TEST_MEASUREMENTS, PROPS.AUTO_TEST_ASSERTIONS,
            PROPS.AUTO_TEST_CONSTRAINTS, PROPS.AUTO_TEST_WEAPON, PROPS.AUTO_TEST_OFFHAND_WEAPON,
            PROPS.AUTO_TEST_PREFS, PROPS.AUTO_TEST_RULE_FLAGS, PROPS.AUTO_TEST_CONSTRAINTS,};
    public static final VALUE[][] SKILL_PAGES = {SKILL_PROPERTIES, SKILL_PARAMETERS, REQUIREMENTS,
            SKILL_ADDITIONAL, ATTRIBUTES, RESISTANCES, UNIT_PARAMETERS, AUTO_TEST_VALUES};
    public static final VALUE[][] ACTION_PAGES = {ACTION_PROPS, ACTION_ATTACK_PARAMS,
            ACTION_PARAMS, COSTS, SNEAK_MODS, ACTION_PROPS2, AUTO_TEST_VALUES,};
    public static final VALUE[][] ACTION_PAGES_DC = {DESCRIPTION, COSTS, ACTION_PROPS_DC,
            ACTION_PARAMS_DC, ACTION_PARAMS_DC2};
    public static final VALUE[][] ARMOR_PAGES = {ARMOR_PROPERTIES, ARMOR_PARAMETERS, PENALTIES,
            UNIT_PARAMETERS};
    public static final VALUE[][] ALT_QUICK_ITEM_PAGES = {DESCRIPTION, QUICK_ITEM_PARAMETERS,
            COSTS};
    public static final VALUE[][] QUICK_ITEM_PAGES = {QUICK_ITEM_PROPERTIES, ITEM_PROPERTIES,
            QUICK_ITEM_PARAMETERS, ITEM_PARAMETERS, COSTS};
    public static final VALUE[][] WEAPON_PAGES = {WEAPON_PROPERTIES, WEAPON_PARAMETERS, PENALTIES};
    public static final VALUE[][] ALT_CHAR_PAGES = {CHARS_HEADER, ATTRIBUTES, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES};
    public static final VALUE[][] ALT_UNIT_PAGES = {UNITS_HEADER, ATTRIBUTES, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES, DESCRIPTION};
    public static final VALUE[][] ALT_BF_OBJ_PAGES = {BF_OBJ_HEADER, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES,}; // DIFFERENT
    public static final VALUE[][] UNIT_PAGES = {UNIT_PARAMETERS, UNIT_DYNAMIC_PARAMETERS,
            UNIT_PROPERTIES, ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, RESISTANCES,
            UNIT_LEVEL_PARAMETERS, LEVEL_PARAMETERS, PRINCIPLE_ALIGNMENTS, PRINCIPLE_IDENTITIES};
    public static final VALUE[][] CHAR_PAGES = {UNIT_PARAMETERS, UNIT_DYNAMIC_PARAMETERS,
            UNIT_PROPERTIES, ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, RESISTANCES, LEVEL_PARAMETERS,
            PRINCIPLE_ALIGNMENTS, PRINCIPLE_IDENTITIES};
    public static final VALUE[][] CLASS_PAGES = {CLASS_HEADER, CLASS_HEADER2,
            // ATTRIBUTES_VAL,
            // MA
            UNIT_PARAMETERS, AUTO_TEST_VALUES,};
    public static final VALUE[][] SPELL_PAGES = {SPELL_PARAMETERS, COSTS, SPELL_PROPERTIES,
            UPKEEP_PARAMS, SPELLS_UPGRADES};
    // VALUE
    // ICONS?
    public static final PARAMETER[] BACKGROUND_PARAMS = {
            // PARAMS.TOUGHNESS,
            // PARAMS.ENDURANCE, PARAMS.STAMINA, PARAMS.ESSENCE,
            // PARAMS.STARTING_FOCUS, PARAMS.SPIRIT,
            PARAMS.WEIGHT, PARAMS.CARRYING_CAPACITY, PARAMS.SIGHT_RANGE, PARAMS.SIDE_SIGHT_PENALTY,
            PARAMS.DETECTION, PARAMS.STEALTH,};
    public static final PARAMETER[] BACKGROUND_PARAMS_ADDITIONAL = {PARAMS.XP, PARAMS.GOLD,
            PARAMS.ATTR_POINTS, PARAMS.MASTERY_POINTS,

            PARAMS.FREE_MASTERIES, PARAMS.MASTERY_POINTS_PER_LEVEL, PARAMS.ATTR_POINTS_PER_LEVEL,
            PARAMS.GIRTH,
            // PARAMS.MEMORIZATION_CAP,
            // PARAMS.XP_GAIN_MOD,
            // PARAMS.XP_LEVEL_MOD,
            // PARAMS.XP_COST_REDUCTION,
            // PARAMS.GOLD_COST_REDUCTION,

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
            BACKGROUND_PARAMS_ADDITIONAL, LORE, IDENTITY_PARAMS};
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

    };
    public static final PARAMETER[] MASTERIES_MAGIC_SCHOOLS = {

            PARAMS.PSYCHIC_MASTERY, PARAMS.SHADOW_MASTERY, PARAMS.WITCHERY_MASTERY,
            PARAMS.NECROMANCY_MASTERY, PARAMS.AFFLICTION_MASTERY, PARAMS.BLOOD_MAGIC_MASTERY,
            PARAMS.SAVAGE_MASTERY, PARAMS.SYLVAN_MASTERY, PARAMS.ELEMENTAL_MASTERY,
            PARAMS.CONJURATION_MASTERY, PARAMS.SORCERY_MASTERY, PARAMS.ENCHANTMENT_MASTERY,
            PARAMS.REDEMPTION_MASTERY, PARAMS.CELESTIAL_MASTERY, PARAMS.BENEDICTION_MASTERY,
            PARAMS.WARP_MASTERY, PARAMS.DESTRUCTION_MASTERY, PARAMS.DEMONOLOGY_MASTERY,};
    private static final VALUE[] DC_SPELL_PARAMETERS = {

            PARAMS.AP_COST, PARAMS.STA_COST, PARAMS.ESS_COST, PARAMS.ENDURANCE_COST, PARAMS.FOC_COST,
            PARAMS.FOC_REQ, PARAMS.RANGE, G_PARAMS.RADIUS, PARAMS.SPELLPOWER_MOD,
            G_PARAMS.DURATION, PARAMS.COOLDOWN, PARAMS.C_COOLDOWN,

    };
    public static final VALUE[][] ALT_SPELL_PAGES = {DESCRIPTION, DC_SPELL_PARAMETERS,
            DC_SPELLS_HEADER, SPELLS_UPGRADES};

    public static final VALUE[][] UNIT_INFO_PARAMS_PHYSICAL = {
     {
      PARAMS.INITIATIVE_MODIFIER, PARAMS.INITIATIVE,
      PARAMS.STARTING_FOCUS,
//      PARAMS.BASE_MORALE,
      PARAMS.ENDURANCE_REGEN, PARAMS.STAMINA_REGEN,
      PARAMS.TOUGHNESS_RECOVERY ,
//      PARAMS.UNCONSCIOUS_THRESHOLD
     },
     {
      PARAMS.SIGHT_RANGE, PARAMS.SIDE_SIGHT_PENALTY,
      PARAMS.HEIGHT, PARAMS.GIRTH,
      PARAMS.WEIGHT, PARAMS.CARRYING_CAPACITY,
      PARAMS.ILLUMINATION, PARAMS.CONCEALMENT
 },
     {
      PARAMS.FOCUS_RETAINMENT , PARAMS.FOCUS_RESTORATION,
      PARAMS. MORALE_RETAINMENT , PARAMS.MORALE_RESTORATION,
//      PARAMS.FEAR RESISTANCE,  PARAMS.WOUNDS RESISTANCE,
//     FATIGUE, CONFUSION,
     },

    };

    public static final VALUE[][] UNIT_INFO_PARAMS_COMBAT = {
     {

     },
//     Crit Bonus, Crit Chance
//    Penalties +Move x 2
//    Penalties +Attack x 2
//    Position mods x 2
//     2
//    Resistance Penetration, Armor Penetration
//    Block Chance, Parry Chance
//    Block Penetration, Parry Penetration
//     Accuracy, Evasion
//3
//    Close quarters - 2x2
//    Long Reach - 2x2
    };

    private static final VALUE[][] UNIT_INFO_PARAMS_MISC = {
     {

     },
//    QUICK_SLOTS, Item Use Speed
//    Detection Stealth
//    Sneak params - 2x2 def, dmg
//
//   Watch x4 – atk/def
//
//3
//    Cadence - 2x2
//
//     Rolls
//    Rolls – 2x12
//    Pass/Stop chance bonuses - 2x4
    };
    private static final VALUE[][] UNIT_INFO_PARAMS_MAGIC = {
     {

     },
    };

    public static final String[] INFO_TABLE_NAMES= {
     "Physical",  "Combat",  "Magic",  "Misc",
    };
    public static final VALUE[][][] UNIT_INFO_PARAMS = {
     UNIT_INFO_PARAMS_PHYSICAL,
     UNIT_INFO_PARAMS_COMBAT ,
     UNIT_INFO_PARAMS_MAGIC,
     UNIT_INFO_PARAMS_MISC ,
    };
    public static VALUE[] ARMOR_GRADES;

    static {
        // TODO revamp value_page usage?
        // List<MultiParameter> armorGradeMultiParams =
        // DC_ContentManager.getArmorGradeMultiParams();
        // ARMOR_GRADES = armorGradeMultiParams.toArray(new
        // VALUE[armorGradeMultiParams.size()]);
    }

    static {
        for (Field f : ValuePages.class.getFields()) {
            if (f.getType().isArray()) {

            }
        }
    }
    public enum PAGE_NAMES {
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
    }

}
