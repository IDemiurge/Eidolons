package main.client.dc;

import main.client.cc.CharacterCreator;
import main.client.game.DC_GameInitializer;
import main.game.DC_Game;

public class Simulation {
    private static DC_Game game;

    public static void init() {
        init(false);

    }

    public static void init(boolean testmode) {
        setGame(DC_GameInitializer.initSimulation());
        if (testmode)
            return;
        CharacterCreator.setGame(getGame());
        CharacterCreator.init();
    }

    public static DC_Game getGame() {
        return game;
    }

    public static void setGame(DC_Game game) {
        Simulation.game = game;
    }

}
