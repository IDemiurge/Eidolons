package main.system.ai.logic.goal;

import main.system.ai.UnitAI;

/**
 * helps filter tasks
 *
 * @author JustMe
 */
public class Goal {
    private GOAL_TYPE TYPE;
    private String arg;
    private UnitAI ai;
    private boolean forced;

    public Goal(GOAL_TYPE TYPE, UnitAI ai, boolean forced) {
        this(TYPE, null, ai);
        this.forced = forced;
    }

    public Goal(GOAL_TYPE TYPE, String arg, UnitAI ai) {
        this.TYPE = TYPE;
        this.arg = arg;
        this.ai = ai;
    }

    public GOAL_TYPE getTYPE() {
        return TYPE;
    }

    public String getArg() {
        return arg;
    }

    public UnitAI getAi() {
        return ai;
    }

    public boolean isForced() {
        return forced;
    }

    // AI_TYPE can influence preferred goals
    // getting task arguments based on GOAL TYPE???
    public enum GOAL_TYPE {
        ATTACK, // ALL HOSTILE GOALS
        APPROACH,
        BUFF, // ALL ALLIES
        SELF, // ALL non-std SELFIES
        DEBUFF,
        RESTORE,
        DEBILITATE,

        SUMMONING,
        MOVE, // STD AND CUSTOM MOVE ACTIONS
        WAIT, // on allies or enemies!
        PREPARE, // BUFFS, MODES
        DEFEND, // ALERT OR DEFEND
        RETREAT, // USUALLY FORCED
        SEARCH, // IF NO ENEMIES DETECTED, LOOK AROUND

        ZONE_SPECIAL,
        AUTO_DAMAGE,
        AUTO_BUFF,
        AUTO_DEBUFF,
        OTHER,
        ZONE_DAMAGE,
        CUSTOM_HOSTILE,
        CUSTOM_SUPPORT,
        STEALTH,
        COWER,
        COATING,

        AMBUSH(true), // SPEC MODE - KIND OF ON ALERT...
        WANDER(true), // RANDOM DESTINATION MOVEMENT, BLOCK SPECIAL MOVES
        STALK(true),
        AGGRO(true),
        PATROL(true),
        GUARD(true),
        IDLE(true),;
        private boolean behavior;

        GOAL_TYPE() {

        }

        GOAL_TYPE(boolean b) {
            behavior = b;
        }

        public boolean isFilterByCanActivate() {
            if (isBehavior()) {
                return false;
            }
            return true;
        } // FOLLOW AT SAFE DISTANCE

        public boolean isBehavior() {
            return behavior;
        }

    }
}
