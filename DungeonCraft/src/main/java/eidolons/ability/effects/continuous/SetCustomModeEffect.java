package eidolons.ability.effects.continuous;

import eidolons.entity.obj.unit.Unit;
import main.ability.effects.MicroEffect;
import main.content.mode.MODE;

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
