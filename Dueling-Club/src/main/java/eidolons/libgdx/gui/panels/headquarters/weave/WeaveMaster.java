package eidolons.libgdx.gui.panels.headquarters.weave;

import eidolons.game.core.EUtils;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.SCREEN_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveMaster {

    public void init(){
        GuiEventManager.bind(GuiEventType.SHOW_WEAVE, (param)->{
            //blackout
            EUtils.switchScreen(new ScreenData(SCREEN_TYPE.WEAVE, ""));
        });
    }

    public static void openWeave() {
        EUtils.switchScreen(new ScreenData(SCREEN_TYPE.WEAVE, ""));
        //closable ESC
    }
}
