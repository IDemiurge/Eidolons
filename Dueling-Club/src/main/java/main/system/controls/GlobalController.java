package main.system.controls;

import main.game.core.Eidolons;

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
            case 'D':
                Eidolons.game.getDebugMaster() .showDebugWindow();
                return true;
        }

        return false;
    }
}
