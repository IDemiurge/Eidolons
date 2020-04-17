package eidolons.game.battlecraft.logic.meta.scenario.scene;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.ActorDataSource;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.SpeechDataSource;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.igg.story.brief.BriefScene;
import eidolons.game.netherflame.igg.story.brief.BriefingData;
import eidolons.game.netherflame.igg.story.brief.BriefingView;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.system.auxiliary.StringMaster;
import main.system.util.Refactor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    public static List<Scene> getScenesLinear(GameDialogue dialogue) {
        Speech speech = dialogue.getRoot();
        List<Speech > list = new ArrayList<>();
        while (true) {
            list.add(speech );
            if (speech.getChildren().isEmpty()) break;
            speech = speech.getChildren().get(0);

        }
        return getScenesLinear(list);
    }


    public static List<Scene> getScenesLinear(List<Speech> fullData) {
        List<Scene> list = new ArrayList<>();
        for (Speech speech : fullData) {
            SpeechData data = speech.getData();
            if (isBriefingTest()) {
                list.add(new BriefingView(new BriefScene(
                        new BriefingData(
                                data.getValue(SPEECH_VALUE.SPRITE),
                                data.getValue(SPEECH_VALUE.BACKGROUND),
                                data.getValue(SPEECH_VALUE.MESSAGE), false
                        ))));
            } else {
                DialogueActor actor1 = DialogueActorMaster.getActor(data.getActorLeft());
                DialogueActor actor2 =StringMaster.isEmpty(  data.getActorRight()) ? null : DialogueActorMaster.getActor(data.getActorRight());

                list.add(new DialogueView(new SpeechDataSource(speech, new ActorDataSource(actor1),
                        new ActorDataSource(actor2))));
            }
//                    new PlainDialogueView(time, skippable, backTexture, message,portraitTexture)

        }
        return list;
    }

    private static boolean isBriefingTest() {
        return false;
    }

    @Refactor
    @Override
    public List<Scene> get() {
        if (dialogue)
            return getScenesLinear(Eidolons.game.getMetaMaster().getDialogueFactory().getDialogue(data));
        return getScenesLinear(Eidolons.game.getMetaMaster().getIntroFactory().getDialogue(data));
    }

}
