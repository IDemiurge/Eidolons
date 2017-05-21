package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.values.properties.G_PROPS;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/19/2017.
 */
public class DialogueDataSource {

    private   List<String> displayedOptions;
    private   Speech speech;

    public DialogueDataSource(Speech speech, List<String> displayedOptions) {
        this.speech = speech;
        this.displayedOptions = displayedOptions;
    }

    public String getText() {
        return speech.getFormattedText();
    }

    public List<String> getReplyOptions() {
//         replica.getChildren().stream()
//         .collect(Collectors.mapping(( r) -> replica.getFormattedText()), Collectors.toList());
        return displayedOptions;
    }

    public String getActorImage() {
        return speech.getActor().getProperty(G_PROPS.IMAGE);
    }

    public List<String> getActorImages() {
        return speech.getActors().stream().map(
         actor-> actor.getProperty(G_PROPS.IMAGE)).collect(Collectors.toList());
    }

}
