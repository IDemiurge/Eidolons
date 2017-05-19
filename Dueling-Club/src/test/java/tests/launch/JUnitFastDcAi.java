package tests.launch;

import main.content.PARAMS;
import main.game.core.Eidolons;
import main.game.core.launch.PresetLauncher.LAUNCH;
import main.system.DC_Formulas;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.frontend.FAST_DC;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/16/2017.
 */
public class JUnitFastDcAi {

    @Test
    public void testFastDcAi() {
        for (int i = 100; i > 0; i--) {
            CoreEngine.setGraphicsOff(true);
            FAST_DC.main(new String[]{
             String.valueOf(Arrays.asList(LAUNCH.values()).indexOf(LAUNCH.AI))
            });
            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTIVE_UNIT_SELECTED);
            Eidolons.game.getUnits().forEach(unit -> {
                Integer endurance = unit.getIntParam(PARAMS.ENDURANCE);
                Integer presumed =
                 DC_Formulas.getEnduranceFromVitality(unit.getIntParam(PARAMS.VITALITY))
                  + unit.getType().getIntParam(PARAMS.ENDURANCE);
                main.system.auxiliary.log.LogMaster.log(1, endurance + " vs " + presumed);
                assertTrue(endurance
                 <= presumed);
            });
            main.system.auxiliary.log.LogMaster.log(1,"\n\n\nJUnit done\n\n\n" );
// speed up!
            CoreEngine.setSelectivelyReadTypes("");
        }
    }
}
