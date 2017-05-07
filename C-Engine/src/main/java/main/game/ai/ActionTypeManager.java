package main.game.ai;

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
