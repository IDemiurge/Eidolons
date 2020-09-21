package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.system.audio.DC_SoundMaster;
import main.system.sound.AudioEnums;

public interface SmartButton extends EventListener {
    default boolean handle(Event e) {
        if (!isIgnoreConfirmBlock())
            if (ConfirmationPanel.getInstance().isVisible())
                return true;


        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;
        AudioEnums.STD_SOUNDS sound = null;
        if (event.getType() == InputEvent.Type.touchUp) {
            if (!isCheckClickArea() || event.getPointer() == -1 //programmatic
                    || GdxMaster.isWithin(event.getTarget(), new Vector2(event.getStageX(), event.getStageY()), true)) {
                if (!isDisabled()) {
                    if (getSoundMap() != null)
                        sound = getSoundMap().up;

                    if (getRunnable() != null)
                        getRunnable().run();
                } else {
                    if (getDisabledRunnable() != null)
                        getDisabledRunnable().run();
                }
            }
        } else {
            if (getSoundMap() != null)
                switch (event.getType()) {
                    case touchDown:
                        if (isDisabled()) {
                            sound = getSoundMap().disabled;
                        } else {
                            sound = getSoundMap().down;
                        }
                        break;
                    case enter:
                        sound = getSoundMap().hover;
                        break;
                }
        }
        if (sound != null) {

            DC_SoundMaster.playStandardSound(sound);
        }
        return true;

    }

    boolean isNoClickCheck();

    default boolean isCheckClickArea() {
        if (isNoClickCheck()) {
            return false;
        }
        return !isFlipX() && !isFlipY();
    }

    Runnable getRunnable();

    AudioEnums.BUTTON_SOUND_MAP getSoundMap();

    default AudioEnums.STD_SOUNDS getSound() {
        return null;
    }

    void setRunnable(Runnable runnable);

    boolean isIgnoreConfirmBlock();

    boolean isDisabled();

    Runnable getDisabledRunnable();

    void setIgnoreConfirmBlock(boolean ignoreConfirmBlock);

    default Actor getActor() {
        return (Actor) this;
    }

    void setDisabledRunnable(Runnable disabledRunnable);

    void setChecked(boolean b);

    boolean isFlipX();

    boolean isFlipY();

    void setNoClickCheck(boolean b);

    void setFlipY(boolean b);

    boolean isChecked();
}
