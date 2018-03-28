package main.game.battlecraft.logic.meta.scenario.scene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.game.core.Eidolons;
import main.libgdx.DialogScenario;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.util.Refactor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/22/2017.
 */
public class SceneFactory implements Supplier<List<DialogScenario>> {
    private String data;
    private boolean dialogue;

    public SceneFactory(String testData) {
        this.data = testData;
    }

    //TODO Speech?
    public static List<DialogScenario> getScenes(GameDialogue dialogue) {
        Speech speech = dialogue.getRoot();
        List<SpeechData> fullData = new ArrayList<>();
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
        List<DialogScenario> list = new ArrayList<>();
//        for (String substring : StringMaster.open(data)) {
        TextureRegion backTexture = null;
        for (SpeechData data : fullData) {
            TextureRegion portraitTexture = null;
            String message = null;
            boolean skippable = true;
            Integer time = -1;

            message = data.getValue(SPEECH_VALUE.MESSAGE);

            if (!StringMaster.isEmpty(data.getValue(SPEECH_VALUE.ACTOR)))
                portraitTexture =
                 TextureCache.getOrCreateR(DialogueActorMaster.
                  getActor(data.getValue(SPEECH_VALUE.ACTOR))
                  .getImagePath());

            if (!StringMaster.isEmpty(data.getValue(SPEECH_VALUE.BACKGROUND)))
                backTexture = TextureCache.getOrCreateR(data.getValue(SPEECH_VALUE.BACKGROUND));

            list.add(new DialogScenario(time, skippable, backTexture, message,
             portraitTexture));

        }
        return list;
    }

    @Refactor
    @Override
    public List<DialogScenario> get() {
        if (dialogue)
            return getScenes(Eidolons.game.getMetaMaster().getDialogueFactory().getDialogue(data));
        return getScenes(Eidolons.game.getMetaMaster().getIntroFactory().getDialogue(data));
    }

}
