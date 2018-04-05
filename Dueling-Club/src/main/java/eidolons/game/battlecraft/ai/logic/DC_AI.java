package eidolons.game.battlecraft.ai.logic;

import eidolons.game.battlecraft.ai.logic.types.brute.BruteAI;
import eidolons.game.core.game.DC_Game;
import main.game.ai.AI;
import main.game.ai.AI_Logic;
import main.game.logic.battle.player.Player;

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
