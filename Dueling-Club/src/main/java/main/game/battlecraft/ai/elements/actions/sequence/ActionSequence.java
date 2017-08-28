package main.game.battlecraft.ai.elements.actions.sequence;

import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.task.Task;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class ActionSequence {
    private UnitAI ai;
    private GOAL_TYPE type;
    private Task task;
    // always re-generate or retain half-executed ones?
    private List<Action> actions;
    private Integer priority;
    private int i = -1;
    private Integer priorityMultiplier;

    public ActionSequence(GOAL_TYPE type, Action... actions) {
        this(actions);
        this.type = type;
    }

    public ActionSequence(Action... actions) {
        this(new ListMaster<Action>().getList(actions), null, null);
        if (this.actions.isEmpty()) {
            setAi(getActions().get(0).getSource().getUnitAI());
        }
    }

    public ActionSequence(List<Action> actions, Task task, UnitAI ai) {
        if (task != null) {
            this.type = task.getType();
        }
        this.task = task;
        this.ai = ai;
        this.actions = actions;
        actions.forEach(action ->action.setTask(getTask()));
    }

    public ActionSequence(Task task2, UnitAI ai2, Action... actions) {
        this(new ListMaster<Action>().getList(actions), task2, ai2);
    }

    @Override
    public String toString() {
        // String actionString = "";
        // for (Action a : actions){
        //
        // }
        return  StringMaster.getWellFormattedString(getType() .toString())
         + ":" + actions.toString()
                // + " priority: " + priority
                ;

    }

    public Action getLastAction() {
        return actions.get(actions.size() - 1);
    }

    public void removeFirstAction() {
        if (actions.isEmpty()) {
            return;
        }
        actions.remove(0);
    }

    public Action getNextAction() {
        i++;
        if (actions.size() <= i) {
            return null;
        }
        return actions.get(i);

    }

    public UnitAI getAi() {
        if (ai == null) {
            setAi(getActions().get(0).getSource().getUnitAI());
        }
        return ai;
    }

    public void setAi(UnitAI ai) {
        this.ai = ai;
    }

    public GOAL_TYPE getType() {
        return type;
    }

    public void setType(GOAL_TYPE type) {
        this.type = type;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Action> getActions() {
        return actions;
    }

    public int getPriority() {
        if (priority == null) {
            return -1;
        }
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Action get(int j) {
        return getActions().get(j);
    }

    public void add(Action action) {
        getActions().add(action);
    }

    public Integer getPriorityMultiplier() {
        return priorityMultiplier;
    }

    public void setPriorityMultiplier(Integer priorityMultiplier) {
        this.priorityMultiplier = priorityMultiplier;
    }
}
