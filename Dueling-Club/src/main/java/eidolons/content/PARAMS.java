package eidolons.content;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.rules.round.UnconsciousRule;
import eidolons.system.DC_Formulas;
import eidolons.system.math.DC_MathManager;
import eidolons.system.text.TextMaster;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.content.text.Descriptions;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;
import main.system.graphics.ColorManager;
import main.system.math.MathMaster;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

//Quantative properties of ..*what?*
public enum PARAMS implements PARAMETER {
    MAX_DEPTH(null, "", false, 3, "dungeons"),
    MIN_DEPTH(null, "", false, 1, "dungeons"),
    POWER_MOD(true, null, "", false, 100, "dungeons", "encounters"),
    POWER_MINIMUM(null, "", false, 0, "dungeons", "encounters"),
    POWER_BASE(null, "", false, 0, "dungeons", "encounters"),
    POWER_MAXIMUM(null, "", false, 0, "dungeons", "encounters"),
    SPAWNING_DELAY_MOD(true, null, "", false, 100, "dungeons", "encounters"),
    SPAWNING_DELAY_BONUS(null, "", false, 0, "encounters"),
    FREE_MASTERIES(null, "", false, DC_MathManager.DEFAULT_FREE_MASTERY_COUNT, "chars"),
    GLORY("party"),
    MAX_HEROES(null, "", false, 0, "party", "arcades"),
    ORDER_CHANCE_MOD(null, "", false, 100, "units", "chars", "perks"),
    ORGANIZATION(null, "", true, 100, "units", "chars", "perks", "party"),
    BATTLE_SPIRIT(null, "", true, 100, "units", "chars", "perks", "party"),
    PRINCIPLE_CLASHES(null, "", true, 0, "units", "chars", "perks", "party"),
    PRINCIPLE_CLASHES_REDUCTION(null, "", true, 0, "units", "chars", "perks", "party"),
    WAR_ALIGNMENT(StringMaster.getWellFormattedString("WAR_ALIGNMENT"), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    PEACE_ALIGNMENT(StringMaster.getWellFormattedString("PEACE AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    HONOR_ALIGNMENT(StringMaster.getWellFormattedString("HONOR AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    TREACHERY_ALIGNMENT(StringMaster.getWellFormattedString("TREACHERY AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    LAW_ALIGNMENT(StringMaster.getWellFormattedString("LAW_ALIGNMENT"), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    FREEDOM_ALIGNMENT(StringMaster.getWellFormattedString("FREEDOM AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    CHARITY_ALIGNMENT(StringMaster.getWellFormattedString("CHARITY AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    AMBITION_ALIGNMENT(StringMaster.getWellFormattedString("AMBITION AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    TRADITION_ALIGNMENT(StringMaster.getWellFormattedString("TRADITION AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    PROGRESS_ALIGNMENT(StringMaster.getWellFormattedString("PROGRESS AL."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),

    WAR_IDENTITY(StringMaster.getWellFormattedString("WAR_IDENTITY"), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    PEACE_IDENTITY(StringMaster.getWellFormattedString("PEACE id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    HONOR_IDENTITY(StringMaster.getWellFormattedString("HONOR id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    TREACHERY_IDENTITY(StringMaster.getWellFormattedString("TREACHERY id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    LAW_IDENTITY(StringMaster.getWellFormattedString("LAW_IDENTITY"), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    FREEDOM_IDENTITY(StringMaster.getWellFormattedString("FREEDOM id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    CHARITY_IDENTITY(StringMaster.getWellFormattedString("CHARITY id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    AMBITION_IDENTITY(StringMaster.getWellFormattedString("AMBITION id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    TRADITION_IDENTITY(StringMaster.getWellFormattedString("TRADITION id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),
    PROGRESS_IDENTITY(StringMaster.getWellFormattedString("PROGRESS id."), null, false, 0, "units", "chars", "perks", "classes", "skills", "deities"),

    IDENTITY_POINTS(null, "", true, 0, "chars", "skills", "classes", "deities"),
    STARTING_IDENTITY_POINTS(null, "", false, 3, "chars"),
    IDENTITY_POINTS_PER_LEVEL(null, "", false, 50, "chars"), // /100
    TOTAL_IDENTITY_POINTS(null, "", true, 0, "chars"),

    DEITY_CLASHES_REDUCTION(null, "", true, 0, "units", "chars", "perks", "party"),
    SHARED_PRINCIPLES_BOOST(null, "", true, 0, "units", "chars", "perks", "party"),
    SHARED_DEITIES_BOOST(null, "", true, 0, "units", "chars", "perks", "party"),
    PRINCIPLE_CLASHES_REMOVED(null, "", true, 0, "units", "chars", "perks", "party"),
    // comradeship, competition, love between each 2 heroes?
    // affection, esteem, towards leader

    DUNGEON_LEVEL("dungeons"),
    GLORY_REWARD("dungeons"),
    GOLD_REWARD("dungeons"),
    XP_REWARD("dungeons"),
    LOOT_VALUE("dungeons"),
    BF_WIDTH("dungeons"),
    BF_HEIGHT("dungeons"),

    STR_DMG_MODIFIER("Str dmg mod", "", false, 0, "actions", "weapons", "jewelry"), // HEAVY/MELEE
    AGI_DMG_MODIFIER("Agi dmg mod", "", false, 0, "actions", "weapons", "jewelry"), // LIGHT/RANGED
    SP_DMG_MODIFIER("Sp dmg mod", "", false, 0, "actions", "weapons", "jewelry"), // WANDS
    INT_DMG_MODIFIER("Int dmg mod", "", false, 0, "actions", "weapons", "jewelry"), // MECHANISMS
    MATERIAL_QUANTITY("Material pieces", "", false, 4, "armor", "weapons", "jewelry"), // MECHANISMS

    DAMAGE_TOTAL(null, "", true, 0, "spells", "actions", "units", "chars", "perks", "weapons"),
    DAMAGE_LAST_AMOUNT(null, "", true, 0, "spells", "actions", "units", "chars", "perks", "weapons"),
    DAMAGE_LAST_DEALT(null, "", true, 0, "spells", "actions", "units", "chars", "perks", "weapons"),

    DURABILITY(null, "", false, 5, "armor", "weapons"),
    C_DURABILITY(null, "", true, 5, "armor", "weapons"),
    DICE(null, "", false, 1, "weapons"),
    DIE_SIZE(null, "", false, 6, "weapons"),
    BASE_DAMAGE_MODIFIER("Base dmg mod", "", false, 100, "weapons"),
    ARMOR_MODIFIER(null, "", false, 50, "armor"),
    RESISTANCE_MODIFIER("Resistance mod", "", false, 10, "armor", "weapons"),

    DURABILITY_MODIFIER("Durability mod", "", false, 100, "armor", "weapons"),
    COST_MODIFIER(null, "", false, 100, "armor", "weapons"),
    QUICK_SLOTS(null, "", false, 0, "armor", "units", "chars", "perks"),
    C_QUICK_SLOTS(null, "", true, 2, "units", "chars", "perks"),
    ITEM_COST_MOD(null, "", false, 0, "units", "chars", "perks"),

    CHARGES(null, "", false, 1, "items"),
    C_CHARGES(null, "", true, 1, "items"),
    // UNIT/CHAR

    TOUGHNESS(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    C_TOUGHNESS(null, "", true, 0, "units", "chars", "perks", "bf obj"),
    ENDURANCE(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    C_ENDURANCE(null, "", true, 0, "units", "chars", "perks", "bf obj"),
    MORALE(null, "MORALE", false, 30, "units", "chars", "perks"),
    C_MORALE(null, "", true, 30, "units", "chars", "perks"),
    STAMINA(null, "STAMINA", false, 5, "units", "chars", "perks"),
    C_STAMINA(null, "STAMINA", true, 5, "units", "chars", "perks"),
    FOCUS(null, "FOCUS", false, 100, "units", "chars", "perks"),
    C_FOCUS(null, "FOCUS", true, 0, "units", "chars", "perks"),
    ESSENCE(null, "ESSENCE", false, 0, "units", "chars", "perks"),
    C_ESSENCE(null, "ESSENCE", true, 0, "units", "chars", "perks"),
    N_OF_COUNTERS("Counter pts", "", false, 0, "units", "chars", "perks", "skills"),
    N_OF_ACTIONS("Action pts", "Maximum number of attacks unit can make per turn", false, 0, "units", "chars", "perks", "bf obj", "skills"){
        @Override
        public String getDisplayedName() {
            if (DC_Engine.isAtbMode())
                return "Initiative";
            return "Action pts";
        }
    },
    C_N_OF_ACTIONS(null, "", true, 0, "units", "chars", "perks", "bf obj"),
    C_N_OF_COUNTERS("Number of Attacks", "", true, 2, "units", "chars", "perks"),


    INITIATIVE_MODIFIER("Initiative mod", "", false, 10, "units", "chars", "perks", "bf obj"),
    INITIATIVE_BONUS("Initiative bonus", "", false, 5, "units", "chars", "perks", "bf obj"),
    C_INITIATIVE_BONUS("Initiative", "", true, 0, "units", "chars", "perks", "bf obj"),
    C_INITIATIVE_TRANSFER("Initiative", "", true, 0, "units", "chars", "perks", "bf obj"),
    C_INITIATIVE("C Initiative", "", true, 0, "units", "chars", "perks", "bf obj"){
        @Override
        public String getDisplayedName() {
            if (DC_Engine.isAtbMode())
                return "Readiness";
            return "Initiative";
        }
    },
    INITIATIVE("Initiative", "", false, 0, "units", "chars", "perks", "bf obj"),
    // INITIATIVE("Initiative", "", false, 30, "units", "chars", "perks", "bf obj"),


    STRENGTH(null, Descriptions.Strength, false, 0, "units", "chars", "perks", "jewelry", "classes"),
    VITALITY(null, Descriptions.Vitality, false, 0, "units", "chars", "perks", "jewelry", "skills", "classes"),
    AGILITY(null, Descriptions.Agility, false, 0, "units", "chars", "perks", "jewelry", "skills", "classes"),
    DEXTERITY(null, Descriptions.Dexterity, false, 0, "units", "chars", "perks", "jewelry", "skills", "classes"),
    WILLPOWER(null, Descriptions.Willpower, false, 0, "units", "chars", "perks", "jewelry", "skills", "classes"),
    INTELLIGENCE(null, Descriptions.Intelligence, false, 0, "chars", "jewelry", "units", "skills", "classes"),
    SPELLPOWER(null, Descriptions.Spellpower, false, 0, "chars", "jewelry", "units", "skills", "classes"),
    KNOWLEDGE(null, Descriptions.Knowledge, false, 0, "chars", "jewelry", "units", "skills", "classes"),
    WISDOM(null, Descriptions.Wisdom, false, 0, "units", "chars", "perks", "jewelry", "skills", "classes"),
    CHARISMA(null, Descriptions.CHARITY, false, 0, "units", "chars", "perks", "jewelry", "skills", "classes"),

    // MASTERY_REQ =
    // LEVEL_REQ

    XP_COST("Xp cost", "Experience cost", false, 0, "skills", "spells", "classes", "actions"),
    TOTAL_XP("Experience", "Experience points", false, 100, "units", "chars", "perks", "mission"),
    POWER("Power", "Power points", false, 10, "units", "chars", "perks", "bf obj"),
    XP("Experience remaining", "Experience remaining", true, 100, "units", "chars", "perks"),

    // XP_TOTAL("Experience", "Experience points", false, 0, "chars", "unit"),
    // XP_SPENT("Experience", "Experience points", true, 0, "chars", "unit"),

    BASE_STRENGTH(null, Descriptions.Strength, false, 0, "units", "chars", "perks", "skills"),
    BASE_VITALITY(null, Descriptions.Vitality, false, 0, "units", "chars", "perks", "skills"),
    BASE_AGILITY(null, Descriptions.Agility, false, 0, "units", "chars", "perks", "skills"),
    BASE_DEXTERITY(null, Descriptions.Dexterity, false, 0, "units", "chars", "perks", "skills"),
    BASE_WILLPOWER(null, Descriptions.Willpower, false, 0, "units", "chars", "perks", "skills"),
    BASE_INTELLIGENCE(null, Descriptions.Intelligence, false, 0, "units", "chars", "perks", "skills"),
    BASE_SPELLPOWER(null, Descriptions.Spellpower, false, 0, "units", "chars", "perks", "skills"),
    BASE_KNOWLEDGE(null, Descriptions.Knowledge, false, 0, "units", "chars", "perks", "skills"),
    BASE_WISDOM(null, Descriptions.Wisdom, false, 0, "units", "chars", "perks", "skills"),
    BASE_CHARISMA(null, Descriptions.CHARITY, false, 0, "units", "chars", "perks", "skills"),

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

    STAMINA_REGEN("Sta. Regen", "STAMINA_REGEN", true, 0, "units", "chars", "perks"),
    ESSENCE_REGEN("Ess. Regen", "ESSENCE_REGEN", false, 0, "units", "chars", "perks"),
    FOCUS_REGEN(null, "FOCUS_REGEN", false, 0, "units", "chars", "perks"),
    STARTING_FOCUS("Start foc", "FOCUS", false, 0, "units", "chars", "perks"),
    BASE_DAMAGE("Base Damage", "", false, 0, "units", "chars", "perks"),
    DAMAGE("Damage", "", false, 0, "units", "chars", "perks"),
    MIN_DAMAGE("Min Damage", "", false, 0, "units", "chars", "perks"),
    MAX_DAMAGE("Max Damage", "", false, 0, "units", "chars", "perks"),

    OFF_HAND_DAMAGE("Offhand Damage", "", false, 0, "units", "chars", "perks"),
    OFF_HAND_MIN_DAMAGE("Offh. Min Dmg", "", false, 0, "units", "chars", "perks"),
    OFF_HAND_MAX_DAMAGE("Offh. Max Dmg", "", false, 0, "units", "chars", "perks"),
    // ARMOR("Armor", "", false, 0, "units", "chars", "perks", "bf obj",
    // "armor"), see last, quick fixer -> ok I don't remember why I did it...
    ARMOR("Armor", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    ARMOR_LAYERS("Layers", "", false, 1, "armor"),
    COVER_PERCENTAGE("Cover perc.", "", false, 50, "armor", "weapons"),
    HARDNESS("Hardness", "", false, 0, "weapons", "armor"),
    PIERCING_ARMOR("Piercing", "", false, 0, "armor"),
    BLUDGEONING_ARMOR("Bludgeoning", "", false, 0, "armor"),
    SLASHING_ARMOR("Slashing", "", false, 0, "armor"),

    FIRE_ARMOR("Fire", "", false, 0, "armor"),
    COLD_ARMOR("Cold", "", false, 0, "armor"),
    ACID_ARMOR("Acid", "", false, 0, "armor"),
    LIGHTNING_ARMOR("Lightning", "", false, 0, "armor"),
    SONIC_ARMOR("Sonic", "", false, 0, "armor"),
    LIGHT_ARMOR("Light", "", false, 0, "armor"),

    HOLY_ARMOR("Fire", "", false, 0, "armor"),
    SHADOW_ARMOR("Cold", "", false, 0, "armor"),
    DEATH_ARMOR("Cold", "", false, 0, "armor"),
    ARCANE_ARMOR("Cold", "", false, 0, "armor"),
    CHAOS_ARMOR("Cold", "", false, 0, "armor"),
    PSIONIC_ARMOR("Cold", "", false, 0, "armor"),

    PIERCING_DURABILITY_MOD(true, "Piercing", "", false, 0, "armor"),
    BLUDGEONING_DURABILITY_MOD(true, "Bludgeoning", "", false, 0, "armor"),
    SLASHING_DURABILITY_MOD(true, "Slashing", "", false, 0, "armor"),

    FIRE_DURABILITY_MOD(true, "Fire", "", false, 0, "armor"),
    COLD_DURABILITY_MOD(true, "Cold", "", false, 0, "armor"),
    ACID_DURABILITY_MOD(true, "Acid", "", false, 0, "armor"),
    LIGHTNING_DURABILITY_MOD(true, "Lightning", "", false, 0, "armor"),
    SONIC_DURABILITY_MOD(true, "Sonic", "", false, 0, "armor"),
    LIGHT_DURABILITY_MOD(true, "Light", "", false, 0, "armor"),

    HOLY_DURABILITY_MOD(true, "Fire", "", false, 0, "armor"),
    SHADOW_DURABILITY_MOD(true, "Cold", "", false, 0, "armor"),
    DEATH_DURABILITY_MOD(true, "Cold", "", false, 0, "armor"),
    ARCANE_DURABILITY_MOD(true, "Cold", "", false, 0, "armor"),
    CHAOS_DURABILITY_MOD(true, "Cold", "", false, 0, "armor"),
    PSIONIC_DURABILITY_MOD(true, "Cold", "", false, 0, "armor"),

    SPIRIT(null, "", false, 3, "units", "chars", "perks"),
    FORTITUDE(null, "", false, 5, "units", "chars", "perks"),

    ENDURANCE_REGEN("End. Regen.", "", false, 0, "units", "chars", "perks", "bf obj"), // Color.GREEN.brighter()),
    SPELL_ARMOR(null, "", false, 0, "units", "armor", "chars", "bf obj"),
    RESISTANCE(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    DEFENSE("Defense", "", false, 0, "units", "chars", "perks", "bf obj"),
    ATTACK("Attack", "", false, 0, "units", "chars", "perks", "bf obj"),
    OFF_HAND_ATTACK("Offh. ATK", "", false, 0, "units", "chars", "perks"),
    DEFENSE_PENETRATION("Def. Pntr", "", false, 0, "chars"),
    RESISTANCE_PENETRATION("Res. Pntr", "", false, 0, "actions", "units", "chars", "perks"),
    ARMOR_PENETRATION("Armor Pntr", "", false, 0, "actions", "units", "chars", "perks", "weapons"),
    ARMOR_MOD(true, "Armor Pntr %", "", false, 100, "actions", "units", "chars", "perks", "weapons"),
    HEIGHT(null, "", false, 0, "units", "chars", "perks", "bf obj"),
    WEIGHT(null, "", false, 0, "armor", "weapons", "units", "chars", "perks", "bf obj", "items"),
    TOTAL_WEIGHT("Total weight", "", true, 0, "units", "chars", "perks"),
    C_CARRYING_WEIGHT("Carry weight", "", true, 0, "units", "chars", "perks"),
    CARRYING_CAPACITY("Max weight", "", false, 25, "units", "chars", "perks"),

    DIVINATION_CAP(null, "", false, 0, "chars"),
    MEMORIZATION_CAP(null, "", false, 4, "chars"),
    MEMORY_REMAINING(null, "", false, 4, "chars"),

    ENCHANTMENT_CAPACITY(null, "MAGIC_AFFINITY", false, 0, "weapons", "jewelry", "armor"),

    DETECTION(null, "DETECTION", false, 10, "bf obj", "units", "chars", "perks", "armor"),
    STEALTH(null, "STEALTH", false, 0, "bf obj", "units", "chars", "perks", "armor"),
    CONCEALMENT(null, null, false, 0, "terrain", "bf obj", "units", "chars", "perks", "armor"),
    ILLUMINATION(null, null, false, 0, "terrain", "bf obj", "units", "chars", "perks", "armor", "classes"),
    LIGHT_EMISSION(null, null, false, 0, "terrain", "bf obj", "units", "chars", "perks", "armor", "classes"),

    GLOBAL_CONCEALMENT(null, null, false, 0, "dungeons"),
    GLOBAL_ILLUMINATION(null, null, false, 0, "dungeons"),
    LIGHT_EMISSION_MODIFIER(null, null, false, 0, "dungeons"),

    PERCEPTION(null, null, false, 0, "bf obj", "units", "chars", "perks", "armor"),
    NOISE(null, null, false, 0, "action", "spell", "bf obj", "units", "chars", "perks", "armor"),
    REVEALMENT(null, null, false, 0, "action", "spell", "bf obj", "units", "chars", "perks", "armor"),

    SIGHT_RANGE(null, "Sight Range", false, 0, "bf obj", "units", "chars", "perks", "armor"),
    BEHIND_SIGHT_BONUS("Behind Sight", "BEHIND_SIGHT_BONUS", false, 0, "bf obj", "units", "chars", "perks"),
    SIDE_SIGHT_PENALTY("Sidesight pen.", "SIDE_SIGHT_PENALTY", false, 0, "bf obj", "units", "chars", "perks", "armor"),
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

    PIERCING_RESISTANCE("Piercing", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    BLUDGEONING_RESISTANCE("Bludgeoning", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    SLASHING_RESISTANCE("Slashing", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),

    FIRE_RESISTANCE("Fire", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    COLD_RESISTANCE("Cold", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    ACID_RESISTANCE("Acid", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    LIGHTNING_RESISTANCE("Lightning", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    LIGHT_RESISTANCE("Light", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    SONIC_RESISTANCE("Sonic", "", false, 0, "units", "chars", "perks", "bf obj", "armor"), // gust
    // of
    // wind
    // -
    // physical?
    // how to split best? Perhaps into 2... Natural vs Supernatural!
    //

    CHAOS_RESISTANCE("Chaos", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    HOLY_RESISTANCE("Holy", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    SHADOW_RESISTANCE("Shadow", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    ARCANE_RESISTANCE("Arcane", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    POISON_RESISTANCE("Poison", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    DEATH_RESISTANCE("Death", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),
    PSIONIC_RESISTANCE("Psionic", "", false, 0, "units", "chars", "perks", "bf obj", "armor"),

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
    // MAGICAL_ITEM_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks",
    // "classes"),

    WIZARDRY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SPELLCRAFT_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DIVINATION_MASTERY(null, Descriptions.DIVINATION_MASTERY, false, 0, "units", "chars", "perks", "classes"),
    WARCRY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    // spellgroups
    PSYCHIC_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    REDEMPTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    SORCERY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    // ANTI_MAGIC_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks",
    // "classes"),
    ENCHANTMENT_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    // TRANSMUTATION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks",
    // "classes"),
    CONJURATION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    CELESTIAL_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    BENEDICTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    ELEMENTAL_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    FIRE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    AIR_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    WATER_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    EARTH_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SYLVAN_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DEMONOLOGY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    WARP_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    DESTRUCTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    AFFLICTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    BLOOD_MAGIC_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    WITCHERY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    SHADOW_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    NECROMANCY_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    DETECTION_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),
    STEALTH_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    SAVAGE_MASTERY(null, "Mastery", false, 0, "units", "chars", "perks", "classes"),

    XP_GAIN_MOD(true, null, "Experience points", false, 100, "units", "chars", "perks"),
    XP_LEVEL_MOD(true, null, "Experience points", false, 100, "units", "chars", "perks"),

    MASTERY_BOUGHT_WITH_XP(null, "", false, 0, ""),
    MASTERY_BOUGHT_WITH_GOLD(null, "", false, 0, ""),
    ATTR_BOUGHT_WITH_XP(null, "", false, 0, ""),
    ATTR_BOUGHT_WITH_GOLD(null, "", false, 0, ""),

    XP_COST_REDUCTION("Experience", "Experience cost", false, 0, "units", "chars", "perks"),
    XP_COST_REDUCTION_VERBATIM_SPELLS("Experience", "Experience cost", false, 0, "units", "chars", "perks"),
    XP_COST_REDUCTION_LEARNED_SPELLS("Experience", "Experience cost", false, 0, "units", "chars", "perks"),
    XP_COST_REDUCTION_SKILLS("Experience", "Experience cost", false, 0, "units", "chars", "perks"),
    XP_COST_REDUCTION_MASTERIES("Experience", "Experience cost", false, 0, "units", "chars", "perks"),
    // unlocking

    ATTR_POINTS("Attribute points", "Attribute points", true, 0, "units", "chars", "perks"),
    ATTR_POINTS_PER_LEVEL("Attribute points", "Attribute points", false, 0, "units", "chars", "perks"),
    MASTERY_POINTS_PER_LEVEL("MASTERY points", "MASTERY points", false, 0, "units", "chars", "perks"),
    MASTERY_POINTS("MASTERY points", "MASTERY points", true, 0, "units", "chars", "perks"),
    // TRADING?

    RANK("", "", true, 0, "skills", "classes"),
    RANK_MAX("", "", false, 0, "skills", "classes"),
    RANK_REQUIREMENT("", "", false, 0, "skills", "classes"),
    RANK_XP_MOD(true, "", "", false, 50, "skills", "classes"),
    RANK_SD_MOD(true, "", "", false, 25, "skills", "classes"),
    RANK_FORMULA_MOD(true, "", "", false, 25, "skills", "classes"),
    TREE_LINK_OFFSET_X("", "", false, 0, "skills", "classes"),
    TREE_LINK_OFFSET_Y("", "", false, 0, "skills", "classes"),
    TREE_NODE_OFFSET_X("", "", false, 0, "skills", "classes"),
    TREE_NODE_OFFSET_Y("", "", false, 0, "skills", "classes"),
    // SPELL
    AI_PRIORITY(null, "", false, 0, "spells", "actions"),
    CIRCLE("Circle", "", false, 1, "spells", "skills", "classes"),
    FORMULA("Formula", "", false, 0, "actions", "spells", "skills", "classes", "items"),
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
    ENERGY_COST("Energy Cost", " Cost in Energy", false, 0,

     "actions"),
    AP_COST("Act.Pt. Cost", " Cost in AP", false, 1, "spells", "actions", "items"),
    ESS_COST("ESS Cost", " Cost in Essence", false, 0, "spells", "actions", "items"),
    FOC_COST("FOC Cost", " Cost in Focus", false, 0, "spells", "actions", "items"),
    STA_COST("STA Cost", " Cost in Stamina", false, 0, "spells", "actions", "items"),
    ENDURANCE_COST("END Cost", " Cost in Endurance", false, 0, "spells", "actions", "items"),
    CP_COST("CP Cost", "Cost in Counter Points", false, 0, "spells", "actions"),

    END_UPKEEP(null, " UPKEEP in Endurance", false, 0, "spells", "units", "items"),
    AP_UPKEEP(null, " UPKEEP in AP", false, 0, "spells", "units", "items"),
    ESS_UPKEEP(null, " UPKEEP in Essence", false, 0, "spells", "units", "items"),
    FOC_UPKEEP(null, " UPKEEP in Focus", false, 0, "spells", "units", "items"),
    STA_UPKEEP(null, " UPKEEP in Stamina", false, 0, "spells", "units", "items"),

    FOC_REQ(

     "Focus Req.", " Focus Requirement", false, 0, "spells", "actions", "items"),
    COOLDOWN_MOD(true, "Cooldown modifier", " Cooldown modifier", false, 0, "skills", "units", "chars", "perks"),
    // TODO into custom values with _MOVES, _SPELLS, _{SPELL_GROUP} or even
    // _{ACTION_NAME}

    COOLDOWN("Cooldown", "Cooldown", false, 1, "spells", "weapons", "actions", "items"),
    C_COOLDOWN("C Cooldown", "Cooldown", true, 0, "spells", "weapons", "actions", "items"),
    RESISTANCE_MOD(true, null, " RESISTANCE_MOD", false, 100, "spells", "actions"),

    DAMAGE_MOD(true, null, " DAMAGE_MOD", false, 100, "actions", "weapons", "units", "chars", "perks"),
    ATTACK_MOD(true, null, " ATTACK_MOD", false, 100, "actions", "weapons", "armor", "units", "chars", "perks"),

    // TODO ALL DC_FORMULA-REQUIRED PARAMS ABOVE THIS LINE!!!

    OFFHAND_ATTACK_MOD(true, null, " OFF_HAND_ATTACK_MOD", false, DC_Formulas.getOffhandAttackMod(), "units", "chars", "perks"),
    OFFHAND_DAMAGE_MOD(true, null, " OFF_HAND_DAMAGE_MOD", false, DC_Formulas.getOffhandDamageMod(), "units", "chars", "perks"),

    THROW_ATTACK_MOD(true, null, " THROW_ATTACK_MOD", false, 100, "weapons", "units", "chars", "perks"),

    THROW_DAMAGE_MOD(true, null, " THROW_DAMAGE_MOD", false, 100, "weapons", "units", "chars", "perks"),

    DEFENSE_MOD(true, null, " DEFENSE_MOD", false, 100, "actions", "weapons", "armor", "units", "chars", "perks"),
    DAMAGE_BONUS(null, " BONUS_DAMAGE", false, 0, "actions", "weapons"),
    ATTACK_BONUS(null, " ATTACK_BONUS", false, 0, "actions", "weapons", "armor", "units", "chars", "perks"),
    DEFENSE_BONUS(null, " DEFENSE_BONUS", false, 0, "actions", "weapons", "armor"),

    SPELLPOWER_BONUS(null, " SPELLPOWER_BONUS", false, 0, "spells", "items"),
    SPELLPOWER_MOD(true, null, " SPELLPOWER_MOD", false, 100, "spells", "items"),

    BLEEDING_MOD(true, null, "", false, 0, "actions", "weapons", "units", "chars", "perks"),
    FORCE(null, "", false, 0, "actions", "spells"),
    FORCE_MOD(true, null, "", false, 100, "actions", "spells", "weapons", "units", "chars", "perks"),
    FORCE_KNOCK_MOD(true, null, "", false, 100, "actions", "spells", "weapons", "units", "chars", "perks"),
    FORCE_PUSH_MOD(true, null, "", false, 100, "actions", "spells", "weapons", "units", "chars", "perks"),
    FORCE_PROTECTION(null, "", false, 0, "armor", "units", "chars", "perks"),
    FORCE_DAMAGE_MOD(true, null, "", false, 100, "actions", "spells", "weapons", "units", "chars", "perks"),
    FORCE_SPELL_DAMAGE_MOD(true, null, "", false, 100, "units", "chars", "perks"),
    FORCE_MOD_SOURCE_WEIGHT(null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    FORCE_MOD_WEAPON_WEIGHT(null, "", false, 100, "actions"),
    FORCE_MAX_STRENGTH_MOD(true, null, "", false, 4, "actions"),
    // FORCE_CUSTOM_FORMULA(null, "", false, 0, "actions"),
    FORCE_SPELLPOWER_MOD(true, null, "", false, 100, "actions", "spells"),
    SIDE_DAMAGE_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    DIAGONAL_DAMAGE_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    SIDE_ATTACK_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    DIAGONAL_ATTACK_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    LONG_REACH_ATTACK_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    LONG_REACH_DAMAGE_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    CLOSE_QUARTERS_DAMAGE_MOD(true, null, "Close Quarters Damage mod", false, 100, "actions", "units", "chars", "perks", "weapons"),
    CLOSE_QUARTERS_ATTACK_MOD(true, null, "Close Quarters Attack mod", false, 100, "actions", "units", "chars", "perks", "weapons"),


    PASSAGE_ATTACK_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    ENGAGEMENT_ATTACK_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    FLIGHT_ATTACK_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    STUMBLE_ATTACK_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    PASSAGE_DEFENSE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    ENGAGEMENT_DEFENSE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    FLIGHT_DEFENSE_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    STUMBLE_DEFENSE_MOD(true, null, null, false, 0, "units", "chars", "perks"),

    DISENGAGEMENT_ATTACK_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    DISENGAGEMENT_DEFENSE_MOD(true, null, null, false, 0, "units", "chars", "perks"),

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
    COUNTER_MOD(true, null, "", false, 100, "actions", "weapons", "units", "chars", "perks"),
    COUNTER_ATTACK_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    COUNTER_DEFENSE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),

    ENGAGED_AOO_DAMAGE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    ENGAGED_AOO_ATTACK_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),
    ENGAGED_AOO_DEFENSE_MOD(true, null, null, false, 100, "actions", "units", "chars", "perks"),

    AUTO_CRIT_CHANCE(null, null, false, 0, "actions", "weapons", "units", "chars", "perks"),

    SNEAK_PROTECTION(null, null, false, 100, "units", "chars", "perks"),
    SNEAK_RANGED_MOD(true, null, null, false, 0, "units", "chars", "perks"),
    SNEAK_DAMAGE_MOD(true, null, " DAMAGE_MOD", false, 100, "actions", "units", "chars", "perks"),
    SNEAK_ATTACK_MOD(true, null, " ATTACK_MOD", false, 100, "actions", "units", "chars", "perks"),
    SNEAK_DEFENSE_MOD(true, null, " DEFENSE_MOD", false, 20, "actions", "units", "chars", "perks"),
    SNEAK_ARMOR_MOD(true, null, " DEFENSE_MOD", false, 100, "actions", "units", "chars", "perks"),
    SNEAK_DAMAGE_BONUS(null, " BONUS_DAMAGE", false, 0, "actions", "units", "chars", "perks"),
    SNEAK_ATTACK_BONUS(null, " ATTACK_BONUS", false, 0, "actions", "units", "chars", "perks"),
    SNEAK_DEFENSE_PENETRATION(null, "SNEAK_DEFENSE_PENETRATION", false, 0, "actions", "units", "chars", "perks"),
    SNEAK_ARMOR_PENETRATION(null, "SNEAK_DEFENSE_PENETRATION", false, 0, "actions", "units", "chars", "perks"),

    // UNIT
    IMPACT_AREA(null, "", false, 0, "weapons", "spells", "actions"),
    RANGE("Range ", "Maximum distance", false, 1,
     // "units",
     "spells",
     // "chars",
     "actions", "weapons", "items"),
    AUTO_ATTACK_RANGE(null, "", false, 0, "actions"),

    CADENCE_FOCUS_BOOST(null, "", false, 0, "actions", "units", "chars", "perks"),
    CADENCE_BONUS(null, "", false, 0, "weapons"),
    CADENCE_RETAINMENT_CHANCE(null, "", false, 0, "weapons", "actions", "units", "chars", "perks"),
    CADENCE_STA_MOD(true, null, "", false, DC_Formulas.DEFAULT_CADENCE_STA_MOD, "skills", "weapons", "units", "chars", "perks"),
    CADENCE_AP_MOD(true, null, "", false, DC_Formulas.DEFAULT_CADENCE_AP_MOD, "skills", "weapons", "units", "chars", "perks"),
    CADENCE_DAMAGE_MOD(true, null, "", false, 0, "skills", "weapons", "units", "chars", "perks"),
    CADENCE_DEFENSE_MOD(true, null, "", false, 0, "skills", "weapons", "units", "chars", "perks"),
    CADENCE_ATTACK_MOD(true, null, "", false, 0, "skills", "weapons", "units", "chars", "perks"),

    DURABILITY_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "weapons", "armor"),
    INITIATIVE_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    ENDURANCE_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    TOUGHNESS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    ESSENCE_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    FOCUS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    STAMINA_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    MORALE_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    N_OF_ACTIONS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),
    N_OF_COUNTERS_PERCENTAGE("Percentage", "", true, MathMaster.PERCENTAGE, "units", "chars", "perks"),

    STAMINA_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    ESSENCE_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    FOCUS_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    AP_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    CP_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),

    EXTRA_ATTACKS_POINT_COST_MOD(true, "Extra Attacks Cost Mod", "", false, 50, "units", "chars", "perks"),
    INSTANT_STAMINA_PENALTY(null, "", false, -50, "skills", "armor", "weapons", "units", "chars", "perks"),
    INSTANT_CP_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    AOO_STAMINA_PENALTY(null, "", false, -50, "skills", "armor", "weapons", "units", "chars", "perks"),
    AOO_CP_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    COUNTER_STAMINA_PENALTY(null, "", false, -50, "skills", "armor", "weapons", "units", "chars", "perks"),
    COUNTER_CP_PENALTY(null, "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),

    SPELL_STA_PENALTY("Spell STA pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    SPELL_ESS_PENALTY("Spell ESS pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    SPELL_FOC_PENALTY("Spell FOC pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    SPELL_AP_PENALTY("Spell AP pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),

    ATTACK_STA_PENALTY("Attack STA pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    ATTACK_AP_PENALTY("Attack AP pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),

    OFFHAND_ATTACK_STA_PENALTY(null, "", false, 0, "skills", "units", "chars", "perks"),
    OFFHAND_ATTACK_AP_PENALTY(null, "", false, 0, "skills", "units", "chars", "perks"),

    MOVE_STA_PENALTY("Move STA pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),
    MOVE_AP_PENALTY("Move AP pen.", "", false, 0, "skills", "armor", "weapons", "units", "chars", "perks"),

    EVASION(null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    ACCURACY(null, "", false, 0, "actions", "weapons", "skills", "units", "chars", "perks"),
    TOUGHNESS_RECOVERY(null, "", false, 25, "bf obj", "skills", "units", "chars", "perks"),
    VIGILANCE_MOD(true, null, "", false, 0, "skills", "units", "chars", "perks"),
    TOUGHNESS_DEATH_BARRIER_MOD(null, "", false, 100, "bf obj", "units", "chars", "perks"),
    ARMOR_BLOCK_DEFENSE_MOD(true, null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    SHIELD_BLOCK_DEFENSE_MOD(true, null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    BLOCK_CHANCE(null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    PARRY_CHANCE(null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    BLOCK_PENETRATION(null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    PARRY_PENETRATION(null, "", false, 0, "weapons", "skills", "units", "chars", "perks"),
    CRITICAL_REDUCTION("Crit. reduction", "", false, 0, "units", "chars", "perks", "skills", "bf obj", "armor"),
    CRITICAL_MOD(true, "Crit. modifier", "", false, 0, "actions", "units", "chars", "perks", "skills", "bf obj", "weapons"),
    FOCUS_RESTORATION(null, "", false, 0, "skills", "units", "chars", "perks"),
    FOCUS_RETAINMENT(null, "", false, 0, "skills", "units", "chars", "perks"),
    FOCUS_RECOVER_REQ(null, "", false, UnconsciousRule.DEFAULT_FOCUS_REQ, "skills", "units", "chars", "perks"),
    MORALE_RESTORATION(null, "", false, 0, "skills", "units", "chars", "perks"),
    MORALE_RETAINMENT(null, "", false, 0, "skills", "units", "chars", "perks"),

    RANGED_PENALTY_MOD(true, "", "", false, 100, "weapons", "skills", "units", "chars", "perks"),

    // RANGED_PENALTY_DMG("close shot dmg.pen", "", false,
    // DC_Formulas.DEFAULT_RANGED_ADJACENT_DMG_PENALTY, "weapons"),
    // RANGED_PENALTY_ATK("close shot atk.pen", "", false,
    // DC_Formulas.DEFAULT_RANGED_ADJACENT_ATK_PENALTY, "weapons"),
    // MELEE_PENALTY_DMG("melee dmg. pen.", "", false,
    // DC_Formulas.DEFAULT_RANGED_MELEE_DMG_PENALTY, "weapons"),
    // MELEE_PENALTY_ATK("melee atk. pen.", "", false,
    // DC_Formulas.DEFAULT_RANGED_MELEE_DMG_PENALTY, "weapons"),
    // saving throw types - Will, Chance, Reflex, ...

    ENGAGE_TARGET_ATTACK_MOD(true, null, "", false, 0, "units", "chars", "perks", "skills", "weapons"),
    ENGAGE_TARGET_DEFENSE_MOD(true, null, "", false, 0, "units", "chars", "perks", "skills", "weapons"),
    ENGAGEMENT_DEFENSE_REDUCTION_MOD(true, null, "", false, 0, "units", "chars", "perks", "skills", "weapons"),

    DURATION_BONUS(null, "", false, 0, "classes", "skills", "jewelry", "weapons", "units", "chars", "perks"),
    DURATION_MOD(true, null, "", false, 0, "classes", "skills", "jewelry", "weapons", "units", "chars", "perks"),

    LUCK_MOD(true, null, "", false, 0, "skills", "armor", "jewelry", "weapons", "units", "chars", "perks"),
    LUCK_BONUS(null, "", false, 0, "skills", "armor", "jewelry", "weapons", "units", "chars", "perks"),

    GROUP_NUMBER("encounters"),
    MAX_GROUP_NUMBER("encounters"),
    MIN_GROUP_NUMBER("encounters"),
    POWER_LEVEL(null, "", false, 0, "dungeons", "encounters"),
    UNIT_NUMBER("encounters"),
    MAX_UNIT_PER_GROUP("encounters"),

    DURABILITY_SELF_DAMAGE_MOD(true, null, "", false, 100, "weapons", "armor"),
    DURABILITY_DAMAGE_MOD(true, null, "", false, 100, "actions", "weapons", "armor"),

    MAX_BUFF_STACKS(null, "", false, 0, "buffs"),
    BUFF_STACKS(null, "", true, 0, "buffs"),
    SKILL_DIFFICULTY(null, "", false, 0, "skills"),
    SUMMONED_XP_MOD(true, null, "", false, 100, "spells", "skills", "units", "chars", "perks", "classes"),
    UPKEEP_MOD(true, null, "", false, 100, "skills", "units", "chars", "perks", "classes"),
    QUICK_ITEM_GOLD_PERCENTAGE(null, "", false, 0, "units", "chars", "perks"),
    JEWELRY_GOLD_PERCENTAGE(null, "", false, 0, "units", "chars", "perks"),
    MAIN_HAND_GOLD_PERCENTAGE(null, "", false, 70, "units", "chars", "perks"),
    ARMOR_GOLD_PERCENTAGE(null, "", false, 40, "units", "chars", "perks"),
    SPELL_XP_MOD(true, null, "", false, 0, "units", "chars", "perks"),
    CLEAVE_MAX_TARGETS(null, "", false, 0, "skills", "units", "chars", "perks"),
    CLEAVE_DAMAGE_PERCENTAGE_TRANSFER(null, "", false, 0, "skills", "units", "chars", "perks"),
    CLEAVE_DAMAGE_LOSS_PER_JUMP(null, "", false, 0, "skills", "units", "chars", "perks"),

    FEAR_RESISTANCE(null, "", false, 0, "skills", "units", "chars", "perks"),
    BLEEDING_RESISTANCE(null, "", false, 0, "skills", "units", "chars", "perks"),
    INTERRUPT_RESISTANCE(null, "", false, 0, "skills", "units", "chars", "perks"),
    BASH_RESISTANCE(null, "", false, 0, "skills", "units", "chars", "perks"),
    INJURY_RESISTANCE(null, "", false, 0, "skills", "units", "chars", "perks"),
    WOUNDS_RESISTANCE(null, "", false, 0, "skills", "units", "chars", "perks"),

    MIND_AFFECTING_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    FAITH_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    REFLEX_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    BODY_STRENGTH_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    QUICK_WIT_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    FORTITUDE_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    MASS_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    DETECTION_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    DEFENSE_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),

    REACTION_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    REACTION_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    DISARM_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    DISARM_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),

    FORCE_ROLL_SAVE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    FORCE_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),

    MIND_AFFECTING_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    FAITH_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    REFLEX_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    BODY_STRENGTH_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    QUICK_WIT_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    FORTITUDE_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    MASS_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    DETECTION_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    DEFENSE_ROLL_BEAT_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks", "spells"),
    SINGLE_HAND_ATTACK_BONUS_MOD(true, null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    SINGLE_HAND_DEFENSE_BONUS_MOD(true, null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    SINGLE_HAND_DAMAGE_BONUS_MOD(true, null, "", false, 0, "skills", "classes", "units", "chars", "perks"),
    THROW_SIZE_BONUS(null, "", false, 0, "skills", "classes", "units", "chars", "perks"),

    SPACE(null, "", false, 1000, "terrain", "bf obj"), // for passable objects!
    GIRTH(null, "", false, 300, "units", "chars", "perks", "bf obj"),
    COATING_COUNTERS_APPLIED_PER_HIT_MOD(true, null, "", false, 0, "units", "chars", "perks", "skills"),
    COATING_COUNTERS_APPLIED_TO_ITEM_MOD(true, null, "", false, 0, "units", "chars", "perks", "skills"),
    COATING_COUNTERS_SPENT_MOD(true, null, "", false, 0, "units", "chars", "perks", "skills"),

    ARCADE_LEVEL(null, "", false, 0, "party"),
    LEVEL("LEVEL", "", false, 1, "units", "chars", "perks", "party", "arcades"),
    MIN_LEVEL(null, "", false, 0, "mission"),
    MAX_LEVEL(null, "", false, 0, "mission"),
    DIFFICULTY_MOD(true, null, "", false, 100, "mission"),
    UNIT_LEVEL("LEVEL", "", false, 0, "units"),
    HERO_LEVEL("HERO_LEVEL", "", false, 0, "chars"),

    MAX_SPELL_UPGRADES(null, "", false, 4, "spells"),
    LOCK_LEVEL(null, "", false, 0, "bf obj"),
    PICK_LOCK(null, "", false, 0, "classes", "units", "chars", "perks", "skills"),
    TRAP_LEVEL(null, "", false, 0, "bf obj", "trap"),
    DISARM_TRAP(null, "", false, 0, "classes", "units", "chars", "perks", "skills"),

    AUTO_TEST_ID("", "", false, 0, "spells", "actions", "classes", "skills"),
    HT_CUSTOM_POS_X("", "", false, 0, "classes", "skills"),
    HT_CUSTOM_POS_Y("", "", false, 0, "classes", "skills"),
    ANIM_FRAME_DURATION("", "", false, 0, "spells", "actions"),
    ANIM_SPEED("", "", false, 0, "spells", "actions"),;

    static {
        COUNTER_MOD.addSpecialDefault(DC_TYPE.ACTIONS, 75);

        C_TOUGHNESS.setColor(ColorManager.TOUGHNESS);
        C_ENDURANCE.setColor(ColorManager.ENDURANCE);
        C_STAMINA.setColor(ColorManager.STAMINA);
        C_FOCUS.setColor(ColorManager.FOCUS);
        C_MORALE.setColor(ColorManager.MORALE);
        C_ESSENCE.setColor(ColorManager.ESSENCE);
        TOUGHNESS.setColor(ColorManager.TOUGHNESS);
        ENDURANCE.setColor(ColorManager.ENDURANCE);
        STAMINA.setColor(ColorManager.STAMINA);
        FOCUS.setColor(ColorManager.FOCUS);
        MORALE.setColor(ColorManager.MORALE);
        ESSENCE.setColor(ColorManager.ESSENCE);

        GLORY.setDynamic(true);
        XP.setWriteToType(true);
        GLORY.setWriteToType(true);
        GOLD.setWriteToType(true);
        ATTR_POINTS.setWriteToType(true);
        MASTERY_POINTS.setWriteToType(true);
        IDENTITY_POINTS.setWriteToType(true);

        DC_TYPE.ARMOR.setParam(PARAMS.GOLD);
        DC_TYPE.JEWELRY.setParam(PARAMS.GOLD);
        DC_TYPE.ITEMS.setParam(PARAMS.GOLD);
        DC_TYPE.WEAPONS.setParam(PARAMS.GOLD);
        DC_TYPE.UNITS.setParam(PARAMS.GOLD);
        DC_TYPE.SKILLS.setParam(PARAMS.XP);
        DC_TYPE.SPELLS.setParam(PARAMS.XP);
        DC_TYPE.CLASSES.setParam(PARAMS.XP);

        LEVEL.setHighPriority(true);
        CIRCLE.setHighPriority(true);
        XP_COST.setHighPriority(true);

        STAMINA_PENALTY.setSuperLowPriority(true);
        FOCUS_PENALTY.setSuperLowPriority(true);
        ESSENCE_PENALTY.setSuperLowPriority(true);
        AP_PENALTY.setSuperLowPriority(true);

        ENCHANTMENT_CAPACITY.setSuperLowPriority(true);
        QUANTITY.setSuperLowPriority(true);
        // RANGE.setSuperLowPriority(true);

        STRENGTH.setAttr(true);
        VITALITY.setAttr(true);
        AGILITY.setAttr(true);
        DEXTERITY.setAttr(true);

        WILLPOWER.setAttr(true);
        INTELLIGENCE.setAttr(true);
        SPELLPOWER.setAttr(true);
        KNOWLEDGE.setAttr(true);

        WISDOM.setAttr(true);
        CHARISMA.setAttr(true);

        BASE_STRENGTH.setAttr(true);
        BASE_VITALITY.setAttr(true);
        BASE_AGILITY.setAttr(true);
        BASE_DEXTERITY.setAttr(true);

        BASE_WILLPOWER.setAttr(true);
        BASE_INTELLIGENCE.setAttr(true);
        BASE_SPELLPOWER.setAttr(true);
        BASE_KNOWLEDGE.setAttr(true);

        BASE_WISDOM.setAttr(true);
        BASE_CHARISMA.setAttr(true);

        // STRENGTH_PER_LEVEL.setLowPriority(true);
        // VITALITY_PER_LEVEL.setLowPriority(true);
        // AGILITY_PER_LEVEL.setLowPriority(true);
        // DEXTERITY_PER_LEVEL.setLowPriority(true);
        //
        // WILLPOWER_PER_LEVEL.setLowPriority(true);
        // INTELLIGENCE_PER_LEVEL.setLowPriority(true);
        // SPELLPOWER_PER_LEVEL.setLowPriority(true);
        // KNOWLEDGE_PER_LEVEL.setLowPriority(true);
        //
        // WISDOM_PER_LEVEL.setLowPriority(true);
        // CHARISMA_PER_LEVEL.setLowPriority(true);

        WATER_MASTERY.initMastery();
        AIR_MASTERY.initMastery();
        EARTH_MASTERY.initMastery();
        FIRE_MASTERY.initMastery();
        WARCRY_MASTERY.initMastery();
        TACTICS_MASTERY.initMastery();
        LEADERSHIP_MASTERY.initMastery();
        MARKSMANSHIP_MASTERY.initMastery();
        ITEM_MASTERY.initMastery();
        SHADOW_MASTERY.initMastery();
        WITCHERY_MASTERY.initMastery();

        BENEDICTION_MASTERY.initMastery();
        CELESTIAL_MASTERY.initMastery();

        SORCERY_MASTERY.initMastery();
        // ANTI_MAGIC_MASTERY.initMastery();
        // TRANSMUTATION_MASTERY.initMastery();
        CONJURATION_MASTERY.initMastery();

        DEMONOLOGY_MASTERY.initMastery();

        REDEMPTION_MASTERY.initMastery();
        PSYCHIC_MASTERY.initMastery();
        ENCHANTMENT_MASTERY.initMastery();
        WARP_MASTERY.initMastery();
        NECROMANCY_MASTERY.initMastery();
        SAVAGE_MASTERY.initMastery();
        DESTRUCTION_MASTERY.initMastery();

        AFFLICTION_MASTERY.initMastery();
        BLOOD_MAGIC_MASTERY.initMastery();

        ELEMENTAL_MASTERY.initMastery();
        SYLVAN_MASTERY.initMastery();
        BLADE_MASTERY.initMastery();
        BLUNT_MASTERY.initMastery();
        // AXE_MASTERY.initMastery();
        // THROWING_MASTERY.initMastery();
        // MARKSMANSHIP_MASTERY.initMastery();

        SHIELD_MASTERY.initMastery();

        MOBILITY_MASTERY.initMastery();
        ATHLETICS_MASTERY.initMastery();
        MEDITATION_MASTERY.initMastery();
        DISCIPLINE_MASTERY.initMastery();
        // MAGICAL_ITEM_MASTERY.initMastery();

        // SUMMONER_MASTERY.initMastery();
        // ENCHANTER_MASTERY.initMastery();
        // SORCERER_MASTERY.initMastery();
        WIZARDRY_MASTERY.initMastery();
        SPELLCRAFT_MASTERY.initMastery();
        DIVINATION_MASTERY.initMastery();

        DETECTION_MASTERY.initMastery();
        ARMORER_MASTERY.initMastery();
        DEFENSE_MASTERY.initMastery();
        STEALTH_MASTERY.initMastery();

        DUAL_WIELDING_MASTERY.initMastery();
        AXE_MASTERY.initMastery();
        POLEARM_MASTERY.initMastery();
        TWO_HANDED_MASTERY.initMastery();
        UNARMED_MASTERY.initMastery();
        // HEAVY_ARMOR_MASTERY.initMastery();

    }

    boolean writeToType;
    Color color;
    INPUT_REQ inputReq;
    private String name;
    private String shortName;
    private String descr;
    private String entityType;
    private String[] entityTypes;
    private String[] sentityTypes;
    private int AV_ID;
    private int defaultValue;
    private boolean dynamic = false;
    private Metainfo metainfo;
    private boolean lowPriority = false;
    private boolean attr = false;
    private boolean superLowPriority;
    private boolean mastery;
    private boolean highPriority;
    private String fullName;
    private Map<OBJ_TYPE, Object> defaultValuesMap;
    private boolean mod;

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

    PARAMS(String shortName, String descr, boolean dynamic, int defaultValue, String... entityTypes) {
        this(entityTypes[0], shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
        this.entityTypes = entityTypes;
    }

    PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue,
           Color c) {
        this(entityType, shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
        this.metainfo = new Metainfo(c);
    }

    PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue) {
        this(entityType, shortName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
    }

    PARAMS(String entityType, String shortName, String descr, boolean dynamic, int defaultValue,
           int AV_ID) {
        this.name = StringMaster.getWellFormattedString(name());
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
        this.AV_ID = AV_ID;
    }

    PARAMS() {
        this.name = StringMaster.getWellFormattedString(name());
    }

    PARAMS(boolean attr) {
        this();
        this.entityType = "chars";

    }

    PARAMS(String str) {
        this(str, null, "", false, 0, Integer.MAX_VALUE);
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

    @Override
    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        getDefaultValuesMap().put(type, value);

    }

    @Override
    public Object getSpecialDefault(OBJ_TYPE type) {
        return getDefaultValuesMap().get(type);

    }

    private void initNonMasteryDescription() {
        setDescr(DescriptionMaster.getNonMasteryDescription(this));
    }

    private void initMastery() {
        setMastery(true);
        setDescr(DescriptionMaster.getMasteryDescription(this));
    }

    // getOrCreate(base)

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getDisplayedName() {
        return TextMaster.getDisplayedName(this);
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
    public Metainfo getMetainfo() {
        return metainfo;
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
    public boolean isHighPriority() {
        return highPriority;
    }

    @Override
    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
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

    @Override
    public boolean isSuperLowPriority() {
        return superLowPriority;
    }

    @Override
    public void setSuperLowPriority(boolean superLowPriority) {
        this.superLowPriority = superLowPriority;
    }

    // DETECTION_PER_LEVEL.setUnitLevel(true);
    // CONCEALMENT_PER_LEVEL.setUnitLevel(true);
    // STEALTH_PER_LEVEL.setUnitLevel(true);
    // SIGHT_RANGE_PER_LEVEL.setUnitLevel(true);
    // N_OF_COUNTERS_PER_LEVEL.setUnitLevel(true);
    // N_OF_ACTIONS_PER_LEVEL.setUnitLevel(true);
    // INITIATIVE_MODIFIER_PER_LEVEL.setUnitLevel(true);
    // INITIATIVE_BONUS_PER_LEVEL.setUnitLevel(true);
    // DEFENSE_PENETRATION_PER_LEVEL.setUnitLevel(true);
    // RESISTANCE_PENETRATION_PER_LEVEL.setUnitLevel(true);
    // ARMOR_PENETRATION_PER_LEVEL.setUnitLevel(true);
    // ESSENCE_PER_LEVEL.setUnitLevel(true);
    // ESSENCE_REGEN_PER_LEVEL.setUnitLevel(true);
    // STARTING_FOCUS_PER_LEVEL.setUnitLevel(true);
    // FOCUS_REGEN_PER_LEVEL.setUnitLevel(true);
    // STAMINA_PER_LEVEL.setUnitLevel(true);
    // STAMINA_REGEN_PER_LEVEL.setUnitLevel(true);
    // BASE_DAMAGE_PER_LEVEL.setUnitLevel(true);
    // ARMOR_PER_LEVEL.setUnitLevel(true);
    // SPELL_ARMOR_PER_LEVEL.setUnitLevel(true);
    // RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // DEFENSE_PER_LEVEL.setUnitLevel(true);
    // ATTACK_PER_LEVEL.setUnitLevel(true);
    // TOUGHNESS_PER_LEVEL.setUnitLevel(true);
    // ENDURANCE_PER_LEVEL.setUnitLevel(true);
    // ENDURANCE_REGEN_PER_LEVEL.setUnitLevel(true);
    // SPIRIT_PER_LEVEL.setUnitLevel(true);
    //
    // FIRE_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // WATER_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // AIR_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // EARTH_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // CHAOS_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // HOLY_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // SHADOW_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // ARCANE_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // POISON_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // DEATH_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // PSIONIC_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // PIERCING_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // BLUDGEONING_RESISTANCE_PER_LEVEL.setUnitLevel(true);
    // SLASHING_RESISTANCE_PER_LEVEL.setUnitLevel(true);

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
}
