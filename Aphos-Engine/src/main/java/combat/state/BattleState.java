package combat.state;

import combat.Battle;
import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.EntityRef;
import elements.exec.effect.Effect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleState extends BattleHandler {

    Map<EntityRef, Effect> effects = new LinkedHashMap<>();
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
        for (EntityRef ref : effects.keySet()) {
            effects.get(ref).apply(ref);
        }
    }

    public int getRound() {
        return round;
    }

    public void addEffect(EntityRef ref, Effect effect) {
        effects.put(ref, effect);
    }
}
