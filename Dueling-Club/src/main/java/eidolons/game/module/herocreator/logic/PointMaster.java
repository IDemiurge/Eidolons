package eidolons.game.module.herocreator.logic;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;

public class PointMaster {

    public static final int ATTRIBUTE_MINIMUM = 4;
    public static final int MASTERY_MINIMUM = 0;
    public static final int MASTERY_MAXIMUM = 50;
    public static final int ATTRIBUTE_MAXIMUM = 100;

    public static int getCost(  Entity hero, PARAMETER param) {
        return getPointCost( hero.getIntParam(ContentValsManager
         .getFinalAttrFromBase(param)),hero,param);
    }
        public static int getPointCost(int value, Entity hero, PARAMETER param) {
        int MOD_FACTOR = 10;
//        value--;
        if (param.isAttribute()) {
            Integer defParam = hero
             .getIntParam(DC_ContentValsManager
              .getDefaultAttr(ContentValsManager
               .getFinalAttrFromBase(param)));
            defParam += 5;
            MOD_FACTOR = defParam * 2;

        }

        int mod = value / MOD_FACTOR;

        return 1 + mod;
    }

    public static int getCost(int initial, int points, Entity hero, PARAMETER param) {
        int cost = 0;
        for (int i = 0; i < points; i++) {
            cost += getPointCost(initial + i, hero, param);
        }
        return cost;
    }

    public static boolean canIncrease(Unit entity, PARAMETER modifyParam) {
        if (modifyParam == null) {
            return false;
        }
        boolean mastery = modifyParam.isMastery();
        int pool = mastery ? entity.getIntParam(PARAMS.MASTERY_POINTS)
         :  entity.getIntParam(PARAMS.ATTR_POINTS);

        return pool>= getCost(entity, modifyParam);
    }

    public static void increaseAttribute(PARAMETER arg, Unit hero, int i) {
        increase(arg, hero, i, false);
    }

    public static void increaseMastery(PARAMETER arg, Unit hero, int i) {
        increase(arg, hero, i, true);
    }
    public static void increase(PARAMETER arg, Unit hero, int i, boolean mastery) {
        arg = DC_ContentValsManager
                .getBaseAttr(arg);
        for (int j = 0; j < i; j++) {
            PointMaster.increased(arg, hero);
            hero.modifyParameter(arg, 1, true);
            if (mastery) {
                SkillMaster.masteryIncreased(hero, arg);
            }
        }
    }
    public static void increased(PARAMETER arg,   Unit hero) {
        PARAMS pool = arg.isMastery() ? PARAMS.MASTERY_POINTS : PARAMS.ATTR_POINTS;
        hero.modifyParameter(pool, -getCost(hero, arg));
    }

}
