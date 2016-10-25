package main.game.ai.logic;

import main.game.ai.AI_Logic;

public abstract class TargetingManager extends Old_AI_Manager {
    public TargetingManager(AI_Logic logic) {
        super(logic);
    }

    public abstract int initTarget();

}
