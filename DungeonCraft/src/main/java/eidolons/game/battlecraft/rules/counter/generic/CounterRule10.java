package eidolons.game.battlecraft.rules.counter.generic;

import eidolons.ability.EventAndCondition;
import eidolons.game.battlecraft.rules.saves.SavesMaster;
import eidolons.game.core.game.DC_Game;

/*
max 9 counters
counter dynamics
 */
public abstract class CounterRule10 extends DC_CounterRule{
    public CounterRule10(DC_Game game) {
        super(game);
    }

    protected abstract EventAndCondition getCustomDynamics();
    protected abstract String getCustomDynamicsAmount();

    protected abstract boolean isEffectAccumulated();
    protected abstract SavesMaster.SaveType getSaveType();
    protected abstract EventAndCondition getUpgradedCustomDynamics();
    protected abstract boolean isLoseAllOnSave();

    protected  String getUpgradedName() {
        return getCounter().getUpgraded().getName();
    }



}
