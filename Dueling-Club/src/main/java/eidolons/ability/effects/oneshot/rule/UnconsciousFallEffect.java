package eidolons.ability.effects.oneshot.rule;

import eidolons.ability.effects.DC_Effect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.content.PARAMS;
import main.ability.effects.OneshotEffect;

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
