package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.entity.Ref;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

public class DialogueWizard {
    GameDialogue dialogue;
    List<String> displayedOptions;
    Speech displayedSpeech;

    public DialogueWizard(GameDialogue dialogue) {
        this.dialogue = dialogue;
    }

    public void next(int optionIndex) {
        displayedOptions = new ArrayList<>();
        displayedSpeech = displayedSpeech.getChildren().get(optionIndex);
        if (dialogue instanceof LinearDialogue) {
            displayedOptions.add("Continue");
        } else {
            dialogue.getRoot().getChildren().forEach(speech -> {
                Ref ref = new Ref(dialogue.getRoot().getActor().getLinkedUnit());
                if (dialogue.getRoot().getConditions().check(ref)) {
                    displayedOptions.add(speech.getFormattedText());
                }
            });
        }
        GuiEventManager.trigger(GuiEventType.DIALOGUE_UPDATED,
                new DialogueDataSource(
                        displayedSpeech,
                        displayedOptions));
    }

    public void start() {


        GuiEventManager.bind(GuiEventType.DIALOGUE_OPTION_CHOSEN, p -> {
            next((Integer) p.get());
        });
//        GuiEventManager.bind(GuiEventType.DIALOGUE_SKIPPED, p -> {
//            dialogue.skipped();
//        });
    }
}
