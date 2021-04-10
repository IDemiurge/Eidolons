package eidolons.game.core;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
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
        actionInputManual(null);
        if (!DC_Engine.isAtbMode())
            return;
        AtbTurnManager manager = (AtbTurnManager) game.getTurnManager();
        manager.getAtbController().getUnitsInAtb().clear();
    }

    @Override
    public void start() {
        super.start();
//        getGame().getAiManager().getActionManager().initIntents();
    }

    public void endCombat() {
        setActiveUnit(null );
        getGame().getTurnManager().resetQueue();
        getGame().getTurnManager().setActiveUnit(null );
        for (Unit unit : AggroMaster.getLastAggroGroup()) {
            unit.getAI().combatEnded();
        }
        getGame().getDungeonMaster().getExplorationMaster().switchExplorationMode(true);
    }

    public int getWaitOnStartTime() {
        return 2000;
    }
}
