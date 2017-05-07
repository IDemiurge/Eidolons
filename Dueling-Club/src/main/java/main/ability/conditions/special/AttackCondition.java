package main.ability.conditions.special;

import main.ability.effects.oneshot.attack.AttackEffect;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.game.battlecraft.ai.tools.target.EffectFinder;

public class AttackCondition extends MicroCondition {

    private Boolean counter;

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
                attackEffect = (AttackEffect) EffectFinder.getEffectsOfClass(
                        ref.getActive().getAbilities(), AttackEffect.class)
                        .get(0);
            } catch (Exception e) {
                e.printStackTrace();
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
