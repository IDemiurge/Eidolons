package main.ability.effects;

import main.ability.ActiveAbility;
import main.ability.effects.oneshot.common.AddTriggerEffect;
import main.elements.conditions.Condition;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref.KEYS;
import main.game.event.Event.STANDARD_EVENT_TYPE;

public class DelayedEffect extends AddTriggerEffect {

    private static final STANDARD_EVENT_TYPE EVENT_TYPE = STANDARD_EVENT_TYPE.ROUND_ENDS;
    private static final STANDARD_EVENT_TYPE EVENT_TYPE_CHANNELING = STANDARD_EVENT_TYPE.UNIT_TURN_STARTED;
    private static final Targeting T = new FixedTargeting(KEYS.TARGET);

    public DelayedEffect(Effect effect, Condition c) {
        this(EVENT_TYPE, effect, c);
    }

    /**
     * for Sorcery spells?
     */
    // public DelayedEffect(Effect effect, Condition c) {
    // this(EVENT_TYPE, effect, c);
    // }
    public DelayedEffect(STANDARD_EVENT_TYPE event_type, Effect effect,
                         Condition c) {
        super(event_type, c, new ActiveAbility(T, effect));

    }

    @Override
    public boolean applyThis() {
        boolean result = super.applyThis();
        trigger.setOneShot(true);
        return result;
    }
}
