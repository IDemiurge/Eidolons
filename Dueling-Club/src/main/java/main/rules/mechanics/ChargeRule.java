package main.rules.mechanics;

import main.content.CONTENT_CONSTS.ACTION_TYPE_GROUPS;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.entity.obj.top.DC_ActiveObj;

public class ChargeRule {
    public static Boolean checkRetainUnitTurn(DC_ActiveObj a) {
        if (a.getOwnerObj().canActNow())
            if (a.getOwnerObj().checkPassive(STANDARD_PASSIVES.CHARGE)) {
                if (a.getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
                    if (a.getGame().getState()
                            .getUnitActionStack(a.getOwnerObj()).isEmpty())
                        return true;
                    if (a.getGame().getState()
                            .getUnitActionStack(a.getOwnerObj()).peek()
                            .getActionGroup() != ACTION_TYPE_GROUPS.MOVE)
                        return true;
                }
            }

        return false;
    }
}
