package eidolons.ability.conditions.special;

import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

/**
 * Created by JustMe on 8/3/2017.
 */
public class ScriptedCondition extends MicroCondition {

    private final Condition wrapped;

    public ScriptedCondition(STANDARD_EVENT_TYPE type, String args) {
        wrapped = ScriptParser.getDefaultCondition(type, args);
    }

    @Override
    public boolean check(Ref ref) {
        return wrapped.check(ref);
    }
}
