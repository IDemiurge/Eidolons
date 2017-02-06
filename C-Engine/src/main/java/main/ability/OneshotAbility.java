package main.ability;

import main.ability.effects.Effect;
import main.elements.targeting.Targeting;
import main.entity.Ref;

public class OneshotAbility extends AbilityImpl {

    private boolean activated;

    public OneshotAbility(Targeting t, Effect e) {
        super(t, e);
    }

    @Override
    public boolean activate(Ref ref) {
        if (activated) {
            return false;
        }
        activated = true;
        return super.activate(ref);
    }
}
