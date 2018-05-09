package tests.metagame.scenario.init;

import org.junit.Test;
import tests.metagame.ScenarioTest;

/**
 * Created by JustMe on 5/2/2018.
 */
public class JUnitNewLevel extends ScenarioTest {
int scenarioIndex=0;
int heroIndex = 0;
    @Test
    public void nextLevel(){
        game.getBattleMaster().getOutcomeManager().next();
    }
}
