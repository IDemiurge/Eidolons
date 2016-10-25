package main.game.logic.macro;

import main.game.logic.macro.entity.MacroActionManager;

public class MacroEngine {

    private static boolean initialized;

    public static void init() {
        if (initialized)
            return;
        MacroActionManager.generateMacroActions();
        initialized = true;
    }
}
