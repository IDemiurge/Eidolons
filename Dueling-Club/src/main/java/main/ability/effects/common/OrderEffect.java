package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.entity.obj.DC_HeroObj;

public class OrderEffect extends DC_Effect {

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() instanceof DC_HeroObj) {
            DC_HeroObj unit = (DC_HeroObj) ref.getTargetObj();
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
