package logic.v2.ai.generic.action;

import logic.v2.entity.UnitRef;

/**
 * Created by Alexander on 1/21/2023
 */
public class AiAction {

    private final AiActionTemplate template;

    public AiAction(AiActionTemplate template, UnitRef source) {
        this.template = template;
    }

    public AiAction(String indecision, UnitRef source) {
        this(new AiActionTemplate(), source);
    }

    public AiActionTemplate getTemplate() {
        return template;
    }
}
