package eidolons.macro.effect;

import main.ability.effects.EffectImpl;

public class MacroModeEffect extends EffectImpl {

    @Override
    public boolean applyThis() {
        // mutually exclusive...

        return false;
    }

}
