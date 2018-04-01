package eidolons.ability.effects.common;

import main.ability.effects.oneshot.InstantDeathEffect;

@Deprecated
public class FleeEffect extends InstantDeathEffect {

    public FleeEffect() {
        super(false, false);
    }

    @Override
    public boolean applyThis() {
//        FleeRule.flee(ref);

        return super.applyThis();
    }
}
