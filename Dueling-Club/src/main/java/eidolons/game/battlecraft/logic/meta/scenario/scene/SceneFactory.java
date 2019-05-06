package eidolons.game.battlecraft.logic.meta.scenario.scene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefScene;
import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefingData;
import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefingView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.PlainDialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.texture.TextureCache;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.util.Refactor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/22/2017.
 */
@Deprecated
public class SceneFactory implements Supplier<List<Scene>> {
    private String data;
    private boolean dialogue;

    public SceneFactory(String testData) {
        this.data = testData;
    }

    //TODO Speech?
    public static List<Scene> getScenes(GameDialogue dialogue) {
        Speech speech = dialogue.getRoot();
        List<SpeechData> fullData = new ArrayList<>();
        while (true) {
            fullData.add(speech.getData());
            if (speech.getChildren().isEmpty()) break;
            speech = speech.getChildren().get(0);

        }
        return getScenes(fullData);
    }

//    public static List<DialogueView> getScenes(String data) {
//        List<SpeechData> list =
//         ContainerUtils.openContainer(data).stream().map(s ->
//          new SpeechData(s)).collect(Collectors.toList());
//        return getScenes(list);
//    }

    public static List<Scene> getScenes(List<SpeechData> fullData) {
        Speech speech;
//        speech.getFormattedText()
        List<Scene> list = new ArrayList<>();
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

            list.add(new BriefingView(new BriefScene(
                    new BriefingData(
                            data.getValue(SPEECH_VALUE.SPRITE),
                            data.getValue(SPEECH_VALUE.BACKGROUND),
                            data.getValue(SPEECH_VALUE.MESSAGE), false
            ))));
//                    new PlainDialogueView(time, skippable, backTexture, message,portraitTexture)

        }
        return list;
    }

    @Refactor
    @Override
    public List<Scene> get() {
        if (dialogue)
            return getScenes(Eidolons.game.getMetaMaster().getDialogueFactory().getDialogue(data));
        return getScenes(Eidolons.game.getMetaMaster().getIntroFactory().getDialogue(data));
    }

}
