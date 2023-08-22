package combat;

import combat.battlefield.BattleField;
import combat.state.BattleData;
import combat.state.BattleState;
import combat.sub.BattleManager;

/**
 * Created by Alexander on 8/21/2023
 */
public abstract class BattleHandler {
    protected BattleManager manager;

    public BattleHandler(BattleManager battleManager) {
        this.manager = battleManager;
    }

    public void reset(){
    }

    public void resetAll() {
        manager.resetAll();
    }

    public BattleData getData() {
        return manager.getData();
    }

    public BattleField getField() {
        return manager.getField();
    }

    public BattleState getBattleState() {
        return manager.getBattleState();
    }
    // manager delegates getters
}
