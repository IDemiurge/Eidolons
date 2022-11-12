package eidolons.ability.effects.oneshot.status;

import main.ability.effects.Effect;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;

public class ExhaustionEffect extends AddStatusEffect {

    public ExhaustionEffect() {
        super(UnitEnums.STATUS.EXHAUSTED);
    }

    @Override
    public boolean applyThis() {
//        getDefenseReductionEffect().apply(ref);
        return super.applyThis();
    }

	/*
     * status + defense penalty/no counter?
	 */

    private Effect getDefenseReductionEffect() {
        return null;
    }

}
