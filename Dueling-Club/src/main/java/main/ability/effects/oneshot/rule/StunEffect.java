package main.ability.effects.oneshot.rule;

import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;

public class StunEffect extends DC_Effect  implements OneshotEffect {
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
