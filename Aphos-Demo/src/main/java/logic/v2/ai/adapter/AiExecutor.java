package logic.v2.ai.adapter;

import logic.v2.ai.generic.action.AiAction;
import logic.v2.entity.UnitRef;

/**
 * Created by Alexander on 1/21/2023
 */
public class AiExecutor {

    public void execute (AiAction action){
        UnitRef target = action.getTemplate().getTargeting().getTarget();
        String actionName = action.getTemplate().getProvider().getActionName(target);
        UnitRef source = action.getSource();
        getAction(actionName, source).var
    }

    private Object getAction(String actionName, UnitRef source) {
    }
}
