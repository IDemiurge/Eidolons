package eidolons.game.battlecraft.ai.elements.task;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;

public class Task {
    private final GOAL_TYPE type;
    private final Object arg;
    private final Unit unit;
    private final UnitAI ai;
    private boolean blocked;
    private boolean forced;

    // a more concrete directive than Goal; from which Actions can be derived
    public Task(UnitAI ai, GOAL_TYPE type, Object arg) {
        this.ai = ai;
        this.unit = ai.getUnit();
        this.type = type;
        this.arg = arg;
    }

    public Task(boolean forced, UnitAI ai2, GOAL_TYPE goal, Object object) {
        this(ai2, goal, object);
        this.setForced(forced);
    }

    public String toShortString() {
        try {
            return StringMaster.format(type.toString()) + " on "
             +
             (arg instanceof Integer ?
              getUnit().getGame().getObjectById((Integer) arg).getNameIfKnown() :
              arg == null ? "" : arg);
        } catch (Exception e) {
            return toString();
        }
    }

    @Override
    public String toString() {
        return
         ai.getUnit().getName() + "'s " +
          type + " on "
          +
          (arg instanceof Integer ?
           getUnit().getGame().getObjectById((Integer) arg) :
           arg == null ? "" : arg);

    }

    public UnitAI getAI() {
        return ai;
    }

    public Unit getUnit() {
        return unit;
    }

    public Obj getObjArg() {
        return getUnit().getGame().getObjectById(
         (Integer) getArg());
    }

    /**
     * @return the type
     */
    public synchronized GOAL_TYPE getType() {
        return type;
    }

    /**
     * @return the arg
     */
    public synchronized Object getArg() {
        return arg;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public enum TASK_DESCRIPTION {
        AMMO,
        ATTACK,
        APPROACH,
        WAIT,
        RESTORATION, //"Restoration"
        ;

    }

}
