package tests;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.core.atb.AtbController;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.ExploreGameLoop;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static org.junit.Assert.assertTrue;

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

    public void startCombatNaturally(boolean all_or_closest_group) {

    }

    public void startCombat() {
        startCombat(true);
    }

    public void startCombat(boolean all_or_closest_group) {

        for (GroupAI sub : game.getAiManager().getGroups()) {
            if (all_or_closest_group)
                for (Unit ignored : sub.getMembers()) {
                    ignored.getAI().setEngaged(true);
                }
            else {
                //TODO
            }
        }
//        AggroMaster.unitAttacked();
        game.getDungeonMaster().getExplorationMaster()
         .getAggroMaster().checkStatusUpdate();

        assertTrue(!ExplorationMaster.isExplorationOn());
        assertTrue(!(game.getLoop() instanceof ExploreGameLoop));
    }
    public void logInfo() {

    }

    public void waitForGameLoopStart() {
        assertTrue(
         (Boolean)
          WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_LOOP_STARTED, 1500));

    }
}
