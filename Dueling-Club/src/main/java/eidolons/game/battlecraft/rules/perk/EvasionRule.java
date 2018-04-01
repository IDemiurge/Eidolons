package eidolons.game.battlecraft.rules.perk;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import main.content.enums.entity.UnitEnums;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.text.LogManager;

public class EvasionRule {
    public static boolean checkMissed(DC_ActiveObj action) {
        DC_Obj source = action.getOwnerObj();
        Obj target = action.getRef().getTargetObj();
        if (source == null || target == null) {
            return false;
        }
        if (source.checkPassive(UnitEnums.STANDARD_PASSIVES.TRUE_STRIKE)) {
            return false;
        }
        int chance = getMissChance(action);
        if (chance <= 0) {
            return false;
        }
        // add cell's concealment value, but not the unit's!

        return RandomWizard.chance(chance);
    }

    public static int getMissChance(DC_ActiveObj action) {
        DC_Obj source = action.getOwnerObj();
        Obj target = action.getRef().getTargetObj();

        int chance = target.getIntParam(PARAMS.EVASION)
         - source.getIntParam(PARAMS.ACCURACY);
        return chance;
    }

    public static void logDodged(LogManager logManager, DC_ActiveObj activeObj) {
        logManager.log(StringMaster.getMessagePrefix(true,
         activeObj.getOwnerObj().getOwner().isMe())
         + StringMaster.getPossessive(activeObj.getOwnerObj().getName())
         + " "
         + activeObj.getDisplayedName()
         + " has beed dodged"
         + StringMaster.wrapInParenthesis(""
         + getMissChance(activeObj) + "%"));

    }


}
