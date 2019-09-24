package eidolons.ability.targeting;

import main.content.enums.entity.AbilityEnums;
import main.system.auxiliary.EnumMaster;

public class WrapTargeting {
    public WrapTargeting(String s) {
        AbilityEnums.TARGETING_MODE e = new EnumMaster<AbilityEnums.TARGETING_MODE>().retrieveEnumConst(AbilityEnums.TARGETING_MODE.class, s);



    }
}
