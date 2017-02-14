package main.game.ai.logic;

import main.game.DC_Game;
import main.game.ai.AI;
import main.game.ai.AI_Logic;
import main.game.ai.logic.types.brute.BruteAI;
import main.game.player.Player;

public class DC_AI extends AI {

    public DC_AI(DC_Game game, Player player) {
        super(game, player);

    }

    public void init() {
        super.init();
        AI_Logic logic = new BruteAI(this);
        logic.init();
        controller.setLogic(logic);
    }

}
