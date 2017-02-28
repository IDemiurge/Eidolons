package main.test.debug;

import main.game.core.Eidolons;
import main.libgdx.anims.controls.Controller;

/**
 * Created by JustMe on 2/16/2017.
 */
public class DebugController implements Controller{

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

    DebugMaster    getDebugMaster(){
        return Eidolons.game.getDebugMaster();
    }
    @Override
    public boolean charTyped(char c) {
        switch (c) {
            case 'd':
                getDebugMaster().showDebugWindow();
                return true;
        }
//        FUNC_STANDARD(DebugMaster.group_basic),
//         FUNC_ADD_BF(DebugMaster.group_add_bf_obj),
//         FUNC_ADD_NON_BF(DebugMaster.group_add),
//         FUNC_GLOBAL(DebugMaster.group_bf),
//         FUNC_GRAPHICS(DebugMaster.group_graphics),
//         FUNC_SFX(DebugMaster.group_sfx),
        return false;
    }
}
