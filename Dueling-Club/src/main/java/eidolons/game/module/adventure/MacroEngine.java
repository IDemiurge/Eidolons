package eidolons.game.module.adventure;

import eidolons.game.module.adventure.entity.action.MacroActionManager;

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
