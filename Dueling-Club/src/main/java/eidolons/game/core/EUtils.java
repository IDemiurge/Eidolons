package eidolons.game.core;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.screens.ScreenData;
import eidolons.system.audio.DC_SoundMaster;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.TextWrapper;
import org.apache.commons.lang3.tuple.ImmutableTriple;

/**
 * Created by JustMe on 5/21/2018.
 */
public class EUtils {
    public static void showInfoText(String s) {
        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, s);
    }

    public static void showTextTooltip(String description) {
        description = TextWrapper.wrapWithNewLine(description, getDefaultTextWrapLength());
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new ValueTooltip(description));

    }

    private static int getDefaultTextWrapLength() {
        return GdxMaster.getWidth() / 3 / FontMaster.getDefaultStringWidth("1");
    }

    public static void onConfirm(String text, boolean cancel, Runnable o) {
        GuiEventManager.trigger(GuiEventType.CONFIRM, new ImmutableTriple<>(text, cancel, o));
    }

    public static void hideTooltip() {
        GuiEventManager.trigger(GuiEventType.
         SHOW_TOOLTIP, null);

    }

    public static void playSound(STD_SOUNDS sound) {
//        GuiEventManager.trigger(GuiEventType.SFX_PLAY_LAST);
        DC_SoundMaster.playStandardSound(sound);
    }

    public static void showVFX(EMITTER_PRESET preset, Vector2 v) {
        GuiEventManager.trigger(GuiEventType.SHOW_VFX, preset, v);
    }

    public static void showVFX(EMITTER_PRESET preset, int x, int y) {
        GuiEventManager.trigger(GuiEventType.SHOW_VFX, preset, new Vector2(x, y));
    }

    public static void switchScreen(ScreenData screenData) {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, screenData);
    }
}
