package eidolons.ability.ignored.special;

import main.ability.effects.MicroEffect;
import main.ability.effects.TriggeredEffect;

//Unimplemented
public class RedirectEffect extends MicroEffect implements TriggeredEffect {

    // OR JUST MAKE IT OUT OF DUPLICATE EFFECT
    public RedirectEffect(BIND_FILTER filter) {
        this.setIgnoreGroupTargeting(true);
        setAltering(true);
    }

    @Override
    public boolean applyThis() {
        // TODO Auto-generated method stub
        return false;
    }
}
