package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.PlainDialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.core.game.DC_Game;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 6/3/2017.
 */
public class DialogueHandler {
    GameDialogue dialogue;
    Map<Scene, Speech> map;
    DC_Game game;
    private List<Scene> list;

    public DialogueHandler(GameDialogue dialogue, DC_Game game, List<Scene> scenes) {
        this.dialogue = dialogue;
        this.game = game;
        this.list = scenes;
        this.map = new XLinkedMap<>();
        Speech line = dialogue.getRoot().getChildren().get(0);
        for (Scene actor : scenes) {
            map.put(actor, line);
            if (!line.getChildren().isEmpty())
               line = line.getChildren().get(0);
        }

    }

    public void lineSpoken(Scene actorObject) {
        Ref ref = new Ref(game);
        ref.setAmount(map.get(actorObject).getId());
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.DIALOGUE_LINE_SPOKEN, ref));
    }

    public void dialogueDone() {
        Ref ref = new Ref(game);
        ref.setValue(KEYS.STRING, dialogue.getName());
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.DIALOGUE_FINISHED, ref));
    }

    public List<Scene> getList() {
        return list;
    }

}
