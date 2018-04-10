package tests;

import eidolons.game.core.atb.AtbController;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 4/7/2018.
 */
public class AtbHelper {

    DC_Game game;
    AtbController controller;
    public AtbHelper(DC_Game game) {
        this.game = game;
        if (game.getTurnManager() instanceof AtbTurnManager) {
              controller = ((AtbTurnManager) game.getTurnManager())
             .getAtbController();

        }
    }

    public void endCombat() {
        controller.getUnitsInAtb();
    }

    public void logInfo() {

    }
}
