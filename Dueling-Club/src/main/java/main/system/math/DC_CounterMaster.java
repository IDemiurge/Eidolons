package main.system.math;

import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.system.auxiliary.EnumMaster;
import main.system.util.CounterMaster;

public class DC_CounterMaster {

    public static float getCounterPriority(String counterName, DC_Obj target) {
        if (target instanceof DC_HeroObj) {
            DC_HeroObj heroObj = (DC_HeroObj) target;
            String realName = CounterMaster.findCounter(counterName);
            switch (new EnumMaster<STD_COUNTERS>().retrieveEnumConst(
                    STD_COUNTERS.class, realName)) {
                case Blaze_Counter:
                    if (!target.getGame().getRules().getBlazeRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Bleeding_Counter:
                    if (!target.getGame().getRules().getBleedingRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Blight_Counter:
                    if (!target.getGame().getRules().getBlightRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Corrosion_Counter:
                    break;
                case Despair_Counter:
                    break;
                case Disease_Counter:
                    if (!target.getGame().getRules().getDiseaseRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Ensnared_Counter:
                    if (!target.getGame().getRules().getEnsnareRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Freeze_Counter:
                    if (!target.getGame().getRules().getFreezeRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Hatred_Counter:
                    break;
                case Lust_Counter:
                    break;
                case Madness_Counter:
                    break;
                case Moist_Counter:
                    break;
                case Poison_Counter:
                    if (!target.getGame().getRules().getPoisonRule()
                            .checkApplies(heroObj)) {
                        return 0;
                    }
                    break;
                case Rage_Counter:
                    break;
                case Soul_Counter:
                    break;
                case Undying_Counter:
                    break;
                default:
                    break;

            }
        }
        return CounterMaster.getCounterPriority(counterName, target);
    }

}
