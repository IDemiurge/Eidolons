package logic.execution;

import elements.EntityRef;
import framework.entity.sub.UnitAction;

/**
 * Created by Alexander on 8/21/2023
 */
public class ActionExecutor {

    public boolean actionApplies(UnitAction action, TargetGroup targets){
        new EntityRef(action.getUnit());
    }
}
