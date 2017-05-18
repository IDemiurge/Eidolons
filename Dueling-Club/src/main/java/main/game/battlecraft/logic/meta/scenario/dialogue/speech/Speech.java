package main.game.battlecraft.logic.meta.scenario.dialogue.speech;

import main.ability.Abilities;
import main.data.dialogue.SpeechData;
import main.data.dialogue.SpeechInterface;
import main.data.dialogue.Speeches;
import main.elements.conditions.Condition;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import main.system.datatypes.DequeImpl;

import java.util.List;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Speech implements SpeechInterface {

    private   SpeechData data;
    String actorName;
    String actorNames;
    String unformattedText; // [main hero name], [gender], [race] etc (maybe {val_ref} sytanx?)
    Condition conditions; //check before addChild()
    Abilities abilities; //activate on start

    DialogueActor actor;
    List<DialogueActor> actors;
    String formattedText;
    Speech parent;
    DequeImpl<Speech> children;
    REPLICA_STATUS status;

    public Speech(String actor, String text) {
        this(actor, null, text, null);
    }

    public Speech(String actor, String actors, String text) {
        this(actor, actors, text, null);
    }

    public Speech(String actor, String actors, String text, Condition condition) {
        this.actorName = actor;
        this.actorNames = actors;
        this.unformattedText = text;
        this.conditions = condition;
    }
    public Speech(String text, SpeechData data) {
        this.unformattedText=text;
        this.data=data;
//        unpackData(data);
    }

    public Speech(String actor, String actors, String text, Condition condition
    , Speeches children
    ) {
       this.children = new DequeImpl<Speech>().getAddAllCast(children.getList());
    }
    public void addChild(Speech child) {
        children.add(child);
    }

    public void init(ScenarioMetaMaster master, Speech parent) {
        this.parent = parent;
        actor = master.getDialogueActorMaster().getActor(actorName);
        if (actorNames == null)
            actors = parent.getActors();
        actors =master.getDialogueActorMaster().getActors(actorNames);
        formattedText =unformattedText;// getDialogueFormatter().format(unformattedText, this);
    }

    public Condition getConditions() {
        return conditions;
    }

    public List<DialogueActor> getActors() {
        return actors;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public Speech getParent() {
        return parent;
    }

    public DequeImpl<Speech> getChildren() {
        return children;
    }

    public REPLICA_STATUS getStatus() {
        return status;
    }

    public DialogueActor getActor() {
        return actor;
    }

    public enum REPLICA_STATUS {
        NEW,
        REPEATED,

    }
}
