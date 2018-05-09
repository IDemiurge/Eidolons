package tests.logic.combat;

import eidolons.game.battlecraft.rules.combat.attack.dual.DualWieldingRule;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 5/5/2018.
 */
public class JUnitDualWielding extends AttackTest {

    @Override
    public void test() {

        helper.equipWeapon("Iron Dagger", ITEM_SLOT.MAIN_HAND);
        helper.equipWeapon("Iron Dagger", ITEM_SLOT.OFF_HAND);
        atbHelper.startCombat();
        super.testAttack();
        assertTrue(unit.getBuff(DualWieldingRule.buffTypeNameMainHand)!=null );
//        unit.getAttacks(false).get(0).getCosts().getCost(PARAMS.N_OF_ACTIONS);


    }
}
