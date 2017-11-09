package main.system.controls;

import main.game.core.Eidolons;
import main.libgdx.screens.DungeonScreen;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

/**
 * Created by JustMe on 3/2/2017.
 */
public class GlobalController implements Controller {
    /*
    toggle dummy?

     */
    @Override
    public boolean charTyped(char c) {

        switch (c) {
            case ' ':
                if (DungeonScreen.getInstance() == null)
                    return false;
                if (!DungeonScreen.getInstance().isWaitingForInput())
                    Eidolons.game.getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.PAUSE);
                return true;
            case 'D':
                Eidolons.game.getDebugMaster().showDebugWindow();
                return true;
        }

        return false;
    }
}
