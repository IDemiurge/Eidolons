package tests;

import main.game.battlecraft.ai.AI_Manager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import org.junit.Before;

/**
 * Created by JustMe on 5/23/2017.
 */
public class GenericJUnitTest {
    @Before
    public void init() {
        LogMaster.setOff(true); //log everything* or nothing to speed up
        CoreEngine.setGraphicsOff(true);
        AI_Manager.setOff(true);
    }
}
