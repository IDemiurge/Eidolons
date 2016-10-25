package main.game.ai.logic;

import main.game.ai.AI_Logic;

public abstract class ActionTypeManager extends Old_AI_Manager {
    public ActionTypeManager(AI_Logic logic) {
        super(logic);
    }

    public abstract ACTION_TYPES getAction();

    public enum ACTION_TYPES {
        APPROACH,
        CLOSE_IN,
        CLAIM,
        ESCAPE,
        ATTACK,
        ABILITY,
        SPELL,
        DECLAIM
    }

}
