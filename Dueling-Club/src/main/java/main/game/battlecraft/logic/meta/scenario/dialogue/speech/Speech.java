package main.game.battlecraft.logic.meta.scenario.dialogue.speech;

import main.ability.Abilities;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.data.dialogue.SpeechInterface;
import main.data.dialogue.Speeches;
import main.elements.conditions.Condition;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.List;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Speech implements SpeechInterface {

    String actorName;
    String actorNames;
    String unformattedText; // [main hero name], [gender], [race] etc (maybe {val_ref} sytanx?)
    Condition conditions; //check before addChild()
    Abilities abilities; //TODO NOW VIA SCRIPTS? OR WILL AE BE EASIER?
    String script;
    DialogueActor actor;
    List<DialogueActor> actors;
    String formattedText;
    Speech parent;
    DequeImpl<Speech> children;
    REPLICA_STATUS status;
    private SpeechData data;
    private int id;

    //last speech
    public Speech(Integer id) {
        this(id, null);
    }

    public Speech(Integer id, Speeches children) {
        this.id = id;
        if (children != null) {
            this.children = new DequeImpl<Speech>().addAllCast(children.getList());
        }
    }

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
        this.unformattedText = text;
        this.data = data;
//        unpackData(data);
    }

    public Speech(String actor, String actors, String text, Condition condition
     , Speeches children
    ) {
        this.children = new DequeImpl<Speech>().getAddAllCast(children.getList());
    }

    public SpeechBuilder getSpeechBuilder(String path) {
        return new SpeechBuilder(path );  }

    public void addChild(Speech child) {
        getChildren().add(child);
    }

    public void init(MetaGameMaster master, Speech parent) {
        this.parent = parent;
        if (actorName == null)
            actorName = data.getValue(SPEECH_VALUE.ACTOR);
        if (!StringMaster.isEmpty(actorName))
        try {
            actor = master.getDialogueActorMaster().getActor(actorName);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (actorNames == null)
            actors = parent.getActors();
        actors = master.getDialogueActorMaster().getActors(actorNames);
        formattedText = unformattedText;// getDialogueFormatter().format(unformattedText, this);
    }

    public Condition getConditions() {
        return conditions;
    }

    public void setConditions(Condition conditions) {
        this.conditions = conditions;
    }

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<DialogueActor> getActors() {
        return actors;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public void setFormattedText(String formattedText) {
        this.formattedText = formattedText;
    }

    public Speech getParent() {
        return parent;
    }

    public DequeImpl<Speech> getChildren() {
        if (children == null) children = new DequeImpl<>();
        return children;
    }

    public REPLICA_STATUS getStatus() {
        return status;
    }

    public void setStatus(REPLICA_STATUS status) {
        this.status = status;
    }

    public DialogueActor getActor() {
        return actor;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public void setActorNames(String actorNames) {
        this.actorNames = actorNames;
    }

    public void setUnformattedText(String unformattedText) {
        this.unformattedText = unformattedText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpeechData getData() {
        return data;
    }

    public void setData(SpeechData data) {
        this.data = data;
    }

    public enum REPLICA_STATUS {
        NEW,
        REPEATED,

    }
}
