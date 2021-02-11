package eidolons.game.battlecraft;

import eidolons.game.core.launch.CustomLaunch;

public class EngineLauncher {
    private static EngineLauncher instance;
    private CustomLaunch customLaunch;

    public EngineLauncher() {
        instance = this;
    }

    public   CustomLaunch getCustomLaunch() {
        return customLaunch;
    }

    public   void setCustomLaunch(CustomLaunch customLaunch) {
        main.system.auxiliary.log.LogMaster.important("customLaunch set: " + customLaunch);
        this.customLaunch = customLaunch;
    }

    public static EngineLauncher getInstance() {
        return instance;
    }
}
