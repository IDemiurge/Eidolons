package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.values.properties.G_PROPS;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;

import java.util.List;

public class GameDialogue {
/*
actors
entity ?

"line"

tree structure

xml parsed

each replica has children
 */
    Speech speech;

    public GameDialogue(Speech root) {
        speech = root;
    }

    public String getText(){
        return speech.getFormattedText();
    }

    public List<String> getReplyOptions(){
//         replica.getChildren().stream()
//         .collect(Collectors.mapping(( r) -> replica.getFormattedText()), Collectors.toList());
        return null;
    }
    public String getActorImage(){
        return speech.getActor().getProperty(G_PROPS.IMAGE);
    }
    public List<String> getActorImages(){
//        return replica.getActor().getProperty(G_PROPS.IMAGE);
        return null; }
}
