package tests.metagame;

import main.libgdx.launch.ScenarioLauncher;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;
import tests.GenericJUnitTest;

/**
 * Created by JustMe on 5/16/2017.
 */
public class ScenarioTest extends GenericJUnitTest {

    public static String typeName = "Pride and Treachery";

    @Test
    public void test() {
//        CoreEngine.setItemGenerationOff(true);
        ScenarioLauncher.launch(typeName);

        WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
    }
}
