package elements.exec.effect.generic;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;
import elements.exec.effect.framework.EffectResult;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/23/2023
 */
public class ContinuousEffect extends Effect {

    Effect effect;
    Condition retainCondition;

    public ContinuousEffect(Effect effect) {
        this.effect = effect;
    }

    public ContinuousEffect(Effect effect, Condition retainCondition) {
        this.effect = effect;
        this.retainCondition = retainCondition;
    }

    @Override
    public void applyThis(EntityRef ref) {
        combat().getBattleState().addEffect(ref, effect, retainCondition);
    }
}
