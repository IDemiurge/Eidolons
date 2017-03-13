package main.system.math;

import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.active.DC_ActiveObj;

/**
 * Created by JustMe on 3/12/2017.
 */
public class ModMaster {
    public static Integer getFinalModForAction(DC_ActiveObj action,
                                              PARAMETER mod) {
    return     MathMaster.applyModIfNotZero( action. getIntParam(mod),
         MathMaster.applyModIfNotZero(action. getActiveWeapon().getIntParam(mod),
          action.getOwnerObj().getIntParam(mod)));
    }

    public static int getFinalBonusForAction(DC_ActiveObj dc_activeObj, PARAMS bonus) {
        Integer value = dc_activeObj.getIntParam(bonus)
         + dc_activeObj.getActiveWeapon().getIntParam(bonus)
         + dc_activeObj.getOwnerObj().getIntParam(bonus);
        return value;
    }
}
