package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.content.DC_Formulas;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class CriticalAttackRule {
    public static int getCriticalChance(int attack, int defense, DC_ActiveObj action) {
        return DefenseVsAttackRule.getCritChance(attack, defense, action);
    }

    public static int getCriticalDamagePercentage(DC_ActiveObj action) {
        return getCriticalDamagePercentage(action, null);
    }

    public static int getCriticalDamagePercentage(DC_ActiveObj action, BattleFieldObject attacked) {
        int mod =
         action.getFinalModParam(PARAMS.CRITICAL_MOD);
//        int factor =
//         MathMaster.getFractionValueCentimal(
//          DC_Formulas.DEFAULT_CRITICAL_FACTOR, mod);
        if (attacked != null) {
            mod -= attacked.getIntParam(PARAMS.CRITICAL_REDUCTION);
        }
        return MathMaster.applyMod(
         DC_Formulas.DEFAULT_CRITICAL_FACTOR, mod);
    }
}
