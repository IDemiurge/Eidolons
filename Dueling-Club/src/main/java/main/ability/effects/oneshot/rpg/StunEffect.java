package main.ability.effects.oneshot.rpg;

import main.ability.effects.DC_Effect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.PARAMS;

public class StunEffect extends DC_Effect {
    public StunEffect(String potential) {

    }

    public StunEffect(String potential, Boolean noFocusLoss, Boolean noActionLoss,
                      Boolean noInitiative) {

    }

    @Override
    public boolean applyThis() {

//        new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST, focus).apply(ref);
//        new ModifyValueEffect(PARAMS.C_N_OF_ACTIONS, MOD.MODIFY_BY_CONST, ap).apply(ref);
//        new ModifyValueEffect(PARAMS.C_INITIATIVE_BONUS, MOD.MODIFY_BY_CONST, initiative)
//                .apply(ref);

        // STATUS.STUNNED TODO ?

        return true;
    }

}
