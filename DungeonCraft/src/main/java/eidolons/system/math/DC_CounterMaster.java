package eidolons.system.math;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.system.auxiliary.EnumMaster;
import main.system.entity.CounterMaster;

public class DC_CounterMaster {

    public static float getCounterPriority(String counterName, DC_Obj target) {
        if (target instanceof Unit) {
            Unit heroObj = (Unit) target;
            String realName = CounterMaster.findCounter(counterName);
            switch (new EnumMaster<COUNTER>().retrieveEnumConst(
             COUNTER.class, realName)) {
                case Blaze:
                    if (!target.getGame().getRules().getBlazeRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Bleeding:
                    if (!target.getGame().getRules().getBleedingRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Blight:
                    if (!target.getGame().getRules().getBlightRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Disease:
                    if (!target.getGame().getRules().getDiseaseRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Ensnared:
                    if (!target.getGame().getRules().getEnsnareRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Chill:
                    if (!target.getGame().getRules().getFreezeRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Poison:
                    if (!target.getGame().getRules().getPoisonRule()
                     .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                default:
                    break;

            }
        }
        return CounterMaster.getCounterPriority(counterName, target);
    }

}
