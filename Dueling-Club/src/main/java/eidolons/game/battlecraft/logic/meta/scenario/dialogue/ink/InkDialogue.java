package eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.Story.VariableObserver;
import com.bladecoder.ink.runtime.StoryException;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 11/20/2018.
 */
public class InkDialogue {

    Story story;
    DialogueActor mainActor;
    List<DialogueActor> actors;

    public List<String> getGlobalTags() throws Exception {
        return story.getGlobalTags();
    }

    public boolean canContinue() {
        return story.canContinue();
    }

    public String Continue() {
        try {
            return story.Continue();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return null;
    }

    public void observeVariable(String variableName, VariableObserver observer) throws StoryException, Exception {
        story.observeVariable(variableName, observer);
    }

    public void resetState() throws Exception {
        story.resetState();
    }

    public void choosePathString(String path) throws Exception {
        story.choosePathString(path);
    }

    public List<String> getResponses() {
        return story.getCurrentChoices().stream().map(
         choice -> choice.getText()).collect(Collectors.toList());
    }

    public List<Choice> getCurrentChoices() {
        return story.getCurrentChoices();
    }

    public String getCurrentText() throws Exception {
        return story.getCurrentText();
    }
}
