package main.game.battlecraft.ai.logic.types.brute;

import main.game.ai.AI_Logic;
import main.game.ai.OldPriorityManager;

import java.util.Random;

public class BrutePrioritizer extends OldPriorityManager {

    public BrutePrioritizer(AI_Logic ai) {
        super(ai);
    }

    @Override
    public int getPriorityForUnit() {
        return new Random().nextInt();
    }
}
