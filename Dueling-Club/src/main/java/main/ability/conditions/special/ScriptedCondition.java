package main.ability.conditions.special;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

/**
 * Created by JustMe on 8/3/2017.
 */
public class ScriptedCondition extends MicroCondition {

    private Condition wrapped;
    private STANDARD_EVENT_TYPE type;
    private String args;

    public ScriptedCondition(STANDARD_EVENT_TYPE type, String args) {
        this.type = type;
        this.args = args;
        wrapped = ScriptParser.getDefaultCondition(type, args);
    }

    @Override
    public boolean check(Ref ref) {
        return wrapped.check(ref);
    }
}
