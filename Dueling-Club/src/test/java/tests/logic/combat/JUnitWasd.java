package tests.logic.combat;

import eidolons.system.hotkey.DC_KeyManager;
import main.game.bf.directions.FACING_DIRECTION;
import org.junit.Test;

import static eidolons.system.hotkey.DC_KeyManager.getAbsoluteDirectionForWasd;

/**
 * Created by JustMe on 7/15/2018.
 */
public class JUnitWasd {

    @Test
    public void test() {
        for (FACING_DIRECTION unitDirection : FACING_DIRECTION.normalFacing ) {
            for (FACING_DIRECTION moveDirection : FACING_DIRECTION.normalFacing) {
                char c = DC_KeyManager. getCorrectedWsad(unitDirection, moveDirection);
                main.system.auxiliary.log.LogMaster.log(1, "Unit with "+unitDirection
                 +" press for " + moveDirection
                 +" moves to "+ getAbsoluteDirectionForWasd(c));
            }

        }
    }
}
