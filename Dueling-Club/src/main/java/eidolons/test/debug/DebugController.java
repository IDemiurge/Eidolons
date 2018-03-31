package eidolons.test.debug;

import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.controls.Controller;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.test.debug.DebugMaster.DEBUG_FUNCTIONS;

/**
 * Created by JustMe on 2/16/2017.
 */
public class DebugController implements Controller {

    private static Controller instance;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new DebugController();
        }
        return instance;
    }

    public static void setInstance(Controller instance) {
        DebugController.instance = instance;
    }

    DebugMaster getDebugMaster() {
        return Eidolons.game.getDebugMaster();
    }

    @Override
    public boolean charTyped(char c) {
        switch (c) {
            case 'R':
                ExplorationMaster.setRealTimePaused(!ExplorationMaster.isRealTimePaused());
                return true;
            case 'D':
                getDebugMaster().showDebugWindow();
                return true;
            case ' ':
                if (DungeonScreen.getInstance() == null)
                    return false;
                if (!DungeonScreen.getInstance().isWaitingForInput())
                    getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.PAUSE);
                return true;
            case 'A':
                getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.TOGGLE_ALT_AI);
                return true;
        }
//        TOGGLE_DUMMY(true),
//         TOGGLE_DUMMY_PLUS(true),
//         TOGGLE_OMNIVISION(true),
//         TOGGLE_DEBUG,
//        FUNC_STANDARD(DebugMaster.group_basic),
//         FUNC_ADD_BF(DebugMaster.group_add_bf_obj),
//         FUNC_ADD_NON_BF(DebugMaster.group_add),
//         FUNC_GLOBAL(DebugMaster.group_bf),
//         FUNC_GRAPHICS(DebugMaster.group_graphics),
//         FUNC_SFX(DebugMaster.group_sfx),
        return false;
    }
}
