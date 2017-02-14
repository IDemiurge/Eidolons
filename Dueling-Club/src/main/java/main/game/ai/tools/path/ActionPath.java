package main.game.ai.tools.path;

import main.entity.active.DC_ActiveObj;
import main.game.ai.elements.actions.Action;
import main.game.ai.tools.path.PathBuilder.Choice;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.ListMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ActionPath {

    public List<Action> actions;
    // public List<Coordinates> coordinates;
    public List<Choice> choices;

    private int priority;
    private Coordinates targetCoordinates;

    public ActionPath(ActionPath path) {
        this.choices = new ListMaster<Choice>().cloneList(path.getChoices());
        setTargetCoordinates(path.getTargetCoordinates());
    }

    public ActionPath(Coordinates targetCoordinates, Choice... choices) {
        this.choices = new LinkedList<>(Arrays.asList(choices));
        this.targetCoordinates = targetCoordinates;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ActionPath) {
            ActionPath path = (ActionPath) obj;
            return new ListMaster<Choice>().compare(getChoices(), path.getChoices());
        }
        return false;
    }

    public boolean hasAction(DC_ActiveObj action) {
        for (Choice choice : getChoices()) {
            for (Action a : choice.getActions()) {
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
        String string = "Path: ";
        for (Choice choice : getChoices()) {
            string += choice.toString() + "; ";
        }
        string = string.substring(0, string.length() - 2);
        return string;
    }

    private void initActions() {
        actions = new LinkedList<>();
        for (Choice choice : choices) {
            actions.addAll(choice.getActions());
        }
    }

    public List<Action> getActions() {
        initActions();
        return actions;
    }

    public void add(Choice choice) {
        getChoices().add(choice);

    }

    public List<Choice> getChoices() {
        if (choices == null) {
            choices = new LinkedList<>();
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
