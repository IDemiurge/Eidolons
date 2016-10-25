package main.game.ai.logic;

import main.game.ai.AI_Logic;

public abstract class OldPriorityManager extends Old_AI_Manager {

    public OldPriorityManager(AI_Logic ai) {
        super(ai);
    }

    public abstract int getPriorityForUnit();

}
