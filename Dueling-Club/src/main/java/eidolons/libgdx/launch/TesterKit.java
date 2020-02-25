package eidolons.libgdx.launch;

import eidolons.game.battlecraft.rules.RuleKeeper;
import main.system.auxiliary.log.LOG_CHANNEL;

public class TesterKit {

    public static void init(){
        for (LOG_CHANNEL value : LOG_CHANNEL.values()) {
            value.setOn(false);
        }
        //then selective

//        LogManager.priority = 2;

        RuleKeeper.RULE_SCOPE full;



    }
}
