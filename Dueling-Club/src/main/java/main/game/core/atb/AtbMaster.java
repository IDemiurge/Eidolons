package main.game.core.atb;

import main.content.PARAMS;
import main.entity.active.DC_ActiveObj;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbMaster {
    public static float getReadinessCost(DC_ActiveObj action) {
        return (float) (
         action.getParamDouble(PARAMS.AP_COST)
          * AtbController.ATB_MOD * AtbController.TIME_IN_ROUND / 100);
    }
}
