package eidolons.entity.handlers.active;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import main.entity.handlers.EntityCalculator;
import main.entity.handlers.EntityMaster;
import eidolons.game.battlecraft.rules.combat.attack.DefenseVsAttackRule;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveCalculator extends EntityCalculator<DC_ActiveObj> {

    public ActiveCalculator(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }


    public int calculateTimeCost() {
        if (getEntity().getCosts().getCost(PARAMS.C_N_OF_ACTIONS) == null) {
            return 0;
        }
        return getEntity().getCosts().getCost(PARAMS.C_N_OF_ACTIONS).getPayment()
         .getAmountFormula().getInt(getRef())
         * getEntity().getOwnerObj().getIntParam(PARAMS.INITIATIVE_MODIFIER);

    }

    public int getCritOrDodgeChance(DC_Obj target) {
        if (!getEntity().getChecker().isAttackGeneric())
            if (target instanceof BattleFieldObject)
                if (getEntity().getChecker().isAttack()) {
//            Attack attack = DC_AttackMaster.getAttackFromAction(getEntity());
//             new AttackCalculator(attack, true).getCritOrDodgeChance();
                    return DefenseVsAttackRule.getCritOrDodgeChance(getEntity(), (BattleFieldObject) target);
                }
        return 100;
    }
}
