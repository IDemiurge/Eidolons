package eidolons.game.battlecraft;

import eidolons.game.core.launch.TestLaunch;

public class EngineLauncher {
    private static EngineLauncher instance;
    private TestLaunch testLaunch;

    private EngineLauncher() {
        
    }

    public TestLaunch getCustomLaunch() {
        return testLaunch;
    }

    public   void setTestLaunch(TestLaunch testLaunch) {
        main.system.auxiliary.log.LogMaster.important("customLaunch set: " + testLaunch);
        this.testLaunch = testLaunch;
    }

    public static EngineLauncher getInstance() {
        if (instance == null) {
            instance = new EngineLauncher();
        }
        return instance;
    }
}
