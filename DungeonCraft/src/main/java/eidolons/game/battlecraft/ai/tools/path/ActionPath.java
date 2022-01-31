package eidolons.game.battlecraft.ai.tools.path;

import eidolons.entity.active.ActiveObj;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionPath {

    public List<AiAction> aiActions; //dynamic field - will be changed!

    public List<Choice> choices;

    private int priority;
    private Coordinates targetCoordinates;

    public ActionPath(ActionPath path) {
        this.choices = new ListMaster<Choice>().cloneList(path.getChoices());
        setTargetCoordinates(path.getTargetCoordinates());
    }

    public ActionPath(Coordinates targetCoordinates, List<Choice> choices) {
        this.choices = (choices);
        this.targetCoordinates = targetCoordinates;
    }

    public ActionPath(Coordinates targetCoordinates, Choice... choices) {
        this(targetCoordinates, new ArrayList<>(Arrays.asList(choices)));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ActionPath) {
            ActionPath path = (ActionPath) obj;
            return new ListMaster<Choice>().compare(getChoices(), path.getChoices());
        }
        return false;
    }

    public boolean hasAction(ActiveObj action) {
        for (Choice choice : getChoices()) {
            for (AiAction a : choice.getActions()) {
                if (a.getActive().equals(action)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCoordinate(Coordinates c) {
        for (Choice choice : getChoices()) {
            if (choice.getCoordinates().equals(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Path: ");
        for (Choice choice : getChoices()) {
            stringBuilder.append(choice.toString()).append("; ");
        }
        String string = stringBuilder.toString();
        string = string.substring(0, string.length() - 2);
        return string;
    }

    private void initActions() {
        aiActions = new ArrayList<>();
        for (Choice choice : choices) {
            aiActions.addAll(choice.getActions());
        }
    }

    public List<AiAction> getActions() {
        initActions();
        return aiActions;
    }

    public void add(Choice choice) {
        getChoices().add(choice);

    }

    public List<Choice> getChoices() {
        if (choices == null) {
            choices = new ArrayList<>();
        }
        return choices;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Coordinates getTargetCoordinates() {
        return targetCoordinates;
    }

    public void setTargetCoordinates(Coordinates targetCoordinates) {
        this.targetCoordinates = targetCoordinates;
    }

}
