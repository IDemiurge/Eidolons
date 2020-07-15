package eidolons.system;

import eidolons.content.PARAMS;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.system.auxiliary.Loop;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;
import main.system.math.MathMaster;

public class DC_Formulas {

    public static final float ARMORER_ARMOR_MOD = 1.5f;
    public static final String WAIT_COEFFICIENT = "33";
    public static final String WAIT_MAX_COEFFICIENT = "150";
    public static final Integer STARTING_FOCUS = 25;
    public static final Integer STARTING_ESSENCE_PERCENTAGE = 50;
    public static final int ESS_REGEN_PER_CRYSTAL = 5;
    public static final float DEFENSE_DMG_DECREASE = 1.5f;
    public static final float ATTACK_DMG_INCREASE = 1;
    public static final float DEFENSE_DODGE_CHANCE = 1.5f;
    public static final float ATTACK_CRIT_CHANCE = 1;
    public static final int DEFENSE_DMG_DECREASE_LIMIT = 40;
    public static final int ATTACK_DMG_INCREASE_LIMIT = 50;
    public static final float DEFENSE_PROPORTION_CRIT_SQRT_BASE_MULTIPLIER = 25;
    public static final float DEFENSE_PROPORTION_CRIT_MAX = 50;
    public static final float ATTACK_PROPORTION_CRIT_SQRT_BASE_MULTIPLIER = 50;
    public static final float ATTACK_PROPORTION_CRIT_MAX = 25;
    public static final Integer MORALE_PER_SPIRIT = 10;
    public static final int WEIGHT_FACTOR = 25;
    public static final Integer DEF_COUNTER_MOD = 75;
    public static final int POWER_XP_FACTOR = 10;
    public static final int ATTR_POINTS_PER_LEVEL_BONUS = 1;
    public static final int MASTERY_POINTS_PER_LEVEL_BONUS = 2;
    public static final int INFINITE_VALUE = 100;
    public static final int DURABILITY_DAMAGE_THRESHOLD_ARMOR = 10;
    public static final int DURABILITY_DAMAGE_FACTOR_ARMOR = 20;
    public static final int DURABILITY_DAMAGE_THRESHOLD_WEAPON = 5;
    public static final int DURABILITY_DAMAGE_FACTOR_WEAPON = 15;
    public static final int BUY_ATTRIBUTE_XP_COST = 35;
    public static final int BUY_ATTRIBUTE_GOLD_COST = 50;
    public static final int BUY_MASTERY_GOLD_COST = 35;
    public static final int BUY_MASTERY_XP_COST = 25;
    public static final Integer MASTERY_POINTS_FOR_CIRCLE = 5;
    public static final Formula DIVINATION_POOL_FORMULA = new Formula("4+"
     + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.DIVINATION_CAP) + " /2+(2+"
     + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.DIVINATION_MASTERY) + " /3)*"
    ).getAppendedByModifier(StringMaster.getValueRef(KEYS.SOURCE, PARAMS.DIVINATION_POOL_MOD));
    public static final String UNIT_LEVEL_POWER_BONUS = "({AMOUNT}+1)*5+({AMOUNT})*({AMOUNT})";
    public static final Formula SUMMONED_UNIT_XP = new Formula(
     "min(45*{Mastery}*{active_spell_difficulty}/5,15*({Mastery}+{Spellpower})*{active_spell_difficulty}/10)");
    public static final Integer XP_COST_PER_SPELL_DIFFICULTY = 5;
    public static final String XP_COST_PER_SKILL_DIFFICULTY = "" + "10*({AMOUNT}*{AMOUNT}/4)+"
     + "{AMOUNT}*5";
    public static final int DEFAULT_CRITICAL_FACTOR = 75;
    public static final int DEFAULT_PARRY_DURABILITY_DAMAGE_MOD = 50;
    public static final float KNOWLEDGE_ANY_SPELL_FACTOR = 2.0f;
    public static final int DEFAULT_RANGED_MELEE_DMG_PENALTY = 50;
    public static final int DEFAULT_RANGED_MELEE_ATK_PENALTY = 0;
    public static final int DEFAULT_RANGED_ADJACENT_DMG_PENALTY = 0;
    public static final int DEFAULT_RANGED_ADJACENT_ATK_PENALTY = 50;
    public static final int DEFAULT_CADENCE_STA_MOD = -35;
    public static final int DEFAULT_CADENCE_AP_MOD = -25;
    public static final int SINGLE_HAND_ATTACK_BONUS = 10;
    public static final int SINGLE_HAND_DEFENSE_BONUS = 10;
    public static final int SINGLE_HAND_DAMAGE_BONUS = 10;
    public static final Integer INTELLIGENCE_ORGANIZATION_CAP_MOD = 10;
    public static final String DRUIDIC_VISIONS_ESSENCE = "5+{wisdom}*{spell_spell_difficulty}/10";
    public static final String HOLY_PRAYER_MORALE = "5+{Willpower}*{spell_spell_difficulty}/10";
    private static final int TOUGHNESS_STR_MODIFIER = 15;
    private static final int TOUGHNESS_STR_MODIFIER_HERO = 10;
    private static final float TOUGHNESS_STR_SQUARE_MODIFIER = 0.05f;
    private static final float TOUGHNESS_STR_SQUARE_BARRIER = 200;
    private static final int CARRYING_CAPACITY_STR_MODIFIER = 2;
    private static final int TOUGHNESS_VIT_MODIFIER = 2;
    private static final float FORTITUDE_VIT_MODIFIER = 0.25f;
    private static final float ENDURANCE_VIT_MODIFIER = 40;
    private static final float ENDURANCE_VIT_SQUARE_MODIFIER = 0.15f;
    private static final float ENDURANCE_VIT_SQUARE_BARRIER = 400;
    private static final float REST_BONUS_VIT_MODIFIER = 0.1f;
    private static final float ENDURANCE_REGEN_VIT_MODIFIER = 0.25f;
    private static final float ATTACK_AGI_MODIFIER = 2;
    private static final float INIT_MOD_AGI_MODIFIER = 0.5f;
    private static final float DEF_DEX_MODIFIER = 2;
    private static final float RES_WIL_MODIFIER = 0.5f;
    private static final int RES_FOC_MODIFIER = 1;
    private static final float SPIRIT_WIL_MODIFIER = 0.25f;
    private static final int ESS_WIS_MODIFIER = 5;
    private static final float ESS_WIS_SQUARE_MODIFIER = 0.05f;
    private static final float ESS_WIS_SQUARE_BARRIER = 400;
    private static final float INT_MEM_MODIFIER_SQUARE_BARRIER = 40;
    private static final String INT_MEM_MODIFIER = "{amount}*2+ max(0, {amount}*{amount}/10-"
     + INT_MEM_MODIFIER_SQUARE_BARRIER + ")";
    private static final int CHA_DIV_MODIFIER = 3;
    private static final float DEF_MASTERY_MODIFIER = 1;
    private static final Integer FOCUS_CONST_FOR_CONCENTRATION = 10;
    private static final Integer ESSENCE_CONST_FOR_MEDITATION = 20;
    private static final Integer TOUGHNESS_CONST_FOR_REST = 15;
    private static final String GATEWAY_THRESHOLD = "30";
    private static final String CRYSTAL_THRESHOLD = "15";
    // ++ MAX POOL FORMULA
    private static final float INT_MSTR_MODIFIER = 0.2f;
    private static final String LEVEL_XP_FORMULA =
            "100+(max(1, {AMOUNT})-1)" +
            "*20+(max(1, {AMOUNT})-1)*(max(1, {AMOUNT})-1)*5";
    private static final String LEVEL_GOLD_FORMULA = "125+(max(1, {AMOUNT})-1)*25+(max(1, {AMOUNT})-1)*(max(1, {AMOUNT})-1)*10";
    private static final int OFF_HAND_ATTACK_MOD = 50;
    private static final int OFF_HAND_DAMAGE_MOD = 75;
    private static final int MAIN_HAND_DUAL_ATTACK_MOD = -25;
    private static final String DEFENSE_FROM_MASTERY_FORMULA = "{AMOUNT}+{AMOUNT}*{AMOUNT}/25";
