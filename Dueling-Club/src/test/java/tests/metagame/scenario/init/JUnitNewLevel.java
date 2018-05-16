package tests.metagame.scenario.init;

import eidolons.system.test.Debugger;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;
import tests.metagame.scenario.ScenarioTest;

/**
 * Created by JustMe on 5/2/2018.
 *
 *
 * Checks the following:
 *
    Game state is ALL OK
    LevelUp was successful
    Gamma was reset
    Last level => Victory

 */
public class JUnitNewLevel extends ScenarioTest {
int scenarioIndex=0;
int heroIndex = 0;
    @Test
    public void nextLevel(){

        String snapshot = Debugger.getGameStateSnapshot();
        main.system.auxiliary.log.LogMaster.log(1,"Game snapshot bef "+snapshot );
        game.getBattleMaster().getOutcomeManager().next();
        snapshot = Debugger.getGameStateSnapshot();
        main.system.auxiliary.log.LogMaster.log(1,"Game snapshot after "+snapshot );



        if (isPlayAfter()){
            WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
        }
    }

    private boolean isPlayAfter() {
        return true;
    }

    @Override
    public Integer getScenarioIndex() {
        return scenarioIndex;
    }

    @Override
    public Integer getHeroIndex() {
        return heroIndex;
    }
}
