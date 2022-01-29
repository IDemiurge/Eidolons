package eidolons.content;

import main.entity.Entity;
import main.system.auxiliary.Loop;
import main.system.math.Formula;
import main.system.math.MathMaster;

public class DC_Formulas {

    public static final int MAX_BASE_CLASSES = 3;
    //TODO refactor into enums with vals - so that we can customize via config files etc
    public static final Integer STARTING_FOCUS = 25;
    public static final Integer STARTING_ESSENCE_PERCENTAGE = 50;

    public static final int ATTR_POINTS_PER_LEVEL_BONUS = 1;
    public static final int MASTERY_RANKS_PER_LEVEL_BONUS = 1;
    public static final int INFINITE_VALUE = 100;

    public static final int DURABILITY_DAMAGE_THRESHOLD_ARMOR = 10;
    public static final int DURABILITY_DAMAGE_FACTOR_ARMOR = 20;
    public static final int DURABILITY_DAMAGE_THRESHOLD_WEAPON = 5;
    public static final int DURABILITY_DAMAGE_FACTOR_WEAPON = 15;

    @Deprecated
    public static final int POWER_XP_FACTOR = 10;
    public static final String UNIT_LEVEL_POWER_BONUS = "({AMOUNT}+1)*5+({AMOUNT})*({AMOUNT})";
    public static final Formula SUMMONED_UNIT_XP = new Formula(
     "min(45*{Mastery}*{active_spell_difficulty}/5,15*({Mastery}+{Spellpower})*{active_spell_difficulty}/10)");
    public static final Integer XP_COST_PER_SPELL_DIFFICULTY = 5;
    public static final String XP_COST_PER_SKILL_DIFFICULTY = "" + "10*({AMOUNT}*{AMOUNT}/4)+"
     + "{AMOUNT}*5";
    public static final int DEFAULT_PARRY_DURABILITY_DAMAGE_MOD = 50;

    public static final int DEFAULT_CADENCE_AP_MOD = -25;
    public static final int DEFAULT_CADENCE_TOU_MOD = -35;
    public static final int SINGLE_HAND_ATTACK_BONUS = 10;
    public static final int SINGLE_HAND_DEFENSE_BONUS = 10;
    public static final int SINGLE_HAND_DAMAGE_BONUS = 10;

    private static final String LEVEL_POWER_FORMULA =
            "10+(max(1, {AMOUNT})-1)*2+(max(1, {AMOUNT})-1)*(max(1, {AMOUNT})-1)/2";
    private static final String LEVEL_GOLD_FORMULA =
            "125+(max(1, {AMOUNT})-1)*25+(max(1, {AMOUNT})-1)*(max(1, {AMOUNT})-1)*10";

    private static final int OFF_HAND_ATTACK_MOD = 50;
    private static final int OFF_HAND_DAMAGE_MOD = 75;
    private static final int MAIN_HAND_DUAL_ATTACK_MOD = -25;

    private static final float DEF_MASTERY_MODIFIER = 1;
    private static final String DEFENSE_FROM_MASTERY_FORMULA = "{AMOUNT}+{AMOUNT}*{AMOUNT}/25";
//    private static final String DEFENSE_MOD_FROM_MASTERY_FORMULA = "{AMOUNT}+{AMOUNT}*{AMOUNT}/25";

    private static final String ATTACK_FROM_MASTERY_FORMULA = "{AMOUNT}+{AMOUNT}*{AMOUNT}/50";
    private static final String DAMAGE_FROM_TWOHANDED_MASTERY_FORMULA = "{AMOUNT}/2+{AMOUNT}*{AMOUNT}/70";

    private static final int ATTR_POINTS_PER_LEVEL_INCREASE = 1;
    private static final int MSTR_POINTS_PER_LEVEL_INCREASE = 2;
    private static final Integer ATTR_POINTS_PER_LEVEL_DEFAULT = 5;
    private static final Integer SELLING_PRICE_REDUCTION = 50;
    private static final int MAX_UNIT_LEVEL = 100;

    @Deprecated
    private static final String INITIATIVE_AGI_DEX_FORMULA = "{AMOUNT}*max(0.1, (0.2-{AMOUNT}*0.04))";

    private static final String EXTRA_MOVES_DEX_FORMULA = "{AMOUNT}*max(0.2, (1-{AMOUNT}*0.04))";
    private static final String EXTRA_ATKS_AGI_FORMULA = "{AMOUNT}*max(0.15, (0.75-{AMOUNT}*0.03))";

    public static int getCountersFromAgi(int amount) {
        return calculateFormula(EXTRA_ATKS_AGI_FORMULA, amount) ;
    }

    public static int getExtraMovesFromDex(int amount) {
        return calculateFormula(EXTRA_MOVES_DEX_FORMULA, amount) ;
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
        return calculateFormula(LEVEL_POWER_FORMULA, level);
    }

    public static int getGoldForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        return calculateFormula(LEVEL_GOLD_FORMULA, level);

    }

    public static int calculateAccuracyRating(int defense, int attack) {
        if (defense<0)
        {
            attack+=-defense;
            defense = 1;
        }
        return MathMaster.getMinMax((int) ((attack-defense)/Math.sqrt(defense)*2+20), -20,  100);
    }

    public static int getSkillDifficultyForXpCost(Integer xpCost) {
        return getFormulaArg(XP_COST_PER_SKILL_DIFFICULTY, xpCost);
    }

    public static int getSoulforceForLordLevel(int lordLevel) {
        return 100+25*lordLevel;
    }


    public static int calculateAccuracyRatingSpell(int resist, int penetration) {
        return (int) ((penetration-resist)/Math.sqrt(resist)*2+50);
    }
}






























