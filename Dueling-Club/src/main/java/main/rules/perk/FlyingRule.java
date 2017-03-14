package main.rules.perk;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.MathMaster;

public class FlyingRule {

    private static final int FLYING_ATTACK_MOD = 25;
    private static final int MOVE_STA_COST_REDUCTION = -50;
    private static final int MOVE_AP_COST_REDUCTION = -35;



    public static int getAttackBonus(int attack, Boolean flying_mod) {

        if (BooleanMaster.isTrue(flying_mod)) {
            return MathMaster.getFractionValueCentimal(attack, FLYING_ATTACK_MOD);
        } else {
            return -MathMaster.getFractionValueCentimal(attack, FLYING_ATTACK_MOD);
        }
    }

    public static boolean checkAddMoveCostReductions(Unit ownerObj) {
        // TODO Auto-generated method stub
        if (!ownerObj.isFlying()) {
            return false;
        }

        ownerObj.modifyParamByPercent(PARAMS.MOVE_STA_PENALTY, MOVE_STA_COST_REDUCTION);
        ownerObj.modifyParamByPercent(PARAMS.MOVE_AP_PENALTY, MOVE_AP_COST_REDUCTION);

        return true;

    }

}
