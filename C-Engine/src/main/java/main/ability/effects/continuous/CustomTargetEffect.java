package main.ability.effects.continuous;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.ContainerEffect;
import main.ability.effects.oneshot.MicroEffect;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class CustomTargetEffect extends MicroEffect implements ContainerEffect {

    private Targeting t;
    private Effect effect;

    public CustomTargetEffect(Targeting t, Effect e) {
        this.t = t;
        this.effect = e;
    }

    @Override
    public boolean applyThis() {
        Ref REF = ref.getCopy();
        Integer first_target = null;
        if (REF.getTargetObj() != null) {
            first_target = REF.getTarget();
        }
        if (!t.select(REF))
            return false;
        if (first_target != null) {
            REF.setID(KEYS.CUSTOM_TARGET, first_target);
            REF.setTarget(REF.getTarget());
        }

        return effect.apply(REF);

    }

    @Override
    public String toString() {
        return effect + " with " + t;
    }

    @Override
    public Effect getEffect() {
        return effect;
    }
}
