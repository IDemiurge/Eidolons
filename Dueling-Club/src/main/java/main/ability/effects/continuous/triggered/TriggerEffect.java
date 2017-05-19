package main.ability.effects.continuous.triggered;

import main.ability.ActiveAbility;
import main.ability.effects.AttachmentEffect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.ability.effects.TriggeredEffect;
import main.ability.effects.attachment.AddTriggerEffect;
import main.elements.conditions.Condition;
import main.elements.targeting.Targeting;

public abstract class TriggerEffect extends MicroEffect implements
        AttachmentEffect, TriggeredEffect {

    protected boolean initialized = false;
    protected AddTriggerEffect triggerEffect;
    protected Targeting targeting;
    protected Effects effects;
    protected Condition conditions;
    protected String event_type;

    protected TriggerEffect() {

    }

    @Override
    public boolean applyThis() {
        if (!initialized) {
            init();
            initialized = true;
        }
        ActiveAbility ability = new ActiveAbility(targeting, effects);
        triggerEffect = new AddTriggerEffect(event_type, conditions, ability);
        return triggerEffect.apply(ref);
    }

    protected void init() {
        initEventType();
        initConditions();
        initTargeting();
        initEffects();
    }

    protected abstract void initEventType();

    protected abstract void initEffects();

    protected abstract void initTargeting();

    protected abstract void initConditions();

}
