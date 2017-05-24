package main.game.battlecraft.logic.meta.scenario.scene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.LinearDialogue;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.libgdx.DialogScenario;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/22/2017.
 */
public class SceneFactory {
    DialogueActorMaster actorMaster;

    //TODO Speech?
    public static List<DialogScenario> getScenes(LinearDialogue dialogue) {
        Speech speech = dialogue.getSpeech();
        List<SpeechData> fullData = new LinkedList<>();
        while (true) {
            fullData.add(speech.getData());
            if (speech.getChildren().isEmpty()) break;
            speech = speech.getChildren().get(0);

        }

        return getScenes(fullData);
    }

    public static List<DialogScenario> getScenes(String data) {
        List<SpeechData> list =
         StringMaster.openContainer(data).stream().map(s ->
          new SpeechData(s)).collect(Collectors.toList());
        return getScenes(list);
    }

    public static List<DialogScenario> getScenes(List<SpeechData> fullData) {
        Speech speech;
//        speech.getFormattedText()
        List<DialogScenario> list = new LinkedList<>();
//        for (String substring : StringMaster.openContainer(data)) {
        for (SpeechData data : fullData) {
            TextureRegion portraitTexture = null;
            String message = null;
            TextureRegion backTexture = null;
            boolean skippable = true;
            Integer time = -1;

            portraitTexture =
             TextureCache.getOrCreateR(DialogueActorMaster.
              getActor(data.getValue(SPEECH_VALUE.ACTOR))
//              .getLinkedUnit()
              .getImagePath());
            message = data.getValue(SPEECH_VALUE.MESSAGE);
            if (!StringMaster.isEmpty(data.getValue(SPEECH_VALUE.BACKGROUND)))
            backTexture = TextureCache.getOrCreateR(data.getValue(SPEECH_VALUE.BACKGROUND));

            list.add(new DialogScenario(time, skippable, backTexture, message,
             portraitTexture));

        }
        return list;
    }
}
