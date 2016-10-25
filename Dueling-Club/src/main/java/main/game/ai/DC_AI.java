package main.game.ai;

import main.game.DC_Game;
import main.game.ai.brute.BruteAI;
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
