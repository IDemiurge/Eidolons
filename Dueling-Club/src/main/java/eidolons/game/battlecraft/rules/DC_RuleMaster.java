package eidolons.game.battlecraft.rules;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.main.pale.PaleAspect;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 4/9/2018.
 */
public class DC_RuleMaster {
    private final DC_Rules rules;
    private final DC_Game game;

    public DC_RuleMaster(DC_Game game, DC_Rules rules) {
        this.game = game;
        this.rules = rules;
    }

    public static boolean isFocusReqsOff() {
        return true;
    }

    public static boolean isToughnessReduced() {
        return true;
    }

    public void timePassed(Float time) {
        if (CoreEngine.TEST_LAUNCH) {
            return;
        }
        for (BattleFieldObject object : game.getBfObjects()) {

            if (object.isPale() != PaleAspect.ON)
                continue;

            if (object.isResetIgnored()) {
                continue;
            }
            for (DamageCounterRule rule : rules.getTimedRules().keySet()) {
                if (rule.checkApplies(object)) {
                    TimedRule timedRule = rules.getTimedRules().get(rule);
                    timedRule.timePassed(time, object);
                }

            }
        }
    }
}
