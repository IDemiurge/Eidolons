package main.game.battlecraft.logic.meta.scenario.scene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.dialogue.DataString;
import main.data.dialogue.SpeechData;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.libgdx.DialogScenario;
import main.libgdx.texture.TextureCache;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/22/2017.
 */
public class SceneFactory {
    DialogueActorMaster actorMaster;
//TODO Speech?
    public List<DialogScenario> getScenes(List<SpeechData> fullData) {
        Speech speech;
//        speech.getFormattedText()
        List<DialogScenario> list = new LinkedList<>();
//        for (String substring : StringMaster.openContainer(data)) {
        for (SpeechData data : fullData) {
            TextureRegion portraitTexture = null;
            String message = null;
            TextureRegion backTexture = null;
            boolean skippable = false;
            Integer time = null;
            for (DataString dataString : data.getStrings()) {
                switch (dataString.getType()) {
                    case ACTOR:
                        portraitTexture =
                         TextureCache.getOrCreateR(actorMaster.
                          getActor(dataString.getData()).getLinkedUnit().getImagePath());
                        break;
                    case BACKGROUND:
                        TextureCache.getOrCreateR(dataString.getData());
                        break;
                }
            }

            list.add(new DialogScenario(time, skippable, backTexture, message,
             portraitTexture));

        }
        return list;
    }
}
