package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.GridCell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.elements.task.Task;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.system.ObjUtilities;
import main.content.enums.entity.ActionEnums;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.ExceptionMaster;
import main.system.auxiliary.StringMaster;

public class AiAction {
    Ref ref;
    ActiveObj active;

    boolean complete;
    private Task task;
    private String taskDescription;
    private boolean order;

    public AiAction(ActiveObj actives, Ref ref) {
        this.active = actives;
        this.ref = ref;
        try {
            ref.setID(KEYS.ACTIVE, active.getId());
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

    public AiAction(ActiveObj action) {
        this(action, Ref.getCopy(action.getRef()));

    }

    public AiAction(ActiveObj action, Obj enemy) {
        this(action);
        setRef(enemy.getRef().getCopy());
        getRef().setTarget(enemy.getId());
        getRef().setID(KEYS.ACTIVE, action.getId());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AiAction) {
            AiAction aiAction = (AiAction) obj;
            if (aiAction.getActive().equals(getActive())) {
                return ObjUtilities.compare(aiAction.getTarget(), getTarget());
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
            if (ref.getTargetObj() instanceof GridCell) {
                name = ref.getTargetObj().getProp("name")
                        + StringMaster.wrapInBrackets(ref.getTargetObj().getCoordinates().toString());
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
                ExceptionMaster.printStackTrace(e);
            }
        }
        return targeting;
    }

    public boolean canBeTargeted() {
        if (getTarget() == null)
            return true;
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

    public ActiveObj getActive() {
        return active;
    }

    public void setActive(ActiveObj active) {
        this.active = active;
    }


    public boolean isSingle() {

        if (isDummy()) {
            return true;
        }
        return active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MODE;
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
            return active.getOwnerUnit();
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

    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }
}
