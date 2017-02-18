package main.game.ai.advanced.companion;

import main.entity.obj.unit.Unit;

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
    public static void initCompanionAiParams(Unit hero) {
        // prefs, ai type, ...
    }

    public enum COMPANION_MODE {
        FOLLOW, SCOUT, GUARD, IDLE, ORDERS, AGGRO, STEALTH,
    }

}
