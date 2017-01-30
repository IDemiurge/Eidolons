package main.ability.effects.oneshot.common;

import main.ability.effects.DC_Effect;
import main.entity.obj.BuffObj;

public class InterruptEffect extends DC_Effect {

    @Override
    public boolean applyThis() {
        for (BuffObj b : getTarget().getBuffs()) {

            if (b.getGroupingKey().equalsIgnoreCase("Restoration")) {
                getTarget().removeBuff(b.getName());
            }
        }

        return false;
    }

}
