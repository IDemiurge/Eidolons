package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.libgdx.texture.Sprites;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

/**
 * Created by JustMe on 11/16/2018.
 */
public class SpeechDataSource {

    public static final String DEFAULT_RESPONSE = "ok";

    String message;
    List<String> responses;
    Speech speech;
    ActorDataSource left;
    ActorDataSource right;
    private boolean leftActive = true;
    private String background= Sprites.BG_DEFAULT;
    //custom font/style?


    public SpeechDataSource(Speech speech, ActorDataSource left, ActorDataSource right) {
        this.speech = speech;
        this.left = left;
        this.right = right;
        this.message = speech.getFormattedText();
    }

    public SpeechDataSource(Speech speech) {
        this(speech, new ActorDataSource(speech.getActor()), null);

    }

    public List<String> getResponses() {
        if (!ListMaster.isNotEmpty(responses)) {
            return ListMaster.toStringList(DEFAULT_RESPONSE);
        }
        return responses;
    }

    public ActorDataSource getLeft() {
        return left;
    }

    public ActorDataSource getRight() {
        return right;
    }

    public boolean isLeftActive() {
        return leftActive;
    }

    public void setLeftActive(boolean leftActive) {
        this.leftActive = leftActive;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public enum SPEECH_EFFECT {
        ZOOM_IN,
        DARKEN,
        FLIP,
        FADE,
    }

    public String getMessage() {
        return message;
    }

    public Speech getSpeech() {
        return speech;
    }
}
