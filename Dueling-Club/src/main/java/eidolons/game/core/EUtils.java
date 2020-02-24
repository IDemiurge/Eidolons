package eidolons.game.core;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.stage.GuiStage;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.TextWrapper;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.ImmutableTriple;

/**
 * Created by JustMe on 5/21/2018.
 */
public class EUtils {
    public static final String STYLE = "=STYLE=";

    public static void showInfoText(String s) {
        showInfoText(false, s);
    }
    public static void showInfoText(boolean logged, String s) {
        if (logged){
            Eidolons.getGame().getLogManager().log(s);
        }
        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, s);
    }

    public static void showTextTooltip(String description) {
        description = TextWrapper.wrapWithNewLine(description, getDefaultTextWrapLength());
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new ValueTooltip(description));

    }

    private static int getDefaultTextWrapLength() {
        return GdxMaster.getWidth() / 3 / FontMaster.getDefaultStringWidth("1");
    }

    public static void info(String text, boolean wait, boolean onAnotherThread) {
        onConfirm(wait, text, false, null, onAnotherThread);
    }
    public static void info(String text) {
        onConfirm(text, false, null);
    }

    public static boolean waitConfirm(  String text) {
        return onConfirm(true, text, true, null, false);
    }
        public static boolean onConfirm(boolean wait, String text, boolean cancel,
        Runnable o, boolean onAnotherThread) {
        if (wait) {
            onConfirm(text, cancel, () -> {
                if (o != null)
                    o.run();
                WaitMaster.receiveInput(WAIT_OPERATIONS.CONFIRM, true);
            }, onAnotherThread);

            if (!GdxMaster.isLwjglThread())
            if (WaitMaster.getWaiters().get(WAIT_OPERATIONS.CONFIRM) != null) {
                WaitMaster.waitForInput(WAIT_OPERATIONS.CONFIRM);
                return (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.CONFIRM);
            } else {
                return (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.CONFIRM);
            }
        } else {
            onConfirm(text, cancel, o);
        }

        return false;
    }

    public static void onConfirm(String text, boolean cancel, Runnable o, boolean onAnotherThread) {
        if (onAnotherThread)
            onConfirm(text, cancel, () -> Eidolons.onNonGdxThread(o));
        else onConfirm(text, cancel, o);
    }

    public static void onConfirm(String text, Runnable o, Runnable onCancel) {
        GuiEventManager.trigger(GuiEventType.CONFIRM, new ImmutableTriple<>(text, onCancel, o));
    }

    public static void onConfirm(String text, boolean cancel, Runnable o) {
        GuiEventManager.trigger(GuiEventType.CONFIRM, new ImmutableTriple<>(text, cancel, o));
    }

    public static void hideTooltip() {
        GuiEventManager.trigger(GuiEventType.
         SHOW_TOOLTIP, null);

    }

    public static void playSound(STD_SOUNDS sound) {
        //        GuiEventManager.trigger(GuiEventType.VFX_PLAY_LAST);
        DC_SoundMaster.playStandardSound(sound);
    }

    public static void showVFX(GenericEnums.VFX preset, Vector2 v) {
        GuiEventManager.triggerWithParams(GuiEventType.SHOW_VFX, preset, v);
    }

    public static void showVFX(GenericEnums.VFX preset, int x, int y) {
        GuiEventManager.triggerWithParams(GuiEventType.SHOW_VFX, preset, new Vector2(x, y));
    }

    public static void switchScreen(ScreenData screenData) {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, screenData);
    }

    public static void bind(GuiEventType eventType, EventCallback callback) {
        GuiEventManager.bind(eventType, callback);
    }

    public static void event(GuiEventType eventType, Object param) {
        GuiEventManager.trigger(eventType, param);
    }

    public static void switchBackScreen() {
        SCREEN_TYPE type = Eidolons.getPreviousScreenType();
        switchScreen(new ScreenData(type));
    }

    public static void showInfoTextStyled(GuiStage.LABEL_STYLE style, String s) {
        showInfoText(style + STYLE + s);
    }
}
