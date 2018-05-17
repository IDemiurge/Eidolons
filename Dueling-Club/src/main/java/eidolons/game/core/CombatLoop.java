package eidolons.game.core;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/17/2018.
 */
public class CombatLoop extends GameLoop {

    public CombatLoop(DC_Game game) {
        super(game);
    }

    public void stop() {
       super.stop();
//        signal();
        main.system.auxiliary.log.LogMaster.log(1,"Combat Loop stopped" );
        actionInput(null);
        if (!DC_Engine.isAtbMode())
            return;
        AtbTurnManager manager = (AtbTurnManager) game.getTurnManager();
        manager.getAtbController().getUnitsInAtb().clear();
    }
}
