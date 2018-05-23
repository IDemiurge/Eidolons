package eidolons.game.core;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;
import main.system.text.TextWrapper;

/**
 * Created by JustMe on 5/21/2018.
 */
public class EUtils {
    public static void showInfoText(String s) {
        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, s);
    }

    public static void showTextTooltip(String description) {
        description= TextWrapper.wrapWithNewLine(description, getDefaultTextWrapLength());
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new ValueTooltip(description));

    }

    private static int getDefaultTextWrapLength() {
        return GdxMaster.getWidth()/3/ FontMaster.getDefaultStringWidth("1");
    }

    public static void onConfirm(String text, boolean cancel, Runnable o) {
    }
}
