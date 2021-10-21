package eidolons.game.battlecraft.rules.counter.generic;

import eidolons.ability.EventAndCondition;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.saves.SavesMaster;
import eidolons.game.core.game.DC_Game;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.system.math.Formula;

/*
max 9 counters
counter dynamics
 */
public abstract class CounterRule_10Up extends DC_CounterRule {
    public CounterRule_10Up(DC_Game game) {
        super(game);
    }

    protected void add(BattleFieldObject target, int n) {
        Integer cur = getNumberOfCounters(target);
        int val = cur + n;
        int upgraded = 0, modify = 0;
        if (val >= 10) {
            upgraded = val / 10;
            val -= upgraded * 10;
            modify = val - cur;
            target.modifyCounter(getUpgraded(), upgraded);
            if (getSaveType() != null) {
                Ref ref= Ref.getSelfTargetingRefCopy(target);
                Integer value = new Formula(getSaveAmount()).getInt(ref);
                boolean result = SavesMaster.savingThrow(getSaveType(), target, value);
                if (isLoseAllOnSave()){

                }
                //roll to remove all or ... get worse
            }
        }
        target.modifyCounter(getCounter(), modify);


    }

    protected abstract String getSaveAmount();

    protected EventAndCondition getCustomDynamics() {
        return null;
    }

    protected String getCustomDynamicsAmount() {
        return null;
    }

    protected boolean isEffectAccumulated() {
        return false;
    }

    protected SavesMaster.SaveType getSaveType() {
        return null;
    }

    protected EventAndCondition getUpgradedCustomDynamics() {
        return null;
    }

    /*
    A Save roll may be required upon 10=>1 upgrade
     */
    protected boolean isLoseAllOnSave() {
        return false;
    }

    protected UnitEnums.COUNTER getUpgraded() {
        return getCounter().getUpgraded();
    }


}
