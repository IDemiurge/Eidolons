package main.ability.effects.special;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.MicroEffect;
import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.CONTENT_CONSTS.STATUS;

public class ExhaustionEffect extends MicroEffect {

    @Override
    public boolean applyThis() {
        getDefenseReductionEffect().apply(ref);
        new AddStatusEffect(STATUS.EXHAUSTED).apply(ref);
        return true;
    }

	/*
     * status + defense penalty/no counter?
	 */

    private Effect getDefenseReductionEffect() {
        return null;
    }

}
