package eidolons.ability.conditions.shortcut;

import eidolons.entity.obj.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

//all orders should check this
public class OrderCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        Unit targetUnit = (Unit) ref.getTargetObj();

        if (!targetUnit.isOwnedBy(ref.getSourceObj().getOwner())) {
            return false;
        }

//        if (!global) {
//            if (PositionMaster.getDistance(c1, c2) > max_distance)
//                return false;
//        }

        if (!targetUnit.canAct()) {
            return false;
        }
        return targetUnit.getBehaviorMode() == null;
    }

}
