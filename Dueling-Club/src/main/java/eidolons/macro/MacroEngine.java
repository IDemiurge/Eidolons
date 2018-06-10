package eidolons.macro;

import eidolons.macro.entity.action.MacroActionManager;

public class MacroEngine {

    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }
        MacroActionManager.generateMacroActions();
        initialized = true;
    }
}
