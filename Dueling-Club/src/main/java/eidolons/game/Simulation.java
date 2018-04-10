package eidolons.game;

import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.CharacterCreator;

public class Simulation {
    private static DC_Game game;

    public static void init() {
        init(false);

    }

    public static void init(boolean testmode) {
        game = new DC_Game(true);
        game.init();
        if (testmode) {
            return;
        }
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
