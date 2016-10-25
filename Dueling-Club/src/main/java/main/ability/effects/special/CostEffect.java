package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.obj.top.DC_ActiveObj;

public class CostEffect extends MicroEffect {

    private DC_ActiveObj active;
    private KEYS key;

    @OmittedConstructor
    public CostEffect(DC_ActiveObj active) {
        this.active = active;
    }

    public CostEffect(KEYS key) {
        this.key = key;
    }

    @Override
    public boolean applyThis() {
        if (active == null) {
            try {
                active = (DC_ActiveObj) ref.getObj(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return active.getCosts().pay(ref);
    }

}
