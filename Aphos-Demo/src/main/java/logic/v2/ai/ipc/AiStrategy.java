package logic.v2.ai.ipc;

import logic.v2.ai.adapter.AiEvent;
import logic.v2.ai.generic.AiConsts;
import logic.v2.ai.generic.AiElement;
import logic.v2.ai.generic.action.AiAction;
import logic.v2.ai.generic.action.AiActionTemplate;
import logic.v2.entity.UnitRef;
import main.game.logic.event.Event;

/**
 * Created by Alexander on 1/21/2023
 *
 * sequence or provider
 *
 */
public abstract class AiStrategy extends AiElement {
  protected  AiActionTemplate presetTemplate;

    public AiStrategy(UnitRef source) {
        super(source);
    }

    public void processEvent(AiEvent event){
        //TODO may define template or..
        if (!event.getEventType().typeOf(getEventType()))
            return;

    }

    protected abstract Object getEventType();

    public AiAction getAction() {
        /*
        //TODO
        pre-defined as template OR ???
         */
        AiActionTemplate template= getActionTemplate();
        if (template==null){
            return createIndecision();
        }

        AiAction action= new AiAction(template, source);
        // action.setStrategy();

        return action;
    }
    private AiActionTemplate getActionTemplate() {
        return presetTemplate;
    }

    private AiAction createIndecision() {
        return new AiAction(AiConsts.INDECISION, source);
    }

}
