package tests.logic.combat;

import eidolons.entity.item.DC_WeaponObj;
import main.content.enums.entity.ActionEnums;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created by JustMe on 5/4/2018.
 */
public class JUnitThrowWeapon extends ActionTest {

    private final String itemName="Iron Dagger";

    @Override
    @Test
    public void test( ) {
        DC_WeaponObj weapon = helper.equipWeapon(itemName);
        helper.resetAll();
        super.testAction(getActionName());
        helper.resetAll();

        if (helper.getHero().getMainWeapon()== weapon)
            fail(weapon+" is still in hand!");
        if (!game.getDroppedItemManager().getDroppedItems(unit2.getCoordinates()).contains(weapon))
            fail(weapon+" not dropped on the ground!");

        //TODO quick slots
    }

    @Override
    protected String getActionName() {
        return ActionEnums.THROW_MAIN;
//        return DC_ActionManager.getThrowName(itemName);
    }
}
