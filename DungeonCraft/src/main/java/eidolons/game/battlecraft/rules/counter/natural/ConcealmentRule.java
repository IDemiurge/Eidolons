package eidolons.game.battlecraft.rules.counter.natural;

import eidolons.content.PARAMS;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import main.content.enums.rules.VisionEnums;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.text.LogManager;

public class ConcealmentRule {
    /*
     * add buff with a passive dodge ability addPassive effect?
	 * trigger/continuous effect to be removed...
	 * 
	 * or maybe I could hard-code it somewhere almost like resistance?
	 * 
	 * Only for *ranged touch* actions, which could be a STD spell/action
	 * passive or tag or classif.
	 */


    public static boolean checkMissed(ActiveObj action) {
        Unit source = action.getOwnerUnit();
        Obj target = action.getRef().getTargetObj();
        if (source == null || target == null) {
            return false;
        }

        if (source.getVisionMode() == VisionEnums.VISION_MODE.INFRARED_VISION) {

        }
        int chance = getMissChance(action);

        if (chance <= 0) {
            return false;
        }
        // add cell's concealment value, but not the unit's!

        return RandomWizard.chance(chance);
    }

    // DEPENDING ON VISIBILITY_LEVEL?
    public static int getMissChance(ActiveObj action) {
        DC_Obj source = action.getOwnerUnit();
        Obj target = action.getRef().getTargetObj();
        Obj cell = source.getGame().getCell(source.getCoordinates());
        // if (source.checkPassive(STANDARD_PASSIVES.DARKVISION))
        // return false;
        // if (source.checkPassive(STANDARD_PASSIVES.LIGHTVISION))
        // return false;
        int chance = target.getIntParam(PARAMS.CONCEALMENT) - source.getIntParam(PARAMS.DETECTION)
//                - source.getIntParam(PARAMS.PERCEPTION) / 2
//         - target.getIntParam(PARAMS.NOISE) / 2
         - source.getIntParam(PARAMS.ACCURACY)
         - source.getIntParam(PARAMS.ILLUMINATION)
         // if
         // normal
         // vision...
         + cell.getIntParam(PARAMS.CONCEALMENT);
        if (chance < 0) {
            chance = 0;
        }
        chance -= source.getIntParam(PARAMS.ILLUMINATION);
        if (chance < 0) {
            chance += 500;
            if (chance < 0)
                return -(chance) / 2;
            else
                return 0;
        }
        return (chance);
    }

    public static void logMissed(LogManager logManager, ActiveObj activeObj) {
        logManager.log(StringMaster.getMessagePrefix(true,
         activeObj.getOwnerUnit().getOwner().isMe())
         + StringMaster.getPossessive(activeObj.getOwnerUnit().getName())
         + " "
         + activeObj.getDisplayedName()
         + " has missed due to Concealment"
         + StringMaster.wrapInParenthesis(""
         + getMissChance(activeObj) + "%"));

    }

    public static boolean isConcealed(Unit source, BattleFieldObject object) {
        return false;
    }
}
