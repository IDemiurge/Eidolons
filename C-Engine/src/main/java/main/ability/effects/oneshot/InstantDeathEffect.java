package main.ability.effects.oneshot;

import main.ability.effects.OneshotEffect;
import main.ability.effects.ResistibleEffect;
import main.ability.effects.MicroEffect;

public class InstantDeathEffect extends MicroEffect implements ResistibleEffect, OneshotEffect {

    private Boolean quietly;
    private boolean leaveCorpse;

    public InstantDeathEffect(Boolean leaveCorpse, Boolean quietly) {
        this.leaveCorpse = leaveCorpse;
        this.quietly = quietly;
    }

    @Override
    public boolean applyThis() {
        return ref.getTargetObj()
                .kill(ref.getSourceObj(), leaveCorpse, quietly);
    }

}
