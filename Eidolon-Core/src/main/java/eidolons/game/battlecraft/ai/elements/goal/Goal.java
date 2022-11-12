package eidolons.game.battlecraft.ai.elements.goal;

import eidolons.game.battlecraft.ai.UnitAI;
import main.content.enums.system.AiEnums.GOAL_TYPE;

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

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}
