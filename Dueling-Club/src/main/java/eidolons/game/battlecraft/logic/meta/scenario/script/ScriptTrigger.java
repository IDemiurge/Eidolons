package eidolons.game.battlecraft.logic.meta.scenario.script;

import main.ability.Ability;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;

/**
 * Created by JustMe on 5/18/2017.
 */
public class ScriptTrigger extends Trigger {

    private String scriptText;

    public ScriptTrigger(String originalText, EVENT_TYPE eventType, Condition conditions, Ability abilities) {
        super(eventType, conditions, abilities);
        scriptText = originalText;
    }

    public boolean isRemoveOnReset() {
        return false;
    }

    @Override
    public boolean trigger() {
        if (ScriptMaster.isScriptsOff())
            return false;
        boolean result = super.trigger();
        return result;
    }

    @Override
    public boolean isRemoveAfterTriggers(boolean result) {
        if (result)
        if (isOneshot()) {
            return true;
        }
        return false;
    }

    private boolean isOneshot() {
        return !ScriptParser.TEST_MODE;
    }

    @Override
    public boolean check(Event event) {
        return super.check(event);
    }

    @Override
    protected void remove() {
        super.remove();
    }

    @Override
    protected Ref getRef(Event event) {
        return event.getRef();
    }

    public String getScriptText() {
        return scriptText;
    }

    @Override
    public String toString() {
        return "Script Trigger: " + getScriptText();

    }
}
