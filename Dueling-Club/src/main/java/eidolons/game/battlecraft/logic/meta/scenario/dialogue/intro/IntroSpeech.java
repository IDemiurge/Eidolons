package eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechBuilder;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroSpeech extends Speech {
    public IntroSpeech(Integer id) {
        super(id);
    }

    @Override
    public SpeechBuilder getSpeechBuilder(String path) {
        return new IntroSpeechBuilder(path);
    }
}
