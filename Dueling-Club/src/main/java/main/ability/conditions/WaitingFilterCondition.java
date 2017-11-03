package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;

public class WaitingFilterCondition extends MicroCondition {
    public WaitingFilterCondition() {

    }

    public static boolean canBeWaitedUpon(Obj waiter, Unit unit) {
        if (unit.getAI().isOutsideCombat()) {
           return false;
        }
        return unit.canActNow() && (unit.checkInSight() || waiter.getOwner() == unit.getOwner());
    }

    @Override
    public boolean check(Ref ref) {
        // TODO Auto-generated method stub
        Obj obj = ref.getMatchObj();
        if (obj == ref.getSourceObj()) {
            return true;
        }
        // if (obj.getOwner() == Player.NEUTRAL)
        // return false;
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            if (canBeWaitedUpon(ref.getSourceObj(), unit)) {
                return true;
            }
        }
        return false;
    }
}
