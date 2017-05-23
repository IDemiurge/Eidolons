package tests.metagame;

import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.frontend.ScenarioLauncher;
import org.junit.Test;

/**
 * Created by JustMe on 5/16/2017.
 */
public class ScenarioTest  {

    public static String typeName="Pride and Treachery";

    @Test
    public void test(){
//        CoreEngine.setItemGenerationOff(true);
        ScenarioLauncher.lauch(typeName);

        WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
    }
}
