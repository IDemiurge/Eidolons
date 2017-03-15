package main.game.logic.combat.attack;

import main.content.PARAMS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.system.DC_Formulas;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class CriticalAttackRule {
    public static int getCriticalChance(int attack, int defense, DC_ActiveObj action) {
        return DefenseVsAttackRule.getCritChance(  attack, defense , action);
    }

    public static int getCriticalDamagePercentage(DC_ActiveObj action) {
        return getCriticalDamagePercentage(action, null );
    }

    public static int getCriticalDamagePercentage(DC_ActiveObj action, Unit attacked) {
        int mod =
         action.getFinalModParam(PARAMS.CRITICAL_MOD);
//        int factor =
//         MathMaster.getFractionValueCentimal(
//          DC_Formulas.DEFAULT_CRITICAL_FACTOR, mod);
        if (attacked != null)
            mod -= attacked.getIntParam(PARAMS.CRITICAL_REDUCTION);
        return MathMaster.applyMod(
         DC_Formulas.DEFAULT_CRITICAL_FACTOR, mod);
    }
}
