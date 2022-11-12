package eidolons.ability.conditions.special;

import eidolons.system.math.roll.DiceMaster;
import eidolons.system.math.roll.Roll;
import eidolons.system.math.roll.RollMaster;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.RollType;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class RollCondition extends MicroCondition {

    private RollType roll_type;
    private GenericEnums.DieType die=DiceMaster.DEFAULT_DIE;

    public RollCondition(RollType roll_type) {
        this.roll_type = roll_type;
    }

    public RollCondition(RollType roll_type, GenericEnums.DieType die) {
        this.roll_type = roll_type;
        this.die = die;
    }

    @Override
    public boolean check(Ref ref) {

        return RollMaster.roll( new Roll(roll_type, die), ref);
    }

}
