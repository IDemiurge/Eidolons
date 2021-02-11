package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;

public interface DialogueContainerAdapter {
    DialogueHandler getHandler();

    void fadeBg(Float dur, Float alpha);

    void fade(Float dur, Float alpha,  boolean ui_bg_both);

    boolean isOpaque();

    void fadeOut();

    void done();

    void resume();

    void pause();

    void setTime(Float aFloat);

    void disableTimer();
}
