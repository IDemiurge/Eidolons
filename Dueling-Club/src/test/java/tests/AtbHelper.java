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
        if (!ExplorationMaster.isExplorationOn())
            return;
        startCombat(true);
        waitForGameLoopStart();
    }

    public void startCombat(boolean all_or_closest_group) {

        if (game.getPlayer(false).getControlledUnits().isEmpty()) {
            game.getDungeonMaster().getExplorationMaster().switchExplorationMode(false);
            return;
        }


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
        if (!ExplorationMaster.isExplorationOn()) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTIVE_UNIT_SELECTED, 1000);
        }
    }

    public void pause() {
        game.getLoop().setPaused(true);
    }
    public void resume() {
        game.getLoop().setPaused(false);
    }

    public void passTime(float v) {
        controller.passTime(v);
    }
}
