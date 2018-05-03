package eidolons.game.battlecraft.rules.counter.timed;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;

/**
 * Created by JustMe on 4/9/2018.
 */
public class DamageRuleTimer   {

    private DamageCounterRule damageCounterRule;
    private float period;
    private float timer;
    BattleFieldObject unit;

    public DamageRuleTimer(BattleFieldObject unit, DamageCounterRule damageCounterRule, float period) {
        this.damageCounterRule = damageCounterRule;
        this.period = period;
        this.unit = unit;
    }

    public void timePassed(float time) {
        timer+=time;
        if (!damageCounterRule.check(unit)){
            //TODO remove
            timer = 0;
            return;
        }
        while (timer>=getTimePeriod()){
            timer -= getTimePeriod();
            damageCounterRule.apply(unit);
            damageCounterRule.processPeriod(unit);
        }
    }

    protected float getTimePeriod() {
        return period;
    }

}
