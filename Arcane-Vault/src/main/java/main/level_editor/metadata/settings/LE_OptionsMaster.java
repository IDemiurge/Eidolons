package main.level_editor.metadata.settings;

import eidolons.system.options.OptionsMaster;

public class LE_OptionsMaster extends OptionsMaster {
    private static LE_Options options;

    public static final void init(){
        options = new LE_Options();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public static LE_Options getOptions() {
        return options;
    }

}
