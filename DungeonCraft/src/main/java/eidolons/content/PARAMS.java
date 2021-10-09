package eidolons.content;

import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.system.math.DC_MathManager;
import eidolons.system.text.DescriptionTooltips;
import main.content.C_OBJ_TYPE;
import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.content.text.Descriptions;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

//Quantative properties of ..*what?*
public enum PARAMS implements PARAMETER {
    SLEIGHT_OF_HAND("chars", "", "", false, 0),
    FORAGING("chars", "", "", false, 0),

    POWER_MOD(true, null, "", false, 100, "encounters"),
    POWER_MINIMUM(null, "", false, 0, "encounters"),
    POWER_BASE(null, "", false, 0, "encounters"),
    POWER_MAXIMUM(null, "", false, 0, "encounters"),

    POWER_TOTAL(true, "units", "chars"),
    SP_PTS_SPENT(true, "units", "chars"),
    SK_PTS_SPENT(true, "units", "chars"),


    FREE_MASTERIES(null, "", false, DC_MathManager.DEFAULT_FREE_MASTERY_COUNT, "chars"),
    GLORY("party"),
    MAX_HEROES(null, "", false, 0, "party", "arcades"),
    ORDER_CHANCE_MOD(null, "", false, 100, "units", "chars", "perks"),
    ORGANIZATION(null, "", true, 100, "units", "chars", "perks", "party"),
    BATTLE_SPIRIT(null, "", true, 100, "units", "chars", "perks", "party"),
    PRINCIPLE_CLASHES(null, "", true, 0, "units", "chars", "perks", "party"),
    PRINCIPLE_CLASHES_REDUCTION(null, "", true, 0, "units", "chars", "perks", "party"),
    WAR_ALIGNMENT(StringMaster.format("WAR_ALIGNMENT"), null, false, 0, "units", "chars"),
    PEACE_ALIGNMENT(StringMaster.format("PEACE AL."), null, false, 0, "units", "chars"),
    HONOR_ALIGNMENT(StringMaster.format("HONOR AL."), null, false, 0, "units", "chars"),
    TREACHERY_ALIGNMENT(StringMaster.format("TREACHERY AL."), null, false, 0, "units", "chars"),
    LAW_ALIGNMENT(StringMaster.format("LAW_ALIGNMENT"), null, false, 0, "units", "chars"),
    FREEDOM_ALIGNMENT(StringMaster.format("FREEDOM AL."), null, false, 0, "units", "chars"),
    CHARITY_ALIGNMENT(StringMaster.format("CHARITY AL."), null, false, 0, "units", "chars"),
    AMBITION_ALIGNMENT(StringMaster.format("AMBITION AL."), null, false, 0, "units", "chars"),
    TRADITION_ALIGNMENT(StringMaster.format("TRADITION AL."), null, false, 0, "units", "chars"),
    PROGRESS_ALIGNMENT(StringMaster.format("PROGRESS AL."), null, false, 0, "units", "chars"),

    WAR_IDENTITY(StringMaster.format("WAR_IDENTITY"), null, false, 0, "units", "chars"),
    PEACE_IDENTITY(StringMaster.format("PEACE id."), null, false, 0, "units", "chars"),
    HONOR_IDENTITY(StringMaster.format("HONOR id."), null, false, 0, "units", "chars"),
    TREACHERY_IDENTITY(StringMaster.format("TREACHERY id."), null, false, 0, "units", "chars"),
    LAW_IDENTITY(StringMaster.format("LAW_IDENTITY"), null, false, 0, "units", "chars"),
    FREEDOM_IDENTITY(StringMaster.format("FREEDOM id."), null, false, 0, "units", "chars"),
    CHARITY_IDENTITY(StringMaster.format("CHARITY id."), null, false, 0, "units", "chars"),
    AMBITION_IDENTITY(StringMaster.format("AMBITION id."), null, false, 0, "units", "chars"),
    TRADITION_IDENTITY(StringMaster.format("TRADITION id."), null, false, 0, "units", "chars"),
    PROGRESS_IDENTITY(StringMaster.format("PROGRESS id."), null, false, 0, "units", "chars"),

    IDENTITY_POINTS(null, "", true, 0, "chars", "classes", "deities"),
    STARTING_IDENTITY_POINTS(null, "", false, 3, "chars"),
    IDENTITY_POINTS_PER_LEVEL(null, "", false, 50, "chars"), // /100
    TOTAL_IDENTITY_POINTS(null, "", true, 0, "chars"),

    DEITY_CLASHES_REDUCTION(null, "", true, 0, "units", "chars", "perks", "party"),
    SHARED_PRINCIPLES_BOOST(null, "", true, 0, "units", "chars", "perks", "party"),
    SHARED_DEITIES_BOOST(null, "", true, 0, "units", "chars", "perks", "party"),
    PRINCIPLE_CLASHES_REMOVED(null, "", true, 0, "units", "chars", "perks", "party"),
    // comradeship, competition, love between each 2 heroes?
    // affection, esteem, towards leader


    STR_DMG_MODIFIER("Str dmg mod", "", false, 0, "actions", "jewelry"), // HEAVY/MELEE
    AGI_DMG_MODIFIER("Agi dmg mod", "", false, 0, "actions", "jewelry"), // LIGHT/RANGED
    SP_DMG_MODIFIER("Sp dmg mod", "", false, 0, "actions", "jewelry"), // WANDS
    INT_DMG_MODIFIER("Int dmg mod", "", false, 0, "actions", "jewelry"), // MECHANISMS
    MATERIAL_QUANTITY("Material pieces", "", false, 4, "jewelry"), // MECHANISMS

    DAMAGE_TOTAL(null, "", true, 0, "actions", "units", "chars", "perks"),
    DAMAGE_LAST_AMOUNT(null, "", true, 0, "actions", "units", "chars", "perks"),
    DAMAGE_LAST_DEALT(null, "", true, 0, "actions", "units", "chars", "perks"),

    DURABILITY(null, "", false, 5),
    C_DURABILITY(null, "", true, 5),
    DICE(null, "", false, 1),
    DIE_SIZE(null, "", false, 6),
    BASE_DAMAGE_MODIFIER("Base dmg mod", "", false, 100),
    ARMOR_MODIFIER(null, "", false, 50),
    RESISTANCE_MODIFIER("Resistance mod", "", false, 10),

