package eidolons.game.battlecraft;

import eidolons.game.core.launch.CustomLaunch;

public class EngineLauncher {
    private static EngineLauncher instance;
    private CustomLaunch customLaunch;

    private EngineLauncher() {
        
    }

    public   CustomLaunch getCustomLaunch() {
        return customLaunch;
    }

    public   void setCustomLaunch(CustomLaunch customLaunch) {
        main.system.auxiliary.log.LogMaster.important("customLaunch set: " + customLaunch);
        this.customLaunch = customLaunch;
    }

    public static EngineLauncher getInstance() {
        if (instance == null) {
            instance = new EngineLauncher();
        }
        return instance;
    }
}
