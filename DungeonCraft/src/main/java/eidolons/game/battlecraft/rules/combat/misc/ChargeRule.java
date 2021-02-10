package eidolons.game.battlecraft.rules.combat.misc;

import eidolons.entity.active.DC_ActiveObj;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;

public class ChargeRule {
    public static Boolean checkRetainUnitTurn(DC_ActiveObj a) {
        if (a.getOwnerUnit().canActNow()) {
            if (a.getOwnerUnit().checkPassive(UnitEnums.STANDARD_PASSIVES.CHARGE)) {
                if (a.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
                    if (a.getGame().getState()
                     .getUnitActionStack(a.getOwnerUnit()).isEmpty()) {
                        return true;
                    }
                    return a.getGame().getState()
                            .getUnitActionStack(a.getOwnerUnit()).peek()
                            .getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.MOVE;
                }
            }
        }

        return false;
    }
}
