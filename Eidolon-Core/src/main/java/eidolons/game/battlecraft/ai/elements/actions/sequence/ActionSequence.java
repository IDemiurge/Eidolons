package eidolons.game.battlecraft.ai.elements.actions.sequence;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.task.Task;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class ActionSequence {
    private UnitAI ai;
    private GOAL_TYPE type;
    private Task task;
    // always re-generate or retain half-executed ones?
    private List<AiAction> aiActions;
    private Integer priority;
    private int i = 0;
    private Integer priorityMultiplier;

    public ActionSequence(GOAL_TYPE type, AiAction... aiActions) {
        this(aiActions);
        this.type = type;
    }

    public ActionSequence(AiAction... aiActions) {
        this(new ListMaster<AiAction>().getList(aiActions), null, null);
        if (this.aiActions.isEmpty()) {
            setAi(getActions().get(0).getSource().getUnitAI());
        }
    }

    public ActionSequence(List<AiAction> aiActions, UnitAI ai) {
        this(aiActions, null, ai);
    }

    public ActionSequence(List<AiAction> aiActions, Task task, UnitAI ai) {
        if (task != null) {
            this.type = task.getType();
        }
        this.task = task;
        this.ai = ai;
        this.aiActions = aiActions;
        aiActions.forEach(action -> action.setTask(getTask()));
    }

    public ActionSequence(Task task2, UnitAI ai2, AiAction... aiActions) {
        this(new ListMaster<AiAction>().getList(aiActions), task2, ai2);
    }

    @Override
    public String toString() {
        // String actionString = "";
        // for (Action a : actions){
        //
        // }
        return StringMaster.toStringForm(getType())
         + ":" + aiActions.toString()
         // + " priority: " + priority
         ;

    }

    public AiAction getLastAction() {
        return aiActions.get(aiActions.size() - 1);
    }

    public void removeFirstAction() {
        if (aiActions.isEmpty()) {
            return;
        }
        aiActions.remove(0);
    }

    public AiAction getCurrentAction() {
        return aiActions.get(i);
    }

    public AiAction peekNextAction() {
        if (aiActions.size() <= i) {
            return null;
        }
        return aiActions.get(i);
    }

    public AiAction popNextAction() {
        if (aiActions.size() <= i) {
            return null;
        }
        return aiActions.get(i++);

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
        if (task == null)
            task = new Task(getAi(), getType(), "");
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<AiAction> getActions() {
        return aiActions;
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

    public AiAction get(int j) {
        return getActions().get(j);
    }

    public void add(AiAction aiAction) {
        getActions().add(aiAction);
    }

    public Integer getPriorityMultiplier() {
        return priorityMultiplier;
    }

    public void setPriorityMultiplier(Integer priorityMultiplier) {
        this.priorityMultiplier = priorityMultiplier;
    }
}
