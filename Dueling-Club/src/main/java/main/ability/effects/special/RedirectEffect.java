package main.ability.effects.special;

import main.ability.effects.MicroEffect;
import main.ability.effects.TriggeredEffect;
import main.test.Unimplemented;

@Unimplemented
public class RedirectEffect extends MicroEffect implements TriggeredEffect{

    private BIND_FILTER filter;

    // OR JUST MAKE IT OUT OF DUPLICATE EFFECT
    public RedirectEffect(BIND_FILTER filter) {
        this.filter = filter;
        this.setIgnoreGroupTargeting(true);
        setAltering(true);
    }

    @Override
    public boolean applyThis() {
        // TODO Auto-generated method stub
        return false;
    }
}
