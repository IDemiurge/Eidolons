package main.ability.effects.special;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.MicroEffect;
import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.enums.entity.UnitEnums;

public class ExhaustionEffect extends MicroEffect {

    @Override
    public boolean applyThis() {
        getDefenseReductionEffect().apply(ref);
        new AddStatusEffect(UnitEnums.STATUS.EXHAUSTED).apply(ref);
        return true;
    }

	/*
     * status + defense penalty/no counter?
	 */

    private Effect getDefenseReductionEffect() {
        return null;
    }

}
