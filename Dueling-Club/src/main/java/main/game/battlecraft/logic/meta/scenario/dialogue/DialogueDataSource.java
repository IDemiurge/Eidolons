package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.values.properties.G_PROPS;

import java.util.List;

/**
 * Created by JustMe on 5/19/2017.
 */
public class DialogueDataSource {

GameDialogue dialogue;

    public DialogueDataSource(GameDialogue dialogue) {
        this.dialogue = dialogue;
    }

    public String getText(){
        return dialogue.getSpeech().getFormattedText();
    }

    public List<String> getReplyOptions(){
//         replica.getChildren().stream()
//         .collect(Collectors.mapping(( r) -> replica.getFormattedText()), Collectors.toList());
        return null;
    }
    public String getActorImage(){
        return dialogue.getSpeech().getActor().getProperty(G_PROPS.IMAGE);
    }
    public List<String> getActorImages(){
//        return replica.getActor().getProperty(G_PROPS.IMAGE);
        return null; }

}
