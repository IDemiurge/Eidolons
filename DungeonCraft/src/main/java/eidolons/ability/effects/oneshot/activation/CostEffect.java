package eidolons.ability.effects.oneshot.activation;

import eidolons.entity.active.ActiveObj;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;

public class CostEffect extends MicroEffect implements OneshotEffect {

    private ActiveObj active;
    private KEYS key;

    @OmittedConstructor
    public CostEffect(ActiveObj active) {
        this.active = active;
    }

    public CostEffect(KEYS key) {
        this.key = key;
    }

    @Override
    public boolean applyThis() {
        if (active == null) {
            try {
                active = (ActiveObj) ref.getObj(key);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return active.getCosts().pay(ref);
    }

}
