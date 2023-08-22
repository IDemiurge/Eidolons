package combat.state;

import combat.Battle;
import combat.BattleHandler;
import combat.sub.BattleManager;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleState extends BattleHandler {
    int round;
    // Seals seals;

    public BattleState(BattleManager battleManager) {
        super(battleManager);
    }

    @Override
    public void reset() {


    }
}
