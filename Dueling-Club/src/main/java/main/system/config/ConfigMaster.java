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

    public int getInt(String key) {
        return 1;
    }

    public Object getValue(String key) {
        return false;
    }

    public boolean getBoolean(String saving_on_default) {
        return false;
    }
}
