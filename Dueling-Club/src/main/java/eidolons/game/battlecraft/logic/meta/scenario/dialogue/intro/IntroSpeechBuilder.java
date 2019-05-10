package eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechBuilder;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroSpeechBuilder extends SpeechBuilder {


    public IntroSpeechBuilder(String linesPath) {
        super(linesPath);
    }

    @Override
    protected String processText(String text, Speech speech) {
        return text;
    }
}