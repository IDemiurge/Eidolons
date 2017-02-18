package main.ability.effects;

import main.ability.effects.oneshot.MicroEffect;
import main.content.mode.MODE;
import main.entity.obj.unit.Unit;

public class SetCustomModeEffect extends MicroEffect {

    private MODE mode;

    public SetCustomModeEffect(MODE mode) {
        this.mode = mode;
    }

    @Override
    public boolean applyThis() {
        ((Unit) ref.getTargetObj()).setMode(mode);

        return true;
    }

}
