package combat.state;

import combat.Battle;
import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;
import elements.exec.effect.framework.TargetedEffect;

import java.util.*;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleState extends BattleHandler {
    List<TargetedEffect> effects = new ArrayList<>();
    // Map<EntityRef, Effect> effects = new LinkedHashMap<>();
    int round;
    // Seals seals;

    public BattleState(BattleManager battleManager) {
        super(battleManager);
    }

    @Override
    public void newRound() {
        round++;
    }

    @Override
    public void reset() {
        //check remove
        effects.removeIf(ef-> ef.checkRemove());
        for (TargetedEffect ef : effects) {
            ef.apply();
        }
    }

    public int getRound() {
        return round;
    }

    public void addEffect(EntityRef ref, Effect effect, Condition retainCondition) {
        effects.add(new TargetedEffect(ref, effect).setRetainCondition(retainCondition));
    }
}
