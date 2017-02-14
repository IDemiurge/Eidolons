package main.rules.old;

import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect;
import main.ability.effects.RemoveBuffEffect;
import main.ability.effects.oneshot.common.ConditionalEffect;
import main.ability.effects.oneshot.common.IfElseEffect;
import main.ability.effects.oneshot.common.OwnershipChangeEffect;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.StringComparison;
import main.elements.conditions.standard.OwnershipCondition;
import main.entity.Ref.KEYS;
import main.game.MicroGame;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.rules.DC_RuleImpl;

/**
 * Manage ownership
 *
 * @author JustMe
 */
public class ClaimRule extends DC_RuleImpl {
    private static final String CLAIM_COUNTERS = "{EVENT_TARGET_CLAIM_COUNTERS}";
    private static final String thresholdFunction = "[AV(CLAIM_COUNTERS_THRESHOLD,EVENT_TARGET)]";
    private static final String buffName = "Claimed";
    NumericCondition nOfCounters = new NumericCondition(false, CLAIM_COUNTERS,
            thresholdFunction);
    private Condition counterCheck = new StringComparison(KEYS.STRING.name(),
            "CLAIM", false);

    public ClaimRule(MicroGame game) {
        super(game);
    }

    @Override
    public void initEffects() {

        Conditions conditions = new Conditions();
        conditions.add(new NumericCondition("0", CLAIM_COUNTERS));
        Conditions conditions2 = new Conditions();
        conditions2.add(new OwnershipCondition(KEYS.EVENT_TARGET.name(), true));

        conditions2.add(nOfCounters);
        RemoveBuffEffect removeBuffEffect = new RemoveBuffEffect(buffName);
        Effect effect = new OwnershipChangeEffect(false);
        AddBuffEffect addBuffEffect = new AddBuffEffect(new NumericCondition(
                "{BASIS_CLAIM_COUNTERS}", "0"), buffName, effect);

        effects = new IfElseEffect(removeBuffEffect, conditions,
                new ConditionalEffect(conditions2, addBuffEffect));
    }

    @Override
    public void initConditions() {
        conditions = new Conditions();
        // conditions.add(nOfCounters);

        conditions.add(counterCheck);

    }

    @Override
    public void initEventType() {
        event_type = STANDARD_EVENT_TYPE.COUNTER_MODIFIED;

    }

}
