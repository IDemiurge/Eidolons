package eidolons.ability.effects.oneshot.rule;

import eidolons.ability.effects.DC_Effect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.round.UnconsciousRule;
import main.ability.effects.OneshotEffect;

public class UnconsciousFallEffect extends DC_Effect implements OneshotEffect {
    ModifyValueEffect valueModEffect;
    ModifyValueEffect valueModEffect2;

    public UnconsciousFallEffect() {
        valueModEffect = new ModifyValueEffect(PARAMS.C_FOCUS, MOD.SET, "0");
        valueModEffect2 = new ModifyValueEffect(PARAMS.C_INITIATIVE, MOD.SET, "" +
                UnconsciousRule.DEFAULT_ATB_FALL_TO);
    }

    @Override
    public boolean applyThis() {
        // [QUICK FIX] - more gracefully?
        new RemoveBuffEffect("Channeling").apply(ref);
          valueModEffect.apply(ref);
          valueModEffect2.apply(ref);
        return true;
    }

}
