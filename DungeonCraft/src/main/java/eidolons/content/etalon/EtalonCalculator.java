package eidolons.content.etalon;

import eidolons.content.values.DC_ValueManager;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.SkillEnums;
import main.system.auxiliary.data.ArrayMaster;
import main.system.math.MathMaster;

import static main.system.auxiliary.log.LogMaster.log;

public class EtalonCalculator {
    private static final int MSTR_PER_LEVEL_INCREMENT = 1;
    private static final int ATTR_PER_LEVEL_INCREMENT = 2;
    private static final int MSTR_PER_LEVEL = 2;
    private static final int ATTR_PER_LEVEL = 5;

    public static int getSkillPts(int level, ClassEnums.CLASS_GROUP baseClass, EtalonConsts.MASTERY_SET masterySet) {
        int bonus = getBonusSkPts(level, baseClass);
        int result = bonus + getPts(false, level, masterySet);
        return result;
    }

    public static int getSpellPts(int level, ClassEnums.CLASS_GROUP baseClass, EtalonConsts.MASTERY_SET masterySet) {
        int bonus = getBonusSpPts(level, baseClass);
        int result = bonus + getPts(true, level, masterySet);
        return result;
    }

    private static int getBonusSpPts(int level, ClassEnums.CLASS_GROUP baseClass) {
        return 0;
    }

    private static int getBonusSkPts(int level, ClassEnums.CLASS_GROUP baseClass) {
        return 0;
    }

    private static int getPts(boolean spell, int level, EtalonConsts.MASTERY_SET masterySet) {
        EtalonConsts.MasteryData data = EtalonGen.createMstrData(masterySet.weights, masterySet.spentCoef, level);
        int pts = 0;
        for (String s : data.getValues().keySet()) {
            int value = data.getIntValue(s);
            SkillEnums.MASTERY m = data.getKeyConst(s);
            if (m == null) {
                log(1, "GEN: mastery not found - " + s);
                continue;
            }
            if (isMagicMastery(m)) {
                if (spell) {
                    pts += MathMaster.sum(value) / 2;
                }
            } else {
                if (!spell)
                    pts += MathMaster.sum(value);
            }
        }
        // MathMaster.getArthimeticSequenceSum(1, end, level);
        return pts;
    }

    private static boolean isMagicMastery(SkillEnums.MASTERY m) {
        if (ArrayMaster.contains_(DC_ValueManager.VALUE_GROUP.MAGIC.getParams(), m.getParam())) {
            return true;
        }
        return false;
    }

    public static int getFreeMasteryRanks(int level, ClassEnums.CLASS_GROUP baseClass, float spentCoef) {
        int total = getTotalStat(level, false);
        return Math.round(total * spentCoef);
    }

    public static int getFreeAttrs(int level, ClassEnums.CLASS_GROUP baseClass, float spentCoef) {
        int total = getTotalStat(level, true);
        return Math.round(total * spentCoef);
    }

    public static int getTotalStat(int level, boolean attr) {
        int start = attr ? ATTR_PER_LEVEL : MSTR_PER_LEVEL;
        int increment = attr ? ATTR_PER_LEVEL_INCREMENT : MSTR_PER_LEVEL_INCREMENT;
        int end = start + level * increment;

        int result = MathMaster.getArithmeticSequenceSum(start, end, level);
        return result;
    }

    public static int getMasteryRanksForLvlUp(int level) {
        return MSTR_PER_LEVEL + level * MSTR_PER_LEVEL_INCREMENT;
    }

    public static int getAttrValueForPts(int base, int attrValue, int pts) {
        while (getCost(base, attrValue) <= pts) {
            pts -= getCost(base, attrValue);
            attrValue++;
        }
        return attrValue;
    }

    private static int getCost(int base, int attrValue) {
        return 1 + (attrValue - base) / base;
    }
}
