package eidolons.ability.effects.containers;

import eidolons.game.core.master.EffectMaster;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;

public class AbilityEffect extends MicroEffect {

    private String abilName;
    private Effects effects;

    public AbilityEffect(String abilName) {
        this.abilName = abilName;
    }

    public AbilityEffect(Ability ability) {
    }

    @Override
    public boolean applyThis() {
        return getEffects().apply(ref);
    }

    public Effect getEffects() {
        if (effects != null) {
            return effects;
        }
        effects = EffectMaster.getEffectsFromAbilityString(abilName, ref);


        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

}
