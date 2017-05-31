package main.game.battlecraft.logic.meta.scenario.dialogue.intro;

import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import main.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroFactory extends DialogueFactory {

    @Override
    protected Speech getSpeech(Integer integer) {
        return new IntroSpeech(integer);
    }
    protected String getFilePath() {
        return  DialogueLineFormatter.getIntrosFilePath();
    }
}
