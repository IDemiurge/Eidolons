package main.ability.effects.oneshot.activation;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;

public class CostEffect extends MicroEffect  implements OneshotEffect {

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
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return active.getCosts().pay(ref);
    }

}
