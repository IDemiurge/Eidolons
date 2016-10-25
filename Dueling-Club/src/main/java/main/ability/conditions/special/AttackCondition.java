package main.ability.conditions.special;

import main.ability.effects.AttackEffect;
import main.elements.conditions.MicroCondition;
import main.system.ai.logic.target.EffectMaster;

public class AttackCondition extends MicroCondition {

    private Boolean counter;

    public AttackCondition(Boolean counter) {
        this.counter = counter;
    }

    @Override
    public boolean check() {
        AttackEffect attackEffect = null;
        if (ref.getEffect() instanceof AttackEffect) {
            attackEffect = (AttackEffect) ref.getEffect();
        } else {
            try {
                attackEffect = (AttackEffect) EffectMaster.getEffectsOfClass(
                        ref.getActive().getAbilities(), AttackEffect.class)
                        .get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (attackEffect == null)
            return false;

        boolean result = false;
        if (counter != null) {
            result = attackEffect.getAttack().isCounter();
            if (!counter)
                result = !result;
        }

        return result;
    }

}
