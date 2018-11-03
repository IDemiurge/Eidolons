package eidolons.game.core.atb;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.obj.BuffObj;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbMaster {
    public static float getReadinessCost(DC_ActiveObj action) {
        if (action.isExtraAttackMode()) {
            return 0;
        }
        return (float) (
         action.getParamDouble(PARAMS.AP_COST)
          * AtbController.ATB_READINESS_PER_AP);
    }

    public static float getImmobilizingBuffsMaxDuration(Unit unit) {
        double max = 0;
        for (BuffObj buff : unit.getBuffs()) {
            if (buff.isImmobilizing()) {
                double duration = buff.getDuration();
                if (duration > max)
                    max = duration;
            }
        }
        return (float) max;
    }

    public static double reduceReadiness(DC_ActiveObj action) {
        float initiativeCost = getReadinessCost(action);
        if (initiativeCost <= 0)
            return 0;
        action.getOwnerUnit().modifyParameter(PARAMS.C_INITIATIVE,
         -initiativeCost + "", 0, false);

        ((AtbTurnManager) action.getGame().getTurnManager()).getAtbController().processAtbRelevantEvent();


        return initiativeCost;
    }

    public static Integer getReadiness(Unit unit) {
        return unit.getIntParam(PARAMS.C_INITIATIVE);
    }
}
