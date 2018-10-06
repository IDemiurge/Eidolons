package eidolons.game.battlecraft.logic.meta.scenario.script;

import main.ability.Ability;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
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


    public String getScriptText() {
        return scriptText;
    }

    @Override
    public String toString() {
        return "Script Trigger: " + getScriptText();

    }
}
