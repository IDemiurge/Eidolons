package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.entity.obj.unit.Unit;

public class OrderEffect extends DC_Effect {

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() instanceof Unit) {
            Unit unit = (Unit) ref.getTargetObj();
            if (ref.getTargetObj() == ref.getSourceObj()) {

            }
            // global orders?
/*            if (!OrderMaster.giveOrders(unit, null)) {
                ref.getActive().setCancelled(true);
                return false;
            }*/
            // TODO support cancel!
            return true;
        }
        return false;
    }

}
