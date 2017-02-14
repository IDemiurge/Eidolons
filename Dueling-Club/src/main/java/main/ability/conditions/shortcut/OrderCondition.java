package main.ability.conditions.shortcut;

import main.elements.conditions.MicroCondition;
import main.entity.obj.unit.DC_HeroObj;

public class OrderCondition extends MicroCondition {

    @Override
    public boolean check() {
        DC_HeroObj targetUnit = (DC_HeroObj) ref.getTargetObj();

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
