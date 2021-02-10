package eidolons.ability.conditions.special;

import eidolons.system.math.roll.RollMaster;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class RollCondition extends MicroCondition {

    private ROLL_TYPES roll_type;

    public RollCondition(ROLL_TYPES roll_type) {
        this.roll_type = roll_type;
    }

    @Override
    public boolean check(Ref ref) {
        return RollMaster.roll(roll_type, ref);
    }

}
