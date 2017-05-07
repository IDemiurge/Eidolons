package main.ability.effects.containers;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.system.auxiliary.StringMaster;

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
        effects = (new Effects());
        String separator = StringMaster.AND_PROPERTY_SEPARATOR;
        if (!abilName.contains(separator)) {
            separator = StringMaster.AND_SEPARATOR;
        }
        for (String s : StringMaster.openContainer(abilName, separator)) {
            effects.addAll(EffectFinder.getEffectsFromAbilityType(s, ref));
        }
        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

}
