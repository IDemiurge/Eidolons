package main.ability.effects;

import main.ability.effects.oneshot.MicroEffect;
import main.content.enums.MODE;
import main.entity.obj.DC_HeroObj;

public class SetCustomModeEffect extends MicroEffect {

    private MODE mode;

    public SetCustomModeEffect(MODE mode) {
        this.mode = mode;
    }

    @Override
    public boolean applyThis() {
        ((DC_HeroObj) ref.getTargetObj()).setMode(mode);

        return true;
    }

}
