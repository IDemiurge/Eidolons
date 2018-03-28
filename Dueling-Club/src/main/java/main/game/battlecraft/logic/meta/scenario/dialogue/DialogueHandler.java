package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.DialogScenario;

import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 6/3/2017.
 */
public class DialogueHandler {
    GameDialogue dialogue;
    Map<DialogScenario, Speech> map;
    DC_Game game;
    private List<DialogScenario> list;

    public DialogueHandler(GameDialogue dialogue, DC_Game game, List<DialogScenario> actors) {
        this.dialogue = dialogue;
        this.game = game;
        this.list = actors;
        Speech line = dialogue.getRoot().getChildren().get(0);
        for (DialogScenario actor : actors) {
            map.put(actor, line);
            line = line.getChildren().get(0);
        }

    }

    public void lineSpoken(DialogScenario actorObject) {
        Ref ref = new Ref(game);
        ref.setAmount(map.get(actorObject).getId());
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.DIALOGUE_LINE_SPOKEN, ref));
    }

    public void dialogueDone() {
        Ref ref = new Ref(game);
        ref.setValue(KEYS.STRING, dialogue.getName());
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.DIALOGUE_FINISHED, ref));
    }

    public List<DialogScenario> getList() {
        return list;
    }

}
