package main.game.battlecraft.logic.meta.scenario.dialogue.intro;

import main.data.filesys.PathFinder;
import main.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechBuilder;

/**
 * Created by JustMe on 5/31/2017.
 */
public class IntroSpeechBuilder extends SpeechBuilder {

    public IntroSpeechBuilder() {
        super(PathFinder.getEnginePath() + DialogueLineFormatter.getLinesFilePathIntro());
    }

    @Override
    protected String processText(String text, Speech speech) {
        return text;
    }
}
