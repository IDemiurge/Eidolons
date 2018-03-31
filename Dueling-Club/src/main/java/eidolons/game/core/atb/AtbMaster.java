package eidolons.game.core.atb;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbMaster {
    public static float getReadinessCost(DC_ActiveObj action) {
        return (float) (
         action.getParamDouble(PARAMS.AP_COST)
          * AtbController.ATB_MOD * AtbController.TIME_TO_READY / 100);
    }
}
