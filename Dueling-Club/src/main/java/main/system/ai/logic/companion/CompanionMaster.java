package main.system.ai.logic.companion;

import main.entity.obj.DC_HeroObj;

public class CompanionMaster {
    /*
     * give orders
     *  * create self-orders
     *
     *  'behavior' vs battle
     *
     *  factors of intelligence, aggression, loyalty...
     *  enemy preferences
     *
     */
    public static void initCompanionAiParams(DC_HeroObj hero) {
        // prefs, ai type, ...
    }

    public enum COMPANION_MODE {
        FOLLOW, SCOUT, GUARD, IDLE, ORDERS, AGGRO, STEALTH,
    }

}
