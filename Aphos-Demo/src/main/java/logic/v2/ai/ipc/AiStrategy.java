package logic.v2.ai.ipc;

import logic.v2.ai.generic.AiAction;
import logic.v2.ai.generic.AiActionTemplate;

/**
 * Created by Alexander on 1/21/2023
 *
 */
public class AiStrategy {


    public AiAction getAction() {
        /*
        pre-defined as template OR
         */
        AiActionTemplate template= null;
        if (template==null){
            template = getActionTemplate();

        }
        AiAction action= new AiAction(template);
        action.setStrategy();

        return action;
    }
}
