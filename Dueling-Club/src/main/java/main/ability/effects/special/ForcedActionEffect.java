package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.DC_HeroObj;
import main.game.ai.elements.actions.Action;

public class ForcedActionEffect extends MicroEffect {

    private String actionName;
    private KEYS key;

    public ForcedActionEffect(String actionName) {
        this.actionName = actionName;
    }

    public ForcedActionEffect(String actionName, KEYS target) {
        this(actionName);
        this.key = target;
    }

    @Override
    public boolean applyThis() {

        DC_HeroObj unit = (DC_HeroObj) ref.getTargetObj();
        Ref REF = unit.getRef();
        if (key != null) {
            REF.setTarget(ref.getId(key));
        }

        Action action = new Action(unit.getAction(actionName), REF);
        unit.getGame().getAiManager().getAI(unit).getForcedActions()
                .add(action);
        return true;
    }
}
