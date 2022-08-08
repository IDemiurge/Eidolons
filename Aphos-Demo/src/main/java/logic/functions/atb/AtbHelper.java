package logic.functions.atb;

import logic.core.Aphos;
import main.system.auxiliary.NumberUtils;

import static logic.content.consts.CombatConsts.ATB_PER_INITIATIVE_PER_SEC;
import static logic.content.consts.CombatConsts.ATB_TO_READY;

public class AtbHelper {
    protected static float getAtbGain(Float time, AtbEntity unit) {
        return time * unit.getInitiative() * ATB_PER_INITIATIVE_PER_SEC;
    }
    protected static float getTimedToGainAtb(Float time, AtbEntity unit) {
        return time / unit.getInitiative() / ATB_PER_INITIATIVE_PER_SEC;
    }

    protected static String getTimeString(float v) {
        return NumberUtils.formatFloat(1, v) + " s. left";
    }

    protected static float calculateTimeTillTurn(AtbEntity unit) {
        float time = getTimedToGainAtb((ATB_TO_READY - unit.getAtbReadiness()), unit);
        //TODO
//        if (unit.isImmobilized()) {
//            float duration = AtbMaster.getImmobilizingBuffsMaxDuration(unit.getEntity());
//            if (duration == 0) {
//                if (unit.getEntity().getBuff("channeling") != null) {
//                    if (unit.getAtbReadiness() >= 9.99f) {
//                        return 0;
//                    }
//                }
//                return Float.MAX_VALUE;
//            }
//            return (time + duration);
//        }
        return time;
    }

    protected static int compareForSort(AtbEntity first, AtbEntity second) {
        if (first.getTimeTillTurn() == second.getTimeTillTurn())
            return first.getEntity() == Aphos.hero
                    ? -1 :
                    second.getEntity() == Aphos.hero ? 1 : 0; //EA check
        if (first.getTimeTillTurn() < second.getTimeTillTurn())
            return -1;
        else
            return 1;
    }
}
