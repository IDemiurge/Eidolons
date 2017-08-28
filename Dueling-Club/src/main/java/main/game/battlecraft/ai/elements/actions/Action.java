package main.game.battlecraft.ai.elements.actions;

import main.content.enums.entity.ActionEnums;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.task.Task;
import main.game.battlecraft.ai.tools.target.TargetingMaster;
import main.system.ObjUtilities;
import main.system.auxiliary.StringMaster;

public class Action {
    Ref ref;
    DC_ActiveObj active;

    boolean complete;
    private Task task;
    private String taskDescription;

    public Action(DC_ActiveObj actives, Ref ref) {
        this.active = actives;
        this.ref = ref;
        ref.setID(KEYS.ACTIVE, active.getId());
    }

    public Action(DC_ActiveObj action) {
        this(action, Ref.getCopy(action.getRef()));

    }

    public Action(DC_ActiveObj action, Obj enemy) {
        this(action);
        setRef(enemy.getRef().getCopy());
        getRef().setTarget(enemy.getId());
        getRef().setID(KEYS.ACTIVE, action.getId());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Action) {
            Action action = (Action) obj;
            if (action.getActive().equals(getActive())) {
                if (ObjUtilities.compare(action.getTarget(), getTarget())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (!active.getLogger().isTargetLogged())
            return active.getName();
        if (ref.getTargetObj() != null) {
            String name = ref.getTargetObj().getName();
            if (ref.getTargetObj() instanceof DC_Cell) {
                name = ref.getTargetObj().getProp("name")
                 + StringMaster.wrapInBraces(ref.getTargetObj().getCoordinates().toString());
            }
            return active.getName() + " on " + name;
        }
        return ref.getSourceObj().getName() + "'s " + active.getName();
    }

    public boolean canBeTargeted(Integer id) {
        ref.setID(KEYS.ACTIVE, active.getId());
        return active.canBeTargeted(id);
    }

    public Targeting getTargeting() {
        Targeting targeting = active.getTargeting();
        if (targeting == null) {
            active.construct();
            targeting = active.getTargeting();
        }
        if (targeting == null) {
            try {
                targeting = TargetingMaster.findTargeting(active, SelectiveTargeting.class); // list?
                // preCheck
                // both?
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return targeting;
    }

    public boolean canBeTargeted() {
        return canBeTargeted(getTarget().getId());
    }

    public boolean canBeTargetedOnAny() {
        ref.setID(KEYS.ACTIVE, active.getId());
        return active.canTargetAny();

    }

    public boolean canBeActivated() {
        // if (active.canBeActivated(ref, false))
        // return true;
        // return active.canBeActivated(ref, true);
        return active.canBeActivated(ref, false);
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    public DC_ActiveObj getActive() {
        return active;
    }

    public void setActive(DC_ActiveObj active) {
        this.active = active;
    }


    public boolean isSingle() {

        if (isDummy()) {
            return true;
        }
        if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MODE) {
            return true;
        }
        return false;
    }

    public DC_Obj getTarget() {
        if (ref.getTargetObj() == null) {
            if (active.getTargeting() instanceof AutoTargeting
             || active.getTargeting() instanceof FixedTargeting) {
                active.getTargeting().select(ref);
            }
        }
        try {
            return (DC_Obj) ref.getTargetObj();
        } catch (Exception e) {
            return null;
        }
    }

    public Unit getSource() {
        try {
            return (Unit) ref.getSourceObj();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isDummy() {
        return false;
        // getActive().getName().equals("Move");
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getTaskDescription() {
        if (taskDescription == null)
            if (task != null)
                return task.toShortString();
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
}
