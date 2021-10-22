package eidolons.game.core.atb;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import main.entity.obj.BuffObj;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbMaster {
    public static float getDisplayedReadinessCost(DC_ActiveObj action) {
        return getReadinessCost(action) / 100;
    }

    public static float getReadinessCost(DC_ActiveObj action) {
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
        action.getOwnerUnit().modifyParameter(PARAMS.C_ATB,
                -initiativeCost + "", 0, false);

        ((AtbTurnManager) action.getGame().getTurnManager()).getAtbController().processAtbRelevantEvent();


        return initiativeCost;
    }

    public static Integer getReadiness(Unit unit) {
        return unit.getIntParam(PARAMS.C_ATB);
    }

    public static int getDisplayedAtb(BattleFieldObject obj) {
        return (int) (obj.getIntParam(PARAMS.C_ATB) / AtbController.TIME_LOGIC_MODIFIER);
    }
}
