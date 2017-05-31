package main.game.battlecraft.logic.meta.scenario.dialogue.intro;

import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechBuilder;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroSpeech extends Speech {
    public IntroSpeech(Integer id) {
        super(id);
    }

    @Override
    protected SpeechBuilder getSpeechBuilder() {
        return new IntroSpeechBuilder();
    }
}
