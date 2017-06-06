package main.game.battlecraft.logic.meta.scenario.dialogue.intro;

import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechBuilder;

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
