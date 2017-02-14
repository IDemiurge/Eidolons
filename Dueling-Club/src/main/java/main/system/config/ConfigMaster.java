package main.system.config;

/**
 * Created by JustMe on 2/11/2017.
 */

//should be usable as game settings too, not just testing!
public class ConfigMaster {
    private static ConfigMaster instance = new ConfigMaster();

    private ConfigMaster() {
    }

    public static ConfigMaster getInstance() {
        return instance;
    }

    public void readConfig() {

    }
}
