package main.ability.effects.common;

import main.ability.effects.oneshot.special.InstantDeathEffect;
import main.rules.mechanics.FleeRule;

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
