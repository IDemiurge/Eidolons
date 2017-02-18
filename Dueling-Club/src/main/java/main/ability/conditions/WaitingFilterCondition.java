package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;

public class WaitingFilterCondition extends MicroCondition {
    public WaitingFilterCondition() {

    }

    public static boolean canBeWaitedUpon(Obj waiter, DC_UnitModel unit) {
        return unit.canActNow() && (unit.checkInSight() || waiter.getOwner() == unit.getOwner());
    }

    @Override
    public boolean check() {
        // TODO Auto-generated method stub
        Obj obj = ref.getMatchObj();
        if (obj == ref.getSourceObj()) {
            return true;
        }
        // if (obj.getOwner() == Player.NEUTRAL)
        // return false;
        if (obj instanceof DC_UnitModel) {
            DC_UnitModel unit = (DC_UnitModel) obj;
            if (canBeWaitedUpon(ref.getSourceObj(), unit)) {
                return true;
            }
        }
        return false;
    }
}
