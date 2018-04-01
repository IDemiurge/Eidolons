package eidolons.game.battlecraft.rules.perk;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.ability.conditions.StatusCheckCondition;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;

/**
 * Created by JustMe on 12/26/2016.
 */
public class AirborneRule {

    private static final int REACH_HEIGHT = 500;

    public static void applyAirborne(Unit obj) {
        obj.setParameter(PARAMS.HEIGHT, REACH_HEIGHT);

    }

    public static Condition getMeleeAttackCondition() {
        return new Conditions(new StatusCheckCondition("", UnitEnums.STATUS.AIRBORNE),
         new NotCondition(new NumericCondition("{source_height}", "" + REACH_HEIGHT, false
         ))
        );

    }

}
