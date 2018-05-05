package tests.init;

import org.junit.Test;
import tests.DcTest;

/**
 * Created by JustMe on 5/2/2018.
 */
public class JUnitNewLevel extends DcTest{

    @Test
    public void nextLevel(){
        game.getBattleMaster().getOutcomeManager().next();
    }
}
