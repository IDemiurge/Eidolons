package main.ability.effects.oneshot.special;

import main.ability.effects.ResistibleEffect;
import main.ability.effects.oneshot.MicroEffect;

public class InstantDeathEffect extends MicroEffect implements ResistibleEffect {

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
