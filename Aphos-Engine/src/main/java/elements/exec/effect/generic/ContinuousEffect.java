package elements.exec.effect.generic;

import elements.exec.EntityRef;
import elements.exec.effect.Effect;
import elements.exec.effect.framework.EffectResult;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/23/2023
 */
public class ContinuousEffect extends Effect {

    Effect effect;

    public ContinuousEffect(Effect effect) {
        this.effect = effect;
    }

    @Override
    public void applyThis(EntityRef ref) {
        combat().getBattleState().addEffect(ref, effect);
    }
}
