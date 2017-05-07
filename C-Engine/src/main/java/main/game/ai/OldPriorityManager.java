package main.game.ai;

public abstract class OldPriorityManager extends Old_AI_Manager {

    public OldPriorityManager(AI_Logic ai) {
        super(ai);
    }

    public abstract int getPriorityForUnit();

}
