package main.ability.effects.oneshot.rule;

import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.oneshot.buff.RemoveBuffEffect;
import main.content.PARAMS;

public class UnconsciousFallEffect extends DC_Effect implements OneshotEffect {
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
