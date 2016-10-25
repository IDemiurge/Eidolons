package main.ability.conditions.special;

import main.content.CONTENT_CONSTS.ROLL_TYPES;
import main.elements.conditions.MicroCondition;
import main.system.math.roll.RollMaster;

public class RollCondition extends MicroCondition {

    private ROLL_TYPES roll_type;

    public RollCondition(ROLL_TYPES roll_type) {
        this.roll_type = roll_type;
    }

    @Override
    public boolean check() {
        return RollMaster.roll(roll_type, ref);
    }

}
