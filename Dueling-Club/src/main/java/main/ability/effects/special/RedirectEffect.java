package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;

public class RedirectEffect extends MicroEffect {

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
