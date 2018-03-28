package main.game.battlecraft.rules.combat.misc;

import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.active.DC_ActiveObj;

public class ChargeRule {
    public static Boolean checkRetainUnitTurn(DC_ActiveObj a) {
        if (a.getOwnerObj().canActNow()) {
            if (a.getOwnerObj().checkPassive(UnitEnums.STANDARD_PASSIVES.CHARGE)) {
                if (a.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
                    if (a.getGame().getState()
                     .getUnitActionStack(a.getOwnerObj()).isEmpty()) {
                        return true;
                    }
                    if (a.getGame().getState()
                     .getUnitActionStack(a.getOwnerObj()).peek()
                     .getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
