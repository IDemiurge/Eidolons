package main.ability.effects.container;

import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.system.auxiliary.RandomWizard;

public class RandomizedEffect extends MicroEffect {
    private Effects effects;

    public RandomizedEffect(
     // RANDOMIZATION_CONSTRAINTS RC, RANDOMIZATION_TYPE RT,
     Effects e) {
        this.effects = e;
    }

    @Override
    public boolean applyThis() {
        int index = RandomWizard.getRandomIntBetween(0, effects.getEffects()
         .size());
        return effects.getEffects().get(index).apply(ref);
    }

    public enum RANDOMIZATION_CONSTRAINTS {
        CONDITIONAL,
        NEVER_REPEAT,

    }

    public enum RANDOMIZATION_TYPE {
        ONE_OF,
        CHANCE_FOR_EACH,

    }
}
