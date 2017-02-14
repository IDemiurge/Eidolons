package main.rules.perk;

import main.ability.conditions.StatusCheckCondition;
import main.content.CONTENT_CONSTS;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.entity.obj.unit.DC_HeroObj;

/**
 * Created by JustMe on 12/26/2016.
 */
public class AirborneRule {

    private static final  int REACH_HEIGHT = 500;

    public static void applyAirborne(DC_HeroObj obj) {
        obj.setParameter(PARAMS.HEIGHT, REACH_HEIGHT);

    }
        public static Condition getMeleeAttackCondition() {
return new Conditions(new StatusCheckCondition("", CONTENT_CONSTS.STATUS.AIRBORNE),
        new NotCondition( new NumericCondition("{source_height}", ""+REACH_HEIGHT, false
        ))
        );

    }

}
