package eidolons.game.battlecraft.rules.counter.generic.timed;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 4/9/2018.
 */
public interface TimedEffect {
    //how to clear this cache?
    Map<TimedEffect, Map<BattleFieldObject, DamageRuleTimer>> cache = new HashMap<>();

    default void timePassed(float time, BattleFieldObject object) {
        Map<BattleFieldObject, DamageRuleTimer> map = cache.get(this);
        if (map == null) {
            cache.put(this, map= new HashMap<>());
        }
        DamageRuleTimer timer = map.get(object);
        if (timer == null) {
            map.put(object, timer = createTimer(object));
        }
        timer.timePassed(time);
    }

    default DamageRuleTimer createTimer(BattleFieldObject object) {
        return new DamageRuleTimer(object, getDamageRule(), getTimePeriod());
    }

    default float getTimePeriod() {
        return 1f;
   }

    default DamageCounterRule getDamageRule() {
        return (DamageCounterRule) this;
    }

}
