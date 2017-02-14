package main.rules.perk;

import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.PARAMS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;

public class EvasionRule {
    public static boolean checkMissed(DC_ActiveObj action) {
        DC_Obj source = action.getOwnerObj();
        Obj target = action.getRef().getTargetObj();
        if (source == null || target == null) {
            return false;
        }
        if (source.checkPassive(STANDARD_PASSIVES.TRUE_STRIKE)) {
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
}
