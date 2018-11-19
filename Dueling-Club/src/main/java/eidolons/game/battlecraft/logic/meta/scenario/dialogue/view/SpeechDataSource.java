package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;

/**
 * Created by JustMe on 11/16/2018.
 */
public class SpeechDataSource {

    String actorName;
    String actorImage;
    String imageSuffix;
    String message;
    Speech speech;

    //custom font/style?


    public SpeechDataSource(Speech speech) {
        this.speech = speech;
    }

    public enum SPEECH_EFFECT{
        ZOOM_IN,
        DARKEN,
        FLIP,
        FADE,
    }

    public String getActorName() {
        return actorName;
    }

    public String getActorImage() {
        return actorImage;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public String getMessage() {
        return message;
    }

    public Speech getSpeech() {
        return speech;
    }
}