    DURABILITY_MODIFIER("Durability mod", "", false, 100),
    COST_MODIFIER(null, "", false, 100),
    QUICK_SLOTS(null, "", false, 0, "units", "chars", "perks"),
    C_QUICK_SLOTS(null, "", true, 2, "units", "chars", "perks"),
    ITEM_COST_MOD(null, "", false, 0, "units", "chars", "perks"),

    CHARGES(null, "", false, 1, "items"),
    C_CHARGES(null, "", true, 1, "items"),
    // UNIT/CHAR


    TOUGHNESS(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    C_TOUGHNESS(null, "", true, 0, "units", "chars", "perks", "bf obj"),
    ENDURANCE(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    C_ENDURANCE(null, "", true, 0, "units", "chars", "perks", "bf obj"),
    STAMINA(null, "STAMINA", false, 5, "units", "chars", "perks"),
    C_STAMINA(null, "STAMINA", true, 5, "units", "chars", "perks"),
    FOCUS(null, "FOCUS", false, 100, "units", "chars", "perks"),
    C_FOCUS(null, "FOCUS", true, 0, "units", "chars", "perks"),
    ESSENCE(null, "ESSENCE", false, 0, "units", "chars", "perks"),
    C_ESSENCE(null, "ESSENCE", true, 0, "units", "chars", "perks"),
    EXTRA_ATTACKS("Extra Attacks", "", false, 0, "units", "chars", "perks"),
    C_EXTRA_ATTACKS("Current Extra Attacks", "", true, 2, "units", "chars", "perks"),

    EXTRA_MOVES("Extra Moves", "", false, 0, "units", "chars", "perks"),
    C_EXTRA_MOVES("Current Extra Moves", "", true, 2, "units", "chars", "perks"),

    TOUGHNESS_RECOVERY(null, "", false, 25, "bf obj", "units", "chars", "perks"),
    TOUGHNESS_RETAINMENT(null, "", false, 25, "bf obj", "units", "chars", "perks"),
    FOCUS_RESTORATION(null, "", false, 0, "units", "chars", "perks"),
    FOCUS_RETAINMENT(null, "", false, 0, "units", "chars", "perks"),
    ESSENCE_RESTORATION(null, "", false, 0, "units", "chars", "perks"),
    ESSENCE_RETAINMENT(null, "", false, 0, "units", "chars", "perks"),

    INITIATIVE("Initiative", "", false, 4, "units", "chars", "perks", "bf obj"),
    ATB_START_MOD("ATB_START_PRESET", "", false, 0, "units", "chars", "perks", "bf obj"),
    ATB_START_PRESET("ATB_START_PRESET", "", false, 0, "units", "chars", "perks", "bf obj"),

    C_ATB("Current Readiness", "", true, 0, "units", "chars", "perks", "bf obj") {
        @Override
        public String getDisplayedName() {
            return "Readiness";
        }
    },
    ATB("Initiative", "", false, 0, "units", "chars", "perks", "bf obj"), //what is this for?


    STRENGTH(null, Descriptions.Strength, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    VITALITY(null, Descriptions.Vitality, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    AGILITY(null, Descriptions.Agility, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    DEXTERITY(null, Descriptions.Dexterity, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    WILLPOWER(null, Descriptions.Willpower, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    INTELLIGENCE(null, Descriptions.Intelligence, false, 0, "chars", "jewelry", "units", "classes"),
    SPELLPOWER(null, Descriptions.Spellpower, false, 0, "chars", "jewelry", "units", "classes"),
    KNOWLEDGE(null, Descriptions.Knowledge, false, 0, "chars", "jewelry", "units", "classes"),
    WISDOM(null, Descriptions.Wisdom, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    CHARISMA(null, Descriptions.CHARITY, false, 0, "units", "chars", "perks", "jewelry", "classes"),

    // MASTERY_REQ =
    // LEVEL_REQ

    POWER("Power", "Power points", false, 10, "units", "chars", "perks", "bf obj"),

    BASE_STRENGTH(null, Descriptions.Strength, false, 0, "units", "chars", "perks"),
    BASE_VITALITY(null, Descriptions.Vitality, false, 0, "units", "chars", "perks"),
    BASE_AGILITY(null, Descriptions.Agility, false, 0, "units", "chars", "perks"),
    BASE_DEXTERITY(null, Descriptions.Dexterity, false, 0, "units", "chars", "perks"),
    BASE_WILLPOWER(null, Descriptions.Willpower, false, 0, "units", "chars", "perks"),
    BASE_INTELLIGENCE(null, Descriptions.Intelligence, false, 0, "units", "chars", "perks"),
    BASE_SPELLPOWER(null, Descriptions.Spellpower, false, 0, "units", "chars", "perks"),
    BASE_KNOWLEDGE(null, Descriptions.Knowledge, false, 0, "units", "chars", "perks"),
    BASE_WISDOM(null, Descriptions.Wisdom, false, 0, "units", "chars", "perks"),
    BASE_CHARISMA(null, Descriptions.CHARITY, false, 0, "units", "chars", "perks"),

    DEFAULT_STRENGTH(null, Descriptions.Strength, false, 0, "chars"),
    DEFAULT_VITALITY(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_AGILITY(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_DEXTERITY(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_WILLPOWER(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_INTELLIGENCE(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_SPELLPOWER(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_KNOWLEDGE(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_WISDOM(null, Descriptions.Agility, false, 0, "chars"),
    DEFAULT_CHARISMA(null, Descriptions.Agility, false, 0, "chars"),

    ESSENCE_REGEN("Ess. Regen", "ESSENCE_REGEN", false, 0, "units", "chars", "perks"),
    FOCUS_REGEN(null, "FOCUS_REGEN", false, 0, "units", "chars", "perks"),
    STARTING_FOCUS("Start foc", "FOCUS", false, 0, "units", "chars", "perks"),
    STARTING_ESSENCE("Start foc", "FOCUS", false, 0, "units", "chars", "perks"),
    BASE_DAMAGE("Base Damage", "", false, 0, "units", "chars", "perks"),
    DAMAGE("Damage", "", false, 0, "units", "chars", "perks"),
    MIN_DAMAGE("Min Damage", "", false, 0, "units", "chars", "perks"),
    MAX_DAMAGE("Max Damage", "", false, 0, "units", "chars", "perks"),

    OFF_HAND_DAMAGE("Offhand Damage", "", false, 0, "units", "chars", "perks"),
    OFF_HAND_MIN_DAMAGE("Offh. Min Dmg", "", false, 0, "units", "chars", "perks"),
    OFF_HAND_MAX_DAMAGE("Offh. Max Dmg", "", false, 0, "units", "chars", "perks"),
    // ARMOR("Armor", "", false, 0, "units", "chars", "perks", "bf obj",
    // "armor"), see last, quick fixer -> ok I don't remember why I did it...
    ARMOR("Armor", "", false, 0, "units", "chars", "perks", "bf obj"),
    ARMOR_LAYERS("Layers", "", false, 1),
    COVER_PERCENTAGE("Cover perc.", "", false, 50),
    HARDNESS("Hardness", "", false, 0),
    PIERCING_ARMOR("Piercing", "", false, 0),
    BLUDGEONING_ARMOR("Bludgeoning", "", false, 0),
    SLASHING_ARMOR("Slashing", "", false, 0),

    FIRE_ARMOR("Fire", "", false, 0),
    COLD_ARMOR("Cold", "", false, 0),
    ACID_ARMOR("Acid", "", false, 0),
    LIGHTNING_ARMOR("Lightning", "", false, 0),
    SONIC_ARMOR("Sonic", "", false, 0),
    LIGHT_ARMOR("Light", "", false, 0),

    HOLY_ARMOR("Fire", "", false, 0),
    SHADOW_ARMOR("Cold", "", false, 0),
    DEATH_ARMOR("Cold", "", false, 0),
    ARCANE_ARMOR("Cold", "", false, 0),
    CHAOS_ARMOR("Cold", "", false, 0),
    PSIONIC_ARMOR("Cold", "", false, 0),

    PIERCING_DURABILITY_MOD(true, "Piercing", "", false, 0),
    BLUDGEONING_DURABILITY_MOD(true, "Bludgeoning", "", false, 0),
    SLASHING_DURABILITY_MOD(true, "Slashing", "", false, 0),

    FIRE_DURABILITY_MOD(true, "Fire", "", false, 0),
    COLD_DURABILITY_MOD(true, "Cold", "", false, 0),
    ACID_DURABILITY_MOD(true, "Acid", "", false, 0),
    LIGHTNING_DURABILITY_MOD(true, "Lightning", "", false, 0),
    SONIC_DURABILITY_MOD(true, "Sonic", "", false, 0),
    LIGHT_DURABILITY_MOD(true, "Light", "", false, 0),

    HOLY_DURABILITY_MOD(true, "Fire", "", false, 0),
    SHADOW_DURABILITY_MOD(true, "Cold", "", false, 0),
    DEATH_DURABILITY_MOD(true, "Cold", "", false, 0),
    ARCANE_DURABILITY_MOD(true, "Cold", "", false, 0),
    CHAOS_DURABILITY_MOD(true, "Cold", "", false, 0),
    PSIONIC_DURABILITY_MOD(true, "Cold", "", false, 0),

    //TODO saving throws revamp
    GRIT(null, "", false, 3, "units", "chars", "perks"),
    // willpower/spellpower
    WIT(null, "", false, 3, "units", "chars", "perks"),
    // intelligence/knowledge
    SPIRIT(null, "", false, 3, "units", "chars", "perks"),
    //charisma/wisdom
    FORTITUDE(null, "", false, 5, "units", "chars", "perks"),
    // vit/str
    REFLEX(null, "", false, 3, "units", "chars", "perks"),
    // agi/dex

    ENDURANCE_REGEN("End. Regen.", "", false, 0, "units", "chars", "perks", "bf obj"), // Color.GREEN.brighter()),
    RESISTANCE(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    DEFENSE("Defense", "", false, 0, "units", "chars", "perks", "bf obj"),
    ATTACK("Attack", "", false, 0, "units", "chars", "perks", "bf obj"),
    OFF_HAND_ATTACK("Offh. ATK", "", false, 0, "units", "chars", "perks"),
    DEFENSE_PENETRATION("Def. Pntr", "", false, 0, "chars"),
    RESISTANCE_PENETRATION("Res. Pntr", "", false, 0, "actions", "units", "chars", "perks"),
    ARMOR_PENETRATION("Armor Pntr", "", false, 0, "actions", "units", "chars", "perks"),
    ARMOR_MOD(true, "Armor Pntr %", "", false, 100, "actions", "units", "chars", "perks"),
    ARMOR_BLOCK_BONUS(),
    HEIGHT(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    WEIGHT(null, "", false, 0, "units", "chars", "perks", "bf obj", "items"),
    TOTAL_WEIGHT("Total weight", "", true, 0, "units", "chars", "perks"),
    C_CARRYING_WEIGHT("Carry weight", "", true, 0, "units", "chars", "perks"),
    CARRYING_CAPACITY("Max weight", "", false, 25, "units", "chars", "perks"),

    DIVINATION_CAP(null, "", false, 0, "chars"),
    MEMORIZATION_CAP(null, "", false, 4, "chars"),
    MEMORY_REMAINING(null, "", false, 4, "chars"),

    ENCHANTMENT_CAPACITY(null, "MAGIC_AFFINITY", false, 0, "jewelry"),

    DETECTION(null, "DETECTION", false, 10, "bf obj", "units", "chars", "perks"),
    STEALTH(null, "STEALTH", false, 0, "bf obj", "units", "chars", "perks"),
    CONCEALMENT(null, null, false, 0, "terrain", "bf obj", "units", "chars", "perks"),
    ILLUMINATION(null, null, true, 0, "terrain", "bf obj", "units", "chars", "perks", "classes"),
    LIGHT_EMISSION(null, null, false, 0, "terrain", "bf obj", "units", "chars", "perks", "classes"),

    GLOBAL_CONCEALMENT(null, null, false, 0, "dungeons"),
    GLOBAL_ILLUMINATION(null, null, false, 0, "dungeons"),
    LIGHT_EMISSION_MODIFIER(null, null, false, 0, "dungeons"),

    PERCEPTION(null, null, false, 20, "bf obj", "units", "chars", "perks"),
    NOISE(null, null, false, 50, "action", "spell", "bf obj", "units", "chars", "perks"),
    REVEALMENT(null, null, false, 0, "action", "spell", "bf obj", "units", "chars", "perks"),

    SIGHT_RANGE(null, "Sight Range", false, 0, "bf obj", "units", "chars", "perks"),
    BEHIND_SIGHT_BONUS("Behind Sight", "BEHIND_SIGHT_BONUS", false, 0, "bf obj", "units", "chars", "perks"),
    SIDE_SIGHT_PENALTY("Sidesight pen.", "SIDE_SIGHT_PENALTY", false, 0, "bf obj", "units", "chars", "perks"),
    // buff
    SIGHT_RANGE_EXPANSION(null, "", false, 200, "units", "chars", "perks"),
    SIGHT_RANGE_EXPANSION_SIDES(null, "", false, 150, "units", "chars", "perks"),
    SIGHT_RANGE_EXPANSION_BACKWARD(null, "", false, 200, "units", "chars", "perks"),
    INTERRUPT_DAMAGE(null, "INTERRUPT_DAMAGE", false, 100, "units", "chars", "perks", "actions"),
    ENERGY(null, "", false, 0, C_OBJ_TYPE.ITEMS),
    C_ENERGY(null, "", true, 0, C_OBJ_TYPE.ITEMS),
    // ENERGY("bf obj", "ENERGY", "ENERGY", false, 100),
    ENERGY_REGEN("bf obj", "ENERGY_REGEN", "ENERGY_REGEN", false, 5),
    // C_ENERGY("bf obj", "C_ENERGY", "C_ENERGY", true, 0),
    // ITEM
    // ++per-group values, not just obj_type - weapons/armor e.g.

    GOLD_COST(null, "Cost in Gold", false, 50, C_OBJ_TYPE.ITEMS),
    GOLD(null, " Gold", true, 0, "chars", "shop"),
    GOLD_TOTAL(null, " Gold", false, 0, "chars", "mission", "shop", "bf obj"),
    GOLD_MOD(true, null, "Gold mod", false, 100, "units", "chars", "perks", "shop"),
    GOLD_PER_LEVEL(null, "Gold per level", false, 100, "units", "chars", "perks"),
    GOLD_COST_REDUCTION(null, "Gold cost REDUCTION", false, 0, "units", "chars", "perks"),
    // ++ separately for buy/sell and per main types

    // MECHANISMS
    // TODO separate move and attack and spell penalties!

    CLAIMED_CRYSTALS("chars", "CRYSTALS_CLAIMED", "CRYSTALS_CLAIMED", true, 0),
    CLAIMED_GATEWAYS("chars", "CRYSTALS_CLAIMED", "CRYSTALS_CLAIMED", true, 0),

    PIERCING_RESISTANCE("Piercing", "", false, 0, "units", "chars", "perks", "bf obj"),
    BLUDGEONING_RESISTANCE("Bludgeoning", "", false, 0, "units", "chars", "perks", "bf obj"),
    SLASHING_RESISTANCE("Slashing", "", false, 0, "units", "chars", "perks", "bf obj"),

    FIRE_RESISTANCE("Fire", "", false, 0, "units", "chars", "perks", "bf obj"),
    COLD_RESISTANCE("Cold", "", false, 0, "units", "chars", "perks", "bf obj"),
    ACID_RESISTANCE("Acid", "", false, 0, "units", "chars", "perks", "bf obj"),
    LIGHTNING_RESISTANCE("Lightning", "", false, 0, "units", "chars", "perks", "bf obj"),
    LIGHT_RESISTANCE("Light", "", false, 0, "units", "chars", "perks", "bf obj"),
    SONIC_RESISTANCE("Sonic", "", false, 0, "units", "chars", "perks", "bf obj"), // gust
    // of
    // wind
    // -
    // physical?
    // how to split best? Perhaps into 2... Natural vs Supernatural!
    //

    CHAOS_RESISTANCE("Chaos", "", false, 0, "units", "chars", "perks", "bf obj"),
    HOLY_RESISTANCE("Holy", "", false, 0, "units", "chars", "perks", "bf obj"),
    SHADOW_RESISTANCE("Shadow", "", false, 0, "units", "chars", "perks", "bf obj"),
    ARCANE_RESISTANCE("Arcane", "", false, 0, "units", "chars", "perks", "bf obj"),
    POISON_RESISTANCE("Poison", "", false, 0, "units", "chars", "perks", "bf obj"),
    DEATH_RESISTANCE("Death", "", false, 0, "units", "chars", "perks", "bf obj"),
    PSIONIC_RESISTANCE("Psionic", "", false, 0, "units", "chars", "perks", "bf obj"),

    QUANTITY(null, "", false, 1, "units"),

    // C_N_OF_ATTACKS("Number of Attacks", "", true, 2, "units", "chars", "perks"),
    // C_N_OF_MOVES("Number of Attacks", "", true, 2, "units", "chars", "perks"),

    REST_BONUS(null, "", false, 0, "units", "chars", "perks", "classes"),
    MEDITATION_BONUS(null, "", false, 0, "units", "chars", "perks", "classes"),
    CONCENTRATION_BONUS("", "", false, 0, "units", "chars", "perks", "classes"),
    REST_MOD(true, null, "", false, 0, "units", "chars", "perks", "classes"),
    MEDITATION_MOD(true, null, "", false, 0, "units", "chars", "perks", "classes"),
    CONCENTRATION_MOD(true, "", "", false, 0, "units", "chars", "perks", "classes"),
    // ALERT_BONUS("", "", false, 0, "units", "chars", "perks"),
    // DEFEND_BONUS("", "", false, 0, "units", "chars", "perks"),
    // REST_BONUS("", "", false, 0, "units", "chars", "perks"),
    // MEDITATION_BONUS("", "", false, 0, "units", "chars", "perks"),
    // CONCENTRATION_BONUS("", "", false, 0, "units", "chars", "perks"),
    // ALERT_BONUS("", "", false, 0, "units", "chars", "perks"),
    // DEFEND_BONUS("", "", false, 0, "units", "chars", "perks"),

    // PARAMS.ENCHANTER_MASTERY,
    // PARAMS.JEWELER_MASTERY,
    // LORE_MASTERY, PHILOSOPHY_MASTERY

    INTEGRITY(null, "", false, 100, "chars"),
    // INTEGRITY_MOD_DEEDS(null, "", true, 0, "chars"),
    // INTIMIDATION,
    // DIPLOMACY,
    // BLUFF,

    TACTICS_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    LEADERSHIP_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    MARKSMANSHIP_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    ITEM_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DETECTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    STEALTH_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    ATHLETICS_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    MOBILITY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    BLADE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    BLUNT_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    AXE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    POLEARM_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    UNARMED_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    TWO_HANDED_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DUAL_WIELDING_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    ARMORER_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DEFENSE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SHIELD_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    MEDITATION_MASTERY(null, Descriptions.MEDITATION_MASTERY, false, 0, "units", "chars", "perks", "classes"),
    DISCIPLINE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    WIZARDRY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SPELLCRAFT_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DIVINATION_MASTERY(null, Descriptions.DIVINATION_MASTERY, false, 0, "units", "chars", "perks", "classes"),
    WARCRY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    SORCERY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    ENCHANTMENT_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    CONJURATION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    CELESTIAL_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    BENEDICTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    REDEMPTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    FIRE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    AIR_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    WATER_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    EARTH_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    TRANSMUTATION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SYLVAN_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SAVAGE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    DEMONOLOGY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    WARP_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DESTRUCTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    AFFLICTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    BLOOD_MAGIC_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    NECROMANCY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    PSYCHIC_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    WITCHERY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SHADOW_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),



    ATTR_POINTS("Attribute points", "Attribute points", true, 0, "units", "chars", "perks"),
    ATTR_POINTS_PER_LEVEL("Attribute points", "Attribute points", false, 0, "units", "chars", "perks"),
    MASTERY_RANKS_PER_LEVEL("MASTERY points", "MASTERY points", false, 0, "units", "chars", "perks"),
    MASTERY_POINTS("MASTERY points", "MASTERY points", true, 0, "units", "chars", "perks"),
    // TRADING?

    // SPELL
    AI_PRIORITY(null, "", false, 0, "actions"),
    CIRCLE("Circle", "", false, 1, "classes"),
    FORMULA("Formula", "", false, 0, "actions", "classes", "items", "perks"),
    // CHANNELING("spells", "CHANNELING", "", false, 0),
    // CHANNELING_ESS_COST("spells", "CHANNELING", "", false, 0),
    // CHANNELING_FOC_COST("spells", "CHANNELING", "", false, 0),
    // CHANNELING_STA_COST("spells", "CHANNELING", "", false, 0),
    // CHANNELING_END_COST("spells", "CHANNELING", "", false, 0),
    DIVINATION_MAX_SD_MOD(true, "Divination max sd mod", "", false, 100, "chars"),
    DIVINATION_POOL_MOD(true, null, "", false, 100, "chars"),
    DIVINATION_CROP_FIRST(null, "", false, 0, "chars"),
    DIVINATION_CROP_LAST(null, "", false, 0, "chars"),
    DIVINATION_USE_FIRST(null, "", false, 0, "chars"),
    DIVINATION_USE_LAST(null, "", false, 0, "chars"),

    SPELL_DIFFICULTY("spells", "Difficulty", "Difficulty", false, 10),
    ENERGY_COST("Energy Cost", " Cost in Energy", false, 0, "actions"),

    AP_COST("ATB Cost", " Cost in ATB", false, 1, "actions", "items"),
    ESS_COST("ESS Cost", " Cost in Essence", false, 0, "actions", "items"),
    FOC_COST("FOC Cost", " Cost in Focus", false, 0, "actions", "items"),
    TOU_COST("STA Cost", " Cost in Stamina", false, 0, "actions", "items"),
    ENDURANCE_COST("END Cost", " Cost in Endurance", false, 0, "actions", "items"),
    SF_COST("Soulforce Cost", " Cost in Soulforce", false, 0, "actions", "items"),
    ATK_PTS_COST("CP Cost", "Cost in Counter Points", false, 0, "actions"),
    MOVE_PTS_COST("CP Cost", "Cost in Counter Points", false, 0, "actions"),

    FOC_REQ("Focus Req.", " Focus Requirement", false, 0, "actions", "items"),
    RESISTANCE_MOD(true, null, " RESISTANCE_MOD", false, 100, "actions"),

    DAMAGE_MOD(true, null, " DAMAGE_MOD", false, 100, "actions", "units", "chars", "perks"),
    ATTACK_MOD(true, null, " ATTACK_MOD", false, 100, "actions", "units", "chars", "perks"),

    // TODO ALL DC_FORMULA-REQUIRED PARAMS ABOVE THIS LINE!!!

    OFFHAND_ATTACK_MOD(true, null, " OFF_HAND_ATTACK_MOD", false, DC_Formulas.getOffhandAttackMod(), "units", "chars", "perks"),
    OFFHAND_DAMAGE_MOD(true, null, " OFF_HAND_DAMAGE_MOD", false, DC_Formulas.getOffhandDamageMod(), "units", "chars", "perks"),

    THROW_ATTACK_MOD(true, null, " THROW_ATTACK_MOD", false, 100, "units", "chars", "perks"),

    THROW_DAMAGE_MOD(true, null, " THROW_DAMAGE_MOD", false, 100, "units", "chars", "perks"),

    DEFENSE_MOD(true, null, " DEFENSE_MOD", false, 100, "actions", "units", "chars", "perks"),
    DAMAGE_BONUS(null, " BONUS_DAMAGE", false, 0, "actions"),
    ATTACK_BONUS(null, " ATTACK_BONUS", false, 0, "actions", "units", "chars", "perks"),
    DEFENSE_BONUS(null, " DEFENSE_BONUS", false, 0, "actions"),

    SPELLPOWER_BONUS(null, " SPELLPOWER_BONUS", false, 0, "items"),
    SPELLPOWER_MOD(true, null, " SPELLPOWER_MOD", false, 100, "items"),

    BLEEDING_MOD(true, null, "", false, 0, "actions", "units", "chars", "perks"),

    FORCE(null, "", false, 0, "actions"),
    FORCE_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    FORCE_KNOCK_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    FORCE_PUSH_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    FORCE_PROTECTION(null, "", false, 0, "units", "chars", "perks"),
    FORCE_DAMAGE_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    FORCE_SPELL_DAMAGE_MOD(true, null, "", false, 100, "units", "chars", "perks"),
    FORCE_MOD_SOURCE_WEIGHT(null, "", false, 100, "actions", "units", "chars", "perks"),
    FORCE_MOD_WEAPON_WEIGHT(null, "", false, 100, "actions"),
    FORCE_MAX_STRENGTH_MOD(true, null, "", false, 4, "actions"),
    // FORCE_CUSTOM_FORMULA(null, "", false, 0, "actions"),
    FORCE_SPELLPOWER_MOD(true, null, "", false, 100, "actions"),


    SIDE_DAMAGE_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    DIAGONAL_DAMAGE_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    SIDE_ATTACK_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    DIAGONAL_ATTACK_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    LONG_REACH_ATTACK_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    LONG_REACH_DAMAGE_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    CLOSE_QUARTERS_DAMAGE_MOD(true, null, "Close Quarters Damage mod", false, 100, "actions", "units", "chars", "perks"),
    CLOSE_QUARTERS_ATTACK_MOD(true, null, "Close Quarters Attack mod", false, 100, "actions", "units", "chars", "perks"),


    STOP_DISENGAGEMENT_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    PASS_DISENGAGEMENT_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    STOP_ENGAGEMENT_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    PASS_ENGAGEMENT_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    STOP_FLIGHT_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    PASS_FLIGHT_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    STOP_PASSAGE_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    PASS_PASSAGE_CHANCE_MOD(true, null, null, false, 0, "units", "chars", "perks"),

    WATCH_DEFENSE_MOD(true, null, null, false, 100, "units", "chars", "perks"),
    WATCH_AP_PENALTY_MOD(true, null, null, false, 100, "units", "chars", "perks"),
    WATCH_ATTACK_MOD(true, null, null, false, 100, "units", "chars", "perks"),
    WATCH_ATTACK_OTHERS_MOD(true, null, null, false, 100, "units", "chars", "perks"),
    WATCH_DEFENSE_OTHERS_MOD(true, null, null, false, 100, "units", "chars", "perks"),

    WATCHED_ATTACK_MOD(true, null, null, false, 100, "units", "chars", "perks"),
    WATCH_DETECTION_MOD(true, null, null, false, 0, "units", "chars", "perks"),

    INSTANT_DAMAGE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    INSTANT_ATTACK_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    INSTANT_DEFENSE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),

    AOO_DAMAGE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    AOO_ATTACK_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    AOO_DEFENSE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),

    // COUNTER_DAMAGE_MOD(true,null, null, false, 0, "units", "chars", "perks"),
    COUNTER_MOD(true, null, "", false, 100, "actions", "units", "chars", "perks"),
    COUNTER_ATTACK_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    COUNTER_DEFENSE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),

    AUTO_CRIT_CHANCE(null, null, false, 0, "actions", "units", "chars", "perks"),

    SNEAK_PROTECTION(null, null, false, 100, "units", "chars", "perks"),

    SNEAK_RANGED_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    SNEAK_DAMAGE_MOD(true, null, " DAMAGE_MOD", false, 100, "actions", "units", "chars", "perks"),
    SNEAK_ATTACK_MOD(true, null, " ATTACK_MOD", false, 100, "actions", "units", "chars", "perks"),
    SNEAK_DAMAGE_BONUS(null, " BONUS_DAMAGE", false, 0, "actions", "units", "chars", "perks"),
    SNEAK_ATTACK_BONUS(null, " ATTACK_BONUS", false, 0, "actions", "units", "chars", "perks"),

    SNEAK_DEFENSE_PENETRATION(null, "SNEAK_DEFENSE_PENETRATION", false, 0, "actions", "units", "chars", "perks"),
    SNEAK_ARMOR_PENETRATION(null, "SNEAK_DEFENSE_PENETRATION", false, 0, "actions", "units", "chars", "perks"),

    // UNIT
    IMPACT_AREA(null, "", false, 0, "actions"),
    RANGE("Range ", "Maximum distance", false, 1,
            // "units",
            "spells",
            // "chars",
            "actions", "items"),
    AUTO_ATTACK_RANGE(null, "", false, 0, "actions"),

    CADENCE_FOCUS_BOOST(null, "", false, 0, "actions", "units", "chars", "perks"),
    CADENCE_BONUS(null, "", false, 0),
    CADENCE_RETAINMENT_CHANCE(null, "", false, 0, "actions", "units", "chars", "perks"),
    CADENCE_TOU_MOD(true, null, "", false, DC_Formulas.DEFAULT_CADENCE_TOU_MOD, "units", "chars", "perks"),
    CADENCE_AP_MOD(true, null, "", false, DC_Formulas.DEFAULT_CADENCE_AP_MOD, "units", "chars", "perks"),
    CADENCE_DAMAGE_MOD(true, null, "", false, 0, "units", "chars", "perks"),
    CADENCE_DEFENSE_MOD(true, null, "", false, 0, "units", "chars", "perks"),
    CADENCE_ATTACK_MOD(true, null, "", false, 0, "units", "chars", "perks"),

    //TODO atb perc?
    DURABILITY_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE),
    ENDURANCE_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    TOUGHNESS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    ESSENCE_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    FOCUS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    N_OF_ACTIONS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    N_OF_COUNTERS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),

    TOUGHNESS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    ESSENCE_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    FOCUS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    ATB_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    ATK_PTS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),

    EXTRA_ATTACKS_POINT_COST_MOD(true, "Extra Attacks Cost Mod", "", false, 50, "units", "chars", "perks"),
    INSTANT_TOUGHNESS_COST_MOD(null, "", false, -50, "units", "chars", "perks"),
    INSTANT_PTS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    AOO_TOUGHNESS_COST_MOD(null, "", false, -50, "units", "chars", "perks"),
    AOO_PTS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    COUNTER_TOUGHNESS_COST_MOD(null, "", false, -50, "units", "chars", "perks"),
    COUNTER_PTS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),

    SPELL_TOU_COST_MOD("Spell STA pen.", "", false, 0, "units", "chars", "perks"),
    SPELL_ESS_COST_MOD("Spell ESS pen.", "", false, 0, "units", "chars", "perks"),
    SPELL_FOC_COST_MOD("Spell FOC pen.", "", false, 0, "units", "chars", "perks"),
    SPELL_ATB_COST_MOD("Spell AP pen.", "", false, 0, "units", "chars", "perks"),

    ATTACK_TOUGHNESS_COST_MOD("Attack STA pen.", "", false, 0, "units", "chars", "perks"),
    ATTACK_ATB_COST_MOD("Attack AP pen.", "", false, 0, "units", "chars", "perks"),

    OFFHAND_ATTACK_TOUGHNESS_COST_MOD(null, "", false, 0, "units", "chars", "perks"),
    OFFHAND_ATTACK_ATB_COST_MOD(null, "", false, 0, "units", "chars", "perks"),

    MOVE_TOU_COST_MOD("Move STA pen.", "", false, 0, "units", "chars", "perks"),
    MOVE_ATB_COST_MOD("Move AP pen.", "", false, 0, "units", "chars", "perks"),

    EVASION(null, "", false, 0, "units", "chars", "perks"),
    ACCURACY(null, "", false, 0, "actions", "units", "chars", "perks"),

    VIGILANCE_MOD(true, null, "", false, 0, "units", "chars", "perks"),

    BLOCK_CHANCE(null, "", false, 0, "units", "chars", "perks"),
    BLOCK_CHANCE_BONUS(null, "", false, 0, "units", "chars"),
    BLOCK_CHANCE_BONUS_SHIELD(null, "", false, 0, "units", "chars"),
    BLOCK_CHANCE_BONUS_PARRY(null, "", false, 0, "units", "chars"),

    BLOCK_PENETRATION(null, "", false, 0, "units", "chars", "perks"),
    PARRY_PENETRATION(null, "", false, 0, "units", "chars", "perks"),
    CRITICAL_REDUCTION("Crit. reduction", "", false, 0, "units", "chars", "perks", "bf obj"),
    CRITICAL_MOD(true, "Crit. modifier", "", false, 0, "actions", "units", "chars", "perks", "bf obj"),


    RANGED_PENALTY_MOD(true, "", "", false, 100, "units", "chars", "perks"),


    DURATION_BONUS(null, "", false, 0, "classes", "jewelry", "units", "chars", "perks"),
    DURATION_MOD(true, null, "", false, 0, "classes", "jewelry", "units", "chars", "perks"),

    ADJUST_COEF("encounters"),
    GROUP_NUMBER("encounters"),
    MAX_GROUP_NUMBER("encounters"),
    MIN_GROUP_NUMBER("encounters"),
    POWER_LEVEL(null, "", false, 0, "encounters"),
    UNIT_NUMBER("encounters"),
    MAX_UNIT_PER_GROUP("encounters"),

    DURABILITY_SELF_DAMAGE_MOD(true, null, "", false, 100),
    DURABILITY_DAMAGE_MOD(true, null, "", false, 100, "actions"),

    MAX_BUFF_STACKS(null, "", false, 0, "buffs"),
    BUFF_STACKS(null, "", true, 0, "buffs"),
    SUMMONED_POWER_MOD(true, null, "", false, 100, "units", "chars", "perks", "classes"),
    UPKEEP_MOD(true, null, "", false, 100, "units", "chars", "perks", "classes"),
    QUICK_ITEM_GOLD_PERCENTAGE(null, "", false, 0, "units", "chars", "perks"),
    JEWELRY_GOLD_PERCENTAGE(null, "", false, 0, "units", "chars", "perks"),
    MAIN_HAND_GOLD_PERCENTAGE(null, "", false, 70, "units", "chars", "perks"),
    ARMOR_GOLD_PERCENTAGE(null, "", false, 40, "units", "chars", "perks"),

    CLEAVE_MAX_TARGETS(null, "", false, 0, "units", "chars", "perks"),
    CLEAVE_DAMAGE_PERCENTAGE_TRANSFER(null, "", false, 0, "units", "chars", "perks"),
    CLEAVE_DAMAGE_LOSS_PER_JUMP(null, "", false, 0, "units", "chars", "perks"),

    FEAR_RESISTANCE(null, "", false, 0, "units", "chars", "perks"),
    BLEEDING_RESISTANCE(null, "", false, 0, "units", "chars", "perks"),
    INTERRUPT_RESISTANCE(null, "", false, 0, "units", "chars", "perks"),
    BASH_RESISTANCE(null, "", false, 0, "units", "chars", "perks"),
    INJURY_RESISTANCE(null, "", false, 0, "units", "chars", "perks"),
    WOUNDS_RESISTANCE(null, "", false, 0, "units", "chars", "perks"),

    SPIRIT_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    GRIT_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    REFLEX_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    WIT_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    FORTITUDE_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),

    SPIRIT_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    GRIT_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    REFLEX_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    WIT_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    FORTITUDE_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),

    FORCE_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    FORCE_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    DETECTION_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    PERCEPTION_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    PERCEPTION_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    DETECTION_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    LUCK_ROLL_SAVE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),
    LUCK_ROLL_BEAT_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),

    SINGLE_HAND_ATTACK_BONUS_MOD(true, null, "", false, 0, "classes", "units", "chars", "perks"),
    SINGLE_HAND_DEFENSE_BONUS_MOD(true, null, "", false, 0, "classes", "units", "chars", "perks"),
    SINGLE_HAND_DAMAGE_BONUS_MOD(true, null, "", false, 0, "classes", "units", "chars", "perks"),
    THROW_SIZE_BONUS(null, "", false, 0, "classes", "units", "chars", "perks"),

    COATING_COUNTERS_APPLIED_PER_HIT_MOD(true, null, "", false, 0, "units", "chars", "perks"),
    COATING_COUNTERS_APPLIED_TO_ITEM_MOD(true, null, "", false, 0, "units", "chars", "perks"),
    COATING_COUNTERS_SPENT_MOD(true, null, "", false, 0, "units", "chars", "perks"),

    ARCADE_LEVEL(null, "", false, 0, "party"),
    LEVEL("LEVEL", "", false, 1, "units", "chars", "perks", "party", "arcades"),
    MIN_LEVEL(null, "", false, 0, "mission"),
    MAX_LEVEL(null, "", false, 0, "mission"),
    DIFFICULTY_MOD(true, null, "", false, 100, "mission"),
    UNIT_LEVEL("LEVEL", "", false, 0, "units"),
    HERO_LEVEL("HERO_LEVEL", "", false, 0, "chars"),

    MAX_SPELL_UPGRADES(null, "", false, 4),
    LOCK_LEVEL(null, "", false, 0, "bf obj"),
    PICK_LOCK(null, "", false, 0, "classes", "units", "chars", "perks"),
    TRAP_LEVEL(null, "", false, 0, "bf obj", "trap"),
    DISARM_TRAP(null, "", false, 0, "classes", "units", "chars", "perks"),

    AUTO_TEST_ID("", "", false, 0, "actions", "classes"),
    //    HT_CUSTOM_POS_X("", "", false, 0, "classes"),
    //    HT_CUSTOM_POS_Y("", "", false, 0, "classes"),
    ANIM_FRAME_DURATION("", "", false, 0, "actions"),
    ANIM_SPEED("", "", false, 0, "actions"),


    SOULFORCE(null, "", false, 100, "lord", "party"), //max
    C_SOULFORCE(null, "", true, 0, "lord", "party"),
    BASE_SOULFORCE(null, "", false, 25, "lord", "party"),
/*
could have other params - sf discounts,
 */

    SELF_BUFF_MOD(null, "", false, 60, "chars", "units"),
    DEBT_MOD(null, "", false, 0, "chars", "units"),
    INTEREST_MOD(null, "", false, 0, "chars", "units"),

    SUMMON_ATB(null, "", false, 0), GRID_WIDTH(null, "", false, 1, "boss"), GRID_HEIGHT(null, "", false, 1, "boss"),
    FREE_MOVE_BONUS(null, "", false, 0, "units", "chars"),

    //TODO
    SWITCH_AS_FOCUS_COST_MOD(),
    SWITCH_AS_ATB_COST_MOD(),

    MASTERY_RANKS,
    MASTERY_RANKS_UNSPENT,
    CLASS_RANKS,
    CLASS_RANKS_UNSPENT,
    SPELL_POINTS,
    SPELL_POINTS_UNSPENT,
    SKILL_POINTS,
    SKILL_POINTS_UNSPENT,

    WEIGHT_PENALTY_REDUCTION(),
    DEITY_EFFECTS_MOD,
    MASTERY_SCORE_MOD();

    boolean writeToType;
    Color color;
    INPUT_REQ inputReq;
    private final String name;
    private String shortName;
    private String descr;
    private String entityType;
    private String[] entityTypes;
    private String[] sentityTypes;
    private int defaultValue;
    private boolean dynamic = false;
    private final boolean lowPriority = false;
    private boolean attr = false;
    private boolean superLowPriority;
    private boolean mastery;
    private boolean highPriority;
    private String fullName;
    private String iconPath;
    private Map<OBJ_TYPE, Object> defaultValuesMap;
    private boolean mod;
    private boolean devOnly;

    PARAMS(String shortName, String descr, boolean dynamic, int defaultValue, C_OBJ_TYPE type,
           OBJ_TYPE... types) {
        this(type.getTypes()[0].getName(), shortName, descr, dynamic, defaultValue,
                Integer.MAX_VALUE);
        String[] ENTITY_TYPES = new String[type.getTypes().length + types.length];
        int i = 0;
        for (OBJ_TYPE t : type.getTypes()) {
            ENTITY_TYPES[i] = t.getName();
            i++;
        }
        for (OBJ_TYPE t : types) {
            ENTITY_TYPES[i] = t.getName();
            i++;
        }
        this.entityTypes = ENTITY_TYPES;
    }

    PARAMS(boolean mod, String shortName, String descr, boolean dynamic, int defaultValue,
           String... entityTypes) {
        this(shortName, descr, dynamic, defaultValue, entityTypes);
        this.mod = mod;
    }

    PARAMS(boolean mod, String shortName, String descr, boolean dynamic, int defaultValue) {
        this(shortName, descr, dynamic, defaultValue);
        this.mod = mod;
    }

    PARAMS(String shortName, String descr, boolean dynamic, int defaultValue) {
        this(null, shortName, descr, dynamic, defaultValue);
    }

    PARAMS(String shortName, String descr, boolean dynamic, int defaultValue, String... entityTypes) {
        this(entityTypes[0], shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
        this.entityTypes = entityTypes;
    }

    PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue,
           Color c) {
        this(entityType, shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
        Metainfo metainfo = new Metainfo(c);
    }

    PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue) {
        this(entityType, shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
    }

    PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue,
           int AV_ID) {
        this.name = StringMaster.format(name());
        this.fullName = name();
        if (shortName == null) {
            setShortName(name);
        } else {
            this.setShortName(shortName);
        }
        this.descr = descr;
        this.entityType = entityType;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
    }

    PARAMS() {
        this.name = StringMaster.format(name());
    }

    PARAMS(boolean attr) {
        this();
        this.entityType = "chars";

    }

    PARAMS(String str) {
        this(str, null, "", false, 0, Integer.MAX_VALUE);
    }

    PARAMS(boolean avOnly, String... types) {
        this(null, "", false, 0, types);
        this.devOnly = avOnly;
    }

    @Override
    public INPUT_REQ getInputReq() {
        return inputReq;
    }

    public Map<OBJ_TYPE, Object> getDefaultValuesMap() {
        if (defaultValuesMap == null) {
            defaultValuesMap = new HashMap<>();
        }
        return defaultValuesMap;
    }

    public void initNonMasteryDescription() {
        setDescr(DescriptionMaster.getNonMasteryDescription(this));
    }

    public void initMastery() {
        setMastery(true);
        setDescr(DescriptionMaster.getMasteryDescription(this));
    }

    // getOrCreate(base)

    @Override
    public String getIconPath() {
        return iconPath;
    }

    @Override
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayedName() {
        return DescriptionTooltips.getDisplayedName(this);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the entityTypes
     */

    public String[] getEntityTypes() {
        return entityTypes;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getDescription() {
        return descr;
    }

    @Override
    public String getEntityType() {

        return entityType;
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(defaultValue);
    }

    @Override
    public boolean isMastery() {
        return this.mastery;
    }

    public void setMastery(boolean mastery) {
        this.mastery = mastery;
        this.shortName = shortName.replace("Mastery", "");
    }

    @Override
    public boolean isAttribute() {
        return attr;
    }

    public void setAttr(boolean attr) {
        this.attr = attr;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public boolean isWriteToType() {
        return writeToType;
    }

    @Override
    public void setWriteToType(boolean writeToType) {
        this.writeToType = writeToType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public boolean isMod() {
        return mod;
    }

    public void setMod(boolean mod) {
        this.mod = mod;
    }

    @Override
    public void setDevOnly(boolean devOnly) {
        this.devOnly = devOnly;
    }

    @Override
    public boolean isDevOnly() {
        return devOnly;
    }
}
