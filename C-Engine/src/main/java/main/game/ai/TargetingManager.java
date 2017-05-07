package main.game.ai;

public abstract class TargetingManager extends Old_AI_Manager {
    public TargetingManager(AI_Logic logic) {
        super(logic);
    }

    public abstract int initTarget();

}
