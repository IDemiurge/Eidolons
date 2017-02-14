package main.game.ai.elements.actions;

import main.content.CONTENT_CONSTS.ACTION_TYPE_GROUPS;
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
import main.entity.obj.unit.DC_HeroObj;
import main.game.ai.tools.target.TargetingMaster;
import main.system.ObjUtilities;
import main.system.auxiliary.StringMaster;
import main.system.threading.Weaver;

public class Action {
    Ref ref;
    DC_ActiveObj active;

    boolean complete;

    public Action(DC_ActiveObj actives, Ref ref) {
        this.active = actives;
        this.ref = ref;
        ref.setID(KEYS.ACTIVE, active.getId());
    }

    public Action(DC_ActiveObj action) {
        this(action, Ref.getCopy(action.getRef()));
        Weaver.inNewThread(new Runnable() {
            public void run() {
                add();
            }
        });
    }

    public Action(DC_ActiveObj action, Obj enemy) {
        this(action);
        setRef(enemy.getRef().getCopy());
        getRef().setTarget(enemy.getId());
        getRef().setID(KEYS.ACTIVE, action.getId());
    }

    public void add() {
        try {
            getSource().getUnitAI().addAction(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                // check
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

    public boolean activate() {
        if (ref.getTargetObj() == null) {
            if (!(getActive().getTargeting() instanceof SelectiveTargeting)) {
                // TODO
                // target =ActionManager.forceSelectTarget(this);
                getActive().selectTarget(ref);
            }

        }
        boolean result;

        if (getActive().isChanneling()) {
            result = getActive().activate();
        } else {
            result = getActive().activate(ref);
        }
        getActive().actionComplete();
        return result;
    }

    public boolean isSingle() {

        if (isDummy()) {
            return true;
        }
        if (active.getActionGroup() == ACTION_TYPE_GROUPS.MODE) {
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

    public DC_HeroObj getSource() {
        try {
            return (DC_HeroObj) ref.getSourceObj();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isDummy() {
        return false;
        // getActive().getName().equals("Move");
    }

}
