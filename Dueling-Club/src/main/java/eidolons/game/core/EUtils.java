package eidolons.game.core;

import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/21/2018.
 */
public class EUtils {
    public static void showInfoText(String s) {
        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, s);
    }
}
