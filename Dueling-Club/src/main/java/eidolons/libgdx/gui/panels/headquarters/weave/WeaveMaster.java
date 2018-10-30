package eidolons.libgdx.gui.panels.headquarters.weave;

import eidolons.game.core.EUtils;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveSpace.WEAVE_VIEW_MODE;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveMaster {

    public static void openWeave() {
        try {
            EUtils.switchScreen(new ScreenData(SCREEN_TYPE.WEAVE, ""));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        //closable ESC
    }

    public static void toggleSkillsClasses() {
        WeaveScreen.getInstance().getSpace().toggle();
        WeaveScreen.getInstance().getSpace().refresh();
//        EUtils.event(GuiEventType.REFRESH_WEAVE);
    }

    public void init() {
        GuiEventManager.bind(GuiEventType.SHOW_WEAVE, (param) -> {
            //blackout
            openWeave();
        });
    }

    public static void viewModeChanged(WEAVE_VIEW_MODE mode) {
        WeaveScreen.getInstance().getSpace().setViewMode(mode);
        WeaveScreen.getInstance().getSpace().refresh();
    }

    public static boolean isOn() {
        return false;
    }
}
