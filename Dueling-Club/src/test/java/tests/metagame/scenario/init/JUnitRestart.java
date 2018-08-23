package tests.metagame.scenario.init;

import main.system.auxiliary.secondary.Bools;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;
import tests.EidolonsTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 5/2/2018.
 */
public class JUnitRestart extends EidolonsTest {

    @Test
    public void restart(){
        game.getBattleMaster().getOutcomeManager().restart();
       Boolean result =
        (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_LOOP_STARTED,10000 );

        assertTrue(Bools.isTrue(result));


    }
}
