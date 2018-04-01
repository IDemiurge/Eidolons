package eidolons.ability.effects.containers;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;

public class AbilityEffect extends MicroEffect {

    private String abilName;
    private Effects effects;
    private Ability ability;

    public AbilityEffect(String abilName) {
        this.abilName = abilName;
    }

    public AbilityEffect(Ability ability) {
        this.ability = ability;
    }

    @Override
    public boolean applyThis() {
        return getEffects().apply(ref);
    }

    public Effect getEffects() {
        if (effects != null) {
            return effects;
        }
        effects = EffectFinder.getEffectsFromAbilityString(abilName, ref);


        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

}
