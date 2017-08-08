package main.game.battlecraft.ai.advanced.companion;

import main.content.enums.system.AiEnums.META_GOAL_TYPE;

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


}
