package eidolons.ability.effects.containers.customtarget;

import eidolons.system.DC_ConditionMaster;
import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.content.enums.GenericEnums;
import main.data.DataManager;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref.KEYS;
import main.system.entity.ConditionMaster;
import main.system.math.PositionMaster;

public class RayEffect extends SpecialTargetingEffect {
    private String distance;

    public RayEffect(Effect effects) {
        this(effects, null);
    }

    public RayEffect(Effect effects, String distance) {
        this.effects = effects;
        this.distance = distance;
        effects.setReconstruct(true);
    }

    public void initTargeting() {

        Conditions conditions =ZoneTargeter.initConditions(this, allyOrEnemyOnly, ref  );
        if (distance != null) {
            conditions.add(ConditionMaster.getDistanceFilterCondition(
             KEYS.SOURCE.toString(), distance));
        }
        this.targeting = new AutoTargeting(conditions, DataManager.BF_TYPES);
        setFilteringConditions(conditions);
//        if (PositionMaster.inLine(ref.getTargetObj().getCoordinates(), ref
//         .getSourceObj().getCoordinates())) {
//            conditions.add(ConditionMaster.getLineCondition(ref.getSourceObj(),
//             ref.getTargetObj(), false));
//        } else {
//            conditions.add(ConditionMaster.getDiagonalLineCondition(
//             ref.getSourceObj(), ref.getTargetObj(), false));
//        }
//        // conditions.add(ConditionMaster.getUnitTypeCondition());
//        conditions.add(ConditionMaster.getNotDeadCondition());
//        if (allyOrEnemyOnly != null) {
//            if (allyOrEnemyOnly) {
//                conditions.add(ConditionMaster.getAllyCondition());
//            } else {
//                conditions.add(ConditionMaster.getEnemyCondition());
//            }
//        }
//        if (ref.getObj(KEYS.ACTIVE).checkBool(GenericEnums.STD_BOOLS.BLOCKED)) {
//            conditions.add(DC_ConditionMaster.getClearShotFilterCondition());
//        }

    }

    @Override
    public boolean applyThis() {

        return super.applyThis();
    }
}
