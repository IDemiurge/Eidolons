package tests.logic.combat;

import main.content.enums.entity.ActionEnums;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/5/2018.
 */
public class JUnitAoO extends ActionTest{

    @Override
    public void test() {
        //override roll?
        super.test();
        //interrupted?

        //method called?
        //action stack ?
        // param check?

    }

    @Override
    protected String getActionName() {
        return StringMaster.getWellFormattedString(ActionEnums.STD_SPEC_ACTIONS.Use_Inventory.toString());
    }
}
