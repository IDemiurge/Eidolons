package main.game.battlecraft.ai.advanced.companion;

/**
 * Created by JustMe on 7/30/2017.
 * Can influence Priorities
 */
public class MetaGoal {
    META_GOAL_TYPE type;
    Object arg;

    public MetaGoal(META_GOAL_TYPE type, Object arg) {
        this.type = type;
        this.arg = arg;
    }

    public META_GOAL_TYPE getType() {
        return type;
    }

    public Object getArg() {
        return arg;
    }

    public enum IMPULSE_TYPE {
        IMPULSES, VENGEANCE, HATRED, FEAR, GREED, CURIOSITY, PROTECTIVENESS
    }
    public enum INCLINATION_TYPE {
        DEFENSE,
        ASSASSINATION,
        BRAWL,
        SUPPORT,
        CAUTION,
    }
        public enum CHARACTER_TYPE {
        PROTECTOR,
        ASSASSIN,
        LEADER,
        ARCHER,
        SUPPORT,
    }

    public enum META_GOAL_TYPE {
        PROTECT,
        AVENGE,
        AVOID,
        AID,
        BRAWL,
        ASSASSINATE,
    }
}
