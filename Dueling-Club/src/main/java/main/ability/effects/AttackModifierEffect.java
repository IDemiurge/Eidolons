package main.ability.effects;

import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.effects.oneshot.common.AddTriggerEffect;
import main.elements.conditions.Condition;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.game.event.Event.EVENT_TYPE;
import main.system.ConditionMaster;

public class AttackModifierEffect {

//    private static final EVENT_TYPE EVENT = EVENT_TYPE.UNIT_ATTACKS;
//    private static final EVENT_TYPE ALT_EVENT = EVENT_TYPE.UNIT_DEALS_COMBAT_DAMAGE;
//    private static final EVENT_TYPE ALT_EVENT2 = EVENT_TYPE.UNIT_DEALS_DAMAGE;
    private static final Condition CONDITIONS = ConditionMaster
            .getAttackModifierConditions();
    private static final Targeting TARGETING = new FixedTargeting(
            Ref.KEYS.EVENT_TARGET);

//    public AttackModifierEffect(Effect effects) {
//        super();
//        super(EVENT, CONDITIONS, initAttackModifierAbility(effects));

//    }

    private static Ability initAttackModifierAbility(Effect effects) {
        return new ActiveAbility(TARGETING, effects);
    }

}
