package eidolons.game.battlecraft.logic.meta.scenario.scene;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.Scene;
import eidolons.game.core.Core;
import main.system.auxiliary.Refactor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 5/22/2017.
 */
@Deprecated
public class SceneFactory implements Supplier<List<Scene>> {
    private final String data;
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
        //TODO gdx sync
//         for (Speech speech : fullData) {
//             SpeechData data = speech.getData();
//             if (isBriefingTest()) {
//                 list.add(new BriefingView(new BriefScene(
//                         new BriefingData(
//                                 data.getValue(SPEECH_VALUE.SPRITE),
//                                 data.getValue(SPEECH_VALUE.BACKGROUND),
//                                 data.getValue(SPEECH_VALUE.MESSAGE), false
//                         ))));
//             } else {
//                 DialogueActor actor1 = DialogueActorMaster.getActor(data.getActorLeft());
//                 DialogueActor actor2 =StringMaster.isEmpty(  data.getActorRight()) ? null : DialogueActorMaster.getActor(data.getActorRight());
//
//                 list.add(new DialogueView(new SpeechDataSource(speech, new ActorDataSource(actor1),
//                         new ActorDataSource(actor2))));
//             }
// //                    new PlainDialogueView(time, skippable, backTexture, message,portraitTexture)
//
//         }
        return list;
    }

    private static boolean isBriefingTest() {
        return false;
    }

    @Refactor
    @Override
    public List<Scene> get() {
        if (dialogue)
            return getScenesLinear(Core.game.getMetaMaster().getDialogueFactory().getDialogue(data));
        return getScenesLinear(Core.game.getMetaMaster().getIntroFactory().getDialogue(data));
    }

}
