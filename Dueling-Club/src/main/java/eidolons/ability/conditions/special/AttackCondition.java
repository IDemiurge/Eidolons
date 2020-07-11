package eidolons.ability.conditions.special;

import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.game.core.master.EffectMaster;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class AttackCondition extends MicroCondition {

    private final Boolean counter;

    public AttackCondition(Boolean counter) {
        this.counter = counter;
    }

    @Override
    public boolean check(Ref ref) {
        AttackEffect attackEffect = null;
        if (ref.getEffect() instanceof AttackEffect) {
            attackEffect = (AttackEffect) ref.getEffect();
        } else {
            try {
                attackEffect = (AttackEffect) EffectMaster.getEffectsOfClass(
                 ref.getActive().getAbilities(), AttackEffect.class)
                 .get(0);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (attackEffect == null) {
            return false;
        }

        boolean result = false;
        if (counter != null) {
            result = attackEffect.getAttack().isCounter();
            if (!counter) {
                result = !result;
            }
        }

        return result;
    }

}
