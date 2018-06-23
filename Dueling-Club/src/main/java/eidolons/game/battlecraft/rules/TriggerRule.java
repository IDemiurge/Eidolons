package eidolons.game.battlecraft.rules;

import main.elements.triggers.Trigger;
import main.game.core.game.GenericGame;

public abstract class TriggerRule {

    private Trigger trigger;
    private boolean disabled;

    public Trigger getTrigger() {
        return trigger;
    }

    public void init(GenericGame game) {
        if (trigger == null) {
            initTrigger();
        }
        game.getState().addTrigger(getTrigger());

    }

    public abstract void initTrigger();

    public void removeFrom(GenericGame game) {
        if (trigger == null) {
            initTrigger();
        }
        game.getState().removeTrigger(getTrigger());

    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
