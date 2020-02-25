package eidolons.ability.effects.containers.customtarget;

import eidolons.system.DC_ConditionMaster;
import main.ability.effects.Effect;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.system.entity.ConditionMaster;
import main.system.math.PositionMaster;

public class ZoneTargeter {
    public static Conditions initConditions(Effect effect, Boolean allyOrEnemyOnly, Ref ref) {
//        boolean dead;
//        boolean clearshotReq;
        Conditions conditions = new Conditions();
//        if (distance != null) {
//            conditions.add(ConditionMaster.getDistanceFilterCondition(
//                    Ref.KEYS.SOURCE.toString(), distance));
//        }

        conditions.add(ConditionMaster.getValidZoneTargetCondition());
        conditions.add(getSpecialConditions(effect, ref));
        // conditions.add(ConditionMaster.getUnitTypeCondition());
        conditions.add(ConditionMaster.getNotDeadCondition());

        if (allyOrEnemyOnly != null) {
            if (allyOrEnemyOnly) {
                conditions.add(ConditionMaster.getAllyCondition());
            } else {
                conditions.add(ConditionMaster.getEnemyCondition());
            }
        }
        if (ref.getObj(Ref.KEYS.ACTIVE).checkBool(GenericEnums.STD_BOOLS.BLOCKED)) {
            conditions.add(DC_ConditionMaster.getClearShotFilterCondition());
        }
        return (conditions);
    }

    private static Condition getSpecialConditions(Effect effect, Ref ref) {
        Conditions conditions = new Conditions();
        switch (effect.getClass().getSimpleName()) {
            case "RayEffect":
                if (PositionMaster.inLine(ref.getTargetObj().getCoordinates(), ref
                        .getSourceObj().getCoordinates())) {
                    conditions.add(ConditionMaster.getLineCondition(ref.getSourceObj(),
                            ref.getTargetObj(), false));
                } else {
                    conditions.add(ConditionMaster.getDiagonalLineCondition(
                            ref.getSourceObj(), ref.getTargetObj(), false));

                }
        }
        return conditions;
    }

    public static OBJ_TYPE getTargetsObjType(Effect effects, Ref ref) {
        return  C_OBJ_TYPE.BF_OBJ;
    }
}
