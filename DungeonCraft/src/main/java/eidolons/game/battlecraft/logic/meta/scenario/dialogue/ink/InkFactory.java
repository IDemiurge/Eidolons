package eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink;

import com.bladecoder.ink.runtime.Story;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.InkEnums.INK_DIALOGUE_TEMPLATE;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.SpeechDataSource;
import main.data.dialogue.SpeechData;

/**
 * Created by JustMe on 11/20/2018.
 */
public class InkFactory {

    SpeechDataSource toSpeechMap(InkDialogue dialogue){
        while (dialogue.canContinue()) {
            String text = dialogue.Continue();
            new SpeechData("");
            new Speech("", "");
        }
        return null;
    }


        SpeechDataSource toDataSource(InkDialogue dialogue){
            Speech speech=null ;
            return new SpeechDataSource(speech);
    }
    public InkDialogue createGeneratedDialogue(INK_DIALOGUE_TEMPLATE template) {
        //pregenerate perhaps?
        return null;
    }
        public InkDialogue createPresetDialogue(String path) {
        try {
            String jsonData = InkIoMaster.readJson(path);
            Story story = new Story(jsonData);
            return new InkDialogue( );
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return null;
    }
}
