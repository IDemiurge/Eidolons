package main.ability.effects.containers;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.MicroEffect;
import main.ability.effects.container.ConditionalEffect;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.elements.conditions.NumericCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

public class EnergyCostEffect extends MicroEffect {

    private Effects effects;
    private KEYS key;
    private int cost;

    public EnergyCostEffect(int cost, KEYS key, Effects effects) {
        this.cost = cost;
        this.key = key;
        this.effects = effects;
        effects.add(getReduceEnergyEffect());
    }

    @Override
    public boolean applyThis() {
        // effects.add(getReduceEnergyEffect());
        return new ConditionalEffect(getEnergyCondition(), effects).apply(ref);
    }

    private Condition getEnergyCondition() {
        return new NumericCondition(StringMaster.getValueRef(key,
                PARAMS.C_ENERGY), "" + cost);
    }

    private Effect getReduceEnergyEffect() {
        return new CustomTargetEffect(new FixedTargeting(key),
                new ModifyValueEffect(PARAMS.C_ENERGY,
                        MOD.MODIFY_BY_CONST, "-" + cost));

    }

}
