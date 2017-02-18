package main.client.game;

import main.game.core.game.DC_Game;

public class DC_GameInitializer {

    public static DC_Game initSimulation() {
        DC_Game game = new DC_Game();
        game.setSimulation(true);
        game.init();
        return game;
    }

}
