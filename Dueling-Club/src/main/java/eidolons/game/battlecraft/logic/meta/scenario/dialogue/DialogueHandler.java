package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.*;
import eidolons.game.core.game.DC_Game;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
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
    private ActorDataSource listenerLast;
    private ActorDataSource myActor;

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
        setListenerLast(
                new ActorDataSource(DialogueActorMaster.getActor(dialogue.getRoot().getData().getActorLeft())));

    }

    public SpeechDataSource lineSpoken(Speech speech, int index) {
        ArrayList<String> displayedOptions = new ArrayList<>();
        if (speech.getChildren().size() <= index) {

            if (isLoopDialogueTest() ){
                return new SpeechDataSource(dialogue.getRoot());
            }

            return null;
        }
        Speech displayedSpeech = speech.getChildren().get(index);

        if (dialogue instanceof LinearDialogue) {
            displayedOptions.add("Continue");
        } else {
//            dialogue.getRoot()
            displayedSpeech
                    .getChildren().forEach(s -> {
                if (s.getConditions() == null) {
                    displayedOptions.add(s.getFormattedText());
                } else {
                    Ref ref = new Ref(displayedSpeech.getActor().getLinkedUnit());
                    if (s.getConditions().check(ref))
                        displayedOptions.add(s.getFormattedText());
                }
            });
        }
//        Speech newSpeech= dialogue.getRoot();
        SpeechDataSource data = new SpeechDataSource(displayedSpeech);
        data.getResponses().addAll(displayedOptions);

        setListenerLast(new ActorDataSource(data.getSpeech().getActor()));
        return data;
    }

    private boolean isLoopDialogueTest() {
        return false;
    }

    public void lineSpoken(Scene actorObject, String line) {
//        dialogue.getRoot().getChildren()
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

    public boolean isMe(ActorDataSource listener) {
        if (listener.equals(myActor)) {
            return true;
        }
        if (listener.getActorName().equalsIgnoreCase("you")) {
            return true;
        }
        return false;
    }

    public ActorDataSource getListenerLast() {
        return listenerLast;
    }

    public void setListenerLast(ActorDataSource listenerLast) {
        if (isMe(listenerLast)) {
//            listenerLast.image= "";
            setMyActor(listenerLast);
        }
        this.listenerLast = listenerLast;
    }

    public ActorDataSource getMyActor() {
        return myActor;
    }

    public void setMyActor(ActorDataSource myActor) {
        this.myActor = myActor;
    }
}
