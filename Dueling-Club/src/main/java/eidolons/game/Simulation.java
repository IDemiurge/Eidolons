package eidolons.game;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.SimulationGame;
import eidolons.game.module.herocreator.CharacterCreator;

public class Simulation {
    private static SimulationGame game;

    public static void init() {
        init(false);

    }

    public static void init(boolean testmode) {
        init(testmode, null);
    }
        public static void init(boolean testmode, MetaGameMaster metaMaster) {
        if (game!=null )
            return;
        game = new SimulationGame();
        game.setMetaMaster(metaMaster);
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

    public static void setRealGame(DC_Game real) {
        game.setRealGame(real);
    }
}
