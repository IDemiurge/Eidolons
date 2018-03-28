package main.ability.effects.oneshot.common;

import main.ability.effects.oneshot.attack.force.ForceEffect;

/**
 * Created by JustMe on 4/30/2017.
 */
public class WindEffect extends ForceEffect {

    public WindEffect(String forceFormula) {
        super(forceFormula, false);
    }

    @Override
    public boolean applyThis() {
//        getTarget().modifyCounter(COUNTER.Blaze, counterMod);
//        getTarget().modifyCounter(COUNTER.Moist, counterMod);
//        getTarget().modifyCounter(COUNTER.Suffocation, counterMod);
//        getTarget().modifyCounter(COUNTER.Suffocation, counterMod);
//        getTarget().modifyCounter(COUNTER.Ash, counterMod);

        //vertigo
        //force turn
        //increase Lift Height

        return super.applyThis();
    }
}