//    private static final String DEFENSE_MOD_FROM_MASTERY_FORMULA = "{AMOUNT}+{AMOUNT}*{AMOUNT}/25";

    private static final String ATTACK_FROM_MASTERY_FORMULA = "{AMOUNT}+{AMOUNT}*{AMOUNT}/50";
    private static final String DAMAGE_FROM_TWOHANDED_MASTERY_FORMULA = "{AMOUNT}/2+{AMOUNT}*{AMOUNT}/70";

    private static final String TOUGHNESS_FROM_STRENGTH_FORMULA = "{AMOUNT}*"
     + TOUGHNESS_STR_MODIFIER + "+max(0, ({AMOUNT}*{AMOUNT} -"
     + TOUGHNESS_STR_SQUARE_BARRIER + "))*" + TOUGHNESS_STR_SQUARE_MODIFIER;
    private static final String ENDURANCE_FROM_VITALITY_FORMULA = "{AMOUNT}*"
     + ENDURANCE_VIT_MODIFIER + "+max(0, ({AMOUNT}*{AMOUNT} -"
     + ENDURANCE_VIT_SQUARE_BARRIER + "))*" + ENDURANCE_VIT_SQUARE_MODIFIER;
    private static final String ESS_WIS_FORMULA = "{AMOUNT}*" + ESS_WIS_MODIFIER
     + "+max(0, ({AMOUNT}*{AMOUNT} -" + ESS_WIS_SQUARE_BARRIER + "))*"
     + ESS_WIS_SQUARE_MODIFIER;
    private static final int ATTR_POINTS_PER_LEVEL_INCREASE = 1;
    private static final int MSTR_POINTS_PER_LEVEL_INCREASE = 2;
    private static final Integer ATTR_POINTS_PER_LEVEL_DEFAULT = 5;
    private static final Integer SELLING_PRICE_REDUCTION = 50;
    private static final int MAX_UNIT_LEVEL = 100;
    private static final String ACTS_DEX_MODIFIER_FORMULA = "{AMOUNT}*max(0.1, (0.2-{AMOUNT}*0.04))";

    private static final String EXTRA_MOVES_DEX_FORMULA = "{AMOUNT}*max(0.2, (1-{AMOUNT}*0.04))";
    private static final String EXTRA_ATKS_AGI_FORMULA = "{AMOUNT}*max(0.15, (0.75-{AMOUNT}*0.03))";

    public static Formula DIVINATION_MAX_SD_FORMULA = new Formula("2+"
     + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.DIVINATION_CAP) + " /5+" // WISDOM?
     + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.DIVINATION_MASTERY) + " /2")
     .getAppendedByModifier(StringMaster.getValueRef(KEYS.SOURCE,
      PARAMS.DIVINATION_MAX_SD_MOD));

    public static int getToughnessFromStrengthHero(int amount) {
        return Math.round(amount * TOUGHNESS_STR_MODIFIER_HERO);
    }
    public static int getToughnessFromStrength(int amount) {
        return Math.round(amount * TOUGHNESS_STR_MODIFIER);
    }

    public static int getFortitudeFromVitality(int amount) {
        return Math.round(amount * FORTITUDE_VIT_MODIFIER);
    }

    public static int getToughnessFromVitality(int amount) {
        return Math.round(amount * TOUGHNESS_VIT_MODIFIER);
    }

    public static int getEnduranceFromVitality(int amount) {
        return calculateFormula(ENDURANCE_FROM_VITALITY_FORMULA, amount) / 5 * 5;
    }

    public static int getEssenceFromWisdom(int amount) {
        return calculateFormula(ESS_WIS_FORMULA, amount) / 5 * 5;
        // return Math.round(amount * ESS_WIS_MODIFIER);
    }

    public static int getCarryingCapacityFromStrength(int amount) {
        return Math.round(amount * CARRYING_CAPACITY_STR_MODIFIER);
    }

    public static int getRestBonusFromVitality(int amount) {
        return Math.round(amount * REST_BONUS_VIT_MODIFIER);
    }

    public static int getEnduranceRegenFromVitality(int amount) {
        return Math.round(amount * ENDURANCE_REGEN_VIT_MODIFIER);
    }

    public static int getCountersFromAgi(int amount) {
        return calculateFormula(EXTRA_ATKS_AGI_FORMULA, amount) ;
    }

    public static int getAttackFromAgi(int amount) {

        return Math.round(amount * ATTACK_AGI_MODIFIER);
    }

    public static int getInitModFromAgi(int amount) {

        return Math.round(amount * INIT_MOD_AGI_MODIFIER);
    }

    public static int getActsFromDexAndAgility(int sum) {
        return calculateFormula(ACTS_DEX_MODIFIER_FORMULA, sum) ;
    }

    public static int getExtraMovesFromDex(int amount) {
        return calculateFormula(EXTRA_MOVES_DEX_FORMULA, amount) ;
    }
    public static int getDefFromDex(int amount) {

        return Math.round(amount * DEF_DEX_MODIFIER);
    }

    public static int getDefFromDefenseMastery(int amount) {
        return Math.round(amount * DEF_MASTERY_MODIFIER);
    }

    public static int getResistanceFromWill(int amount) {

        return Math.round(amount * RES_WIL_MODIFIER);
    }

    public static Integer getStartingFocusFromWill(int amount) {
        return Math.round(amount * RES_FOC_MODIFIER);
    }

    public static int getSpiritFromWill(int amount) {

        return Math.round(amount * SPIRIT_WIL_MODIFIER);
    }

    public static int getMemoryFromInt(int amount) {

        return Math.round(calculateFormula(INT_MEM_MODIFIER, amount));
    }

    public static int getMasteryFromIntelligence(int amount) {
        return Math.round(amount * INT_MSTR_MODIFIER);
    }

    public static int getDivinationFromCha(int amount) {

        return Math.round(amount * CHA_DIV_MODIFIER);
    }

    public static String getEssCostReductionFromSpellcraft(int amount) {
        amount = -amount;
        return StringMaster.getPercentageAppend(amount);
    }

    public static String getFocReqReductionFromSorcery(int amount) {
        amount = -amount;
        return StringMaster.getPercentageAppend(amount);
    }

    public static String getFocReqReductionFromEnchantment(int amount) {
        amount = -amount;
        return StringMaster.getPercentageAppend(amount);
    }

    public static String getFocReqReductionFromSummoning(int amount) {
        amount = -amount;
        return StringMaster.getPercentageAppend(amount);
    }

    public static Integer getEssenceConstForMeditation() {
        return ESSENCE_CONST_FOR_MEDITATION;
    }

    public static Integer getToughnessConstForRest() {
        return TOUGHNESS_CONST_FOR_REST;
    }

    public static String getCrystalThreshold() {
        return CRYSTAL_THRESHOLD;
    }

    public static String getGatewayThreshold() {
        return GATEWAY_THRESHOLD;
    }


    public static int getTotalXpForLevel(int level) {
        int xp = 0;
        for (int i = 1; i <= level; i++) {
            xp += getXpForLevel(i);
        }
        return xp;
    }

    public static int getIdentityPointsForLevel(int level) {
        if (level > 10) {
            return 0;
        }
        if (level % 2 == 0) {
            return 0;
        }
        return 1;
    }

    public static int getXpForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        return calculateFormula(LEVEL_XP_FORMULA, level);

    }

    public static int getGoldForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        return calculateFormula(LEVEL_GOLD_FORMULA, level);

    }

    public static int calculateFormula(String formula, int amount) {
        return MathMaster.calculateFormula(formula, amount);
    }

    public static int getOffhandAttackMod() {
        return OFF_HAND_ATTACK_MOD;
    }

    public static int getOffhandDamageMod() {
        return OFF_HAND_DAMAGE_MOD;
    }

    public static int getMainHandDualAttackMod() {
        return MAIN_HAND_DUAL_ATTACK_MOD;
    }

    public static int getPowerFromUnitXP(int xp) {
        return Math.round(xp / POWER_XP_FACTOR);
    }

    public static int getPointsPerLevelIncrease(Entity newType, boolean attrs) {
        return (attrs) ? ATTR_POINTS_PER_LEVEL_INCREASE : MSTR_POINTS_PER_LEVEL_INCREASE;
    }

    public static Integer getAttrPointsPerLevelDefault(Entity newType) {
        return ATTR_POINTS_PER_LEVEL_DEFAULT;
    }

    public static int getDefenseFromDefenseMastery(int amount) {
        return calculateFormula(DEFENSE_FROM_MASTERY_FORMULA, amount);
    }

    public static int getAttackFromWeaponMastery(int amount) {
        // TODO
        return calculateFormula(ATTACK_FROM_MASTERY_FORMULA, amount);
    }

    public static int getDamageFromTwohandedMastery(Integer intParam) {
        return calculateFormula(DAMAGE_FROM_TWOHANDED_MASTERY_FORMULA, intParam);
    }

    public static Integer getSellingPriceReduction() {
        return SELLING_PRICE_REDUCTION;
    }

    private static int getFormulaArg(String formula, Integer result) {
        return getFormulaArg(formula, result, 0, result * 100);
    }

    private static int getFormulaArg(String formula, Integer result, Integer min, Integer max) {
        int i = min;
        Loop.startLoop(max);
        while (!Loop.loopEnded()) {
            int n = calculateFormula(formula, i);
            if (n > result) {
                break;
            }
            i++;
        }
        return i - 1;

    }

    public static Integer getLevelForXp(int n) {
        // return getFormulaArg(formula, n, 1, MAX_UNIT_LEVEL) //progression
        // formula?
        int i = 1;
        Loop.startLoop(MAX_UNIT_LEVEL);
        while (!Loop.loopEnded()) {
            int xpForLevel = getTotalXpForLevel(i);
            if (xpForLevel > n) {
                break;
            }
            i++;

        }
        return i - 1;
    }

    public static int getSkillDifficultyForXpCost(Integer xpCost) {
        return getFormulaArg(XP_COST_PER_SKILL_DIFFICULTY, xpCost);
    }

    public static int getSoulforceForLordLevel(int lordLevel) {
        return 100+25*lordLevel;
    }
}
