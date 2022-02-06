package eidolons.system.math;

import eidolons.content.PARAMS;
import eidolons.entity.feat.active.ActiveObj;
import main.content.values.parameters.PARAMETER;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class ModMaster {
    public static Integer getFinalModForAction(ActiveObj action,
                                               PARAMETER mod) {
        return MathMaster.applyModIfNotZero(action.getIntParam(mod),
         MathMaster.applyModIfNotZero(action.getActiveWeapon().getIntParam(mod),
          action.getOwnerUnit().getIntParam(mod)));
    }

    public static int getFinalBonusForAction(ActiveObj dc_activeObj, PARAMS bonus) {
        return dc_activeObj.getIntParam(bonus)
         + dc_activeObj.getActiveWeapon().getIntParam(bonus)
         + dc_activeObj.getOwnerUnit().getIntParam(bonus);
    }
}
