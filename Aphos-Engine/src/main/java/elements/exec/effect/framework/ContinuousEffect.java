package elements.exec.effect.framework;

import elements.exec.EntityRef;
import elements.exec.effect.Effect;

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
    public boolean apply(EntityRef ref) {
        combat().getBattleState().addEffect(ref, effect);
        return true;
    }
}
