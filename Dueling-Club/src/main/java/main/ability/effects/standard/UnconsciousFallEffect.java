package main.ability.effects.standard;

import main.ability.effects.DC_Effect;
import main.ability.effects.RemoveBuffEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.PARAMS;

public class UnconsciousFallEffect extends DC_Effect {
    ModifyValueEffect valueModEffect;

    public UnconsciousFallEffect() {
        valueModEffect = new ModifyValueEffect(PARAMS.C_FOCUS, MOD.SET, "0");
    }

    @Override
    public boolean applyThis() {
        // [QUICK FIX] - more gracefully?
        new RemoveBuffEffect("Channeling").apply(ref);
        return valueModEffect.apply(ref);
    }

}
