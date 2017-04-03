package main.ability.conditions.shortcut;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.unit.Unit;

public class OrderCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        Unit targetUnit = (Unit) ref.getTargetObj();

        if (targetUnit.isOwnedBy(ref.getSourceObj().getOwner())) {
            return false;
        }

//        if (!global) {
//            if (PositionMaster.getDistance(c1, c2) > max_distance)
//                return false;
//        }

        if (!targetUnit.canAct()) {
            return false;
        }
        if (targetUnit.getBehaviorMode() != null) {
            return false;
        }

        return false;
    }

}
