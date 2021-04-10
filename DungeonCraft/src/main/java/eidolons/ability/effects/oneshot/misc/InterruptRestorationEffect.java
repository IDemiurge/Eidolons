package eidolons.ability.effects.oneshot.misc;

import eidolons.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.entity.obj.BuffObj;

public class InterruptRestorationEffect extends DC_Effect implements OneshotEffect {

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
