package eidolons.content.values;

import eidolons.content.PARAMS;
import main.content.DC_TYPE;
import main.system.graphics.ColorManager;

import static eidolons.content.PARAMS.*;
import static eidolons.content.PROPS.*;

public class ValueInitializer {
    public static void init(){
        initParams();
        initProps();
    }
    public static void initProps(){

        FAVORED_SPELL_GROUPS.setContainer(true);
        NATURAL_WEAPON.setDefaultValue("Average Fist");
        // OFFHAND_NATURAL_WEAPON.setDefaultValue("Average Fist");
        SPELL_UPGRADES.setDynamic(true);

        HINTS.setDynamic(true);
        LAST_SEEN.setDynamic(true);
        UPKEEP_FAIL_ACTION.setDynamic(true);
        DROPPED_ITEMS.setDynamic(true);
        // QUICK_ITEMS.setDynamic(true);
        // INVENTORY.setDynamic(true);
        // JEWELRY.setDynamic(true);
        // VERBATIM_SPELLS.setDynamic(true);
        // MEMORIZED_SPELLS.setDynamic(true);
        DIVINED_SPELLS.setDynamic(true);
        // KNOWN_SPELLS.setDynamic(true);
        // LEARNED_SPELLS.setDynamic(true);

        FACING_DIRECTION.setDynamic(true);
        VISIBILITY_STATUS.setDynamic(true);
        DETECTION_STATUS.setDynamic(true);
        PERCEPTION_STATUS_PLAYER.setDynamic(true);
        PERCEPTION_STATUS.setDynamic(true);

        DC_TYPE.SPELLS.setUpgradeRequirementProp(KNOWN_SPELLS);
        DC_TYPE.CLASSES.setUpgradeRequirementProp(CLASSES);
        DC_TYPE.SKILLS.setUpgradeRequirementProp(SKILLS);

        // DYNAMIC CONTAINERS
    }
    public static void initParams(){
        // COUNTER_MOD.addSpecialDefault(DC_TYPE.ACTIONS, 75);
        // FOCUS_RECOVER_REQ.addSpecialDefault(DC_TYPE.UNITS, UnconsciousRule.DEFAULT_FOCUS_REQ_UNIT);

        C_TOUGHNESS.setColor(ColorManager.TOUGHNESS);
        C_ENDURANCE.setColor(ColorManager.ENDURANCE);
        C_FOCUS.setColor(ColorManager.FOCUS);
        C_ESSENCE.setColor(ColorManager.ESSENCE);
        TOUGHNESS.setColor(ColorManager.TOUGHNESS);
        ENDURANCE.setColor(ColorManager.ENDURANCE);
        FOCUS.setColor(ColorManager.FOCUS);
        ESSENCE.setColor(ColorManager.ESSENCE);

        GLORY.setDynamic(true);
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
}
