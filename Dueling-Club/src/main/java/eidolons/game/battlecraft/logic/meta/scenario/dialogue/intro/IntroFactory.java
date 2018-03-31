package eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroFactory extends DialogueFactory {

    private static final String FILE_NAME = "intros.txt";

    @Override
    protected Speech getSpeech(Integer integer) {
        return new IntroSpeech(integer);
    }

    protected String getFileName() {
        return FILE_NAME;
    }
}
