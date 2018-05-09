package tests.logic.combat;

import TestUtils.JUnitUtils;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import org.junit.Test;

/**
 * Created by JustMe on 3/28/2017.
 */
public class AttackDamageTest extends AttackTest {

    @Test
    public void testAttack() {
//super.
        DC_ActiveObj action = getAction();
        Attack attack = DC_AttackMaster.getAttackFromAction(action);
        int precalc = DamageCalculator.precalculateDamage(attack);

//        helper.doAction(action, new Context(unit, unit2), true);

        super.testAttack();
        Integer dmgDealt = action.getDamageDealt().getAmount();
        if (dmgDealt==null ){
            dmgDealt = Math.max(difE, difT);
        }
        JUnitUtils.assertEqualAndLog(
         precalc,
         dmgDealt,
         action + " dmg precalc",
         action + " Damage Dealt");

    }

    @Override
    protected DC_ActiveObj getAction() {
        return unit.getAttack().getSubActions().get(0);
    }
}
