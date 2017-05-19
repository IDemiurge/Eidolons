package main.ability.effects.oneshot.mechanic;

import main.ability.effects.Effect;
import main.ability.effects.attachment.AddBuffEffect;
import main.elements.conditions.Condition;
import main.system.math.Formula;

public class AddSpreadingBuffEffect extends AddBuffEffect {
    Formula period;
    Boolean restartDuration;
    AddBuffEffect effect;

    public AddSpreadingBuffEffect(Effect effect, String buffTypeName,
                                  Condition conditions, Formula period, Boolean restartDuration) {
        super(conditions, buffTypeName, effect);
        this.effect = new AddBuffEffect(conditions, buffTypeName, effect);
    }

    public AddSpreadingBuffEffect(Effect effect, String buffTypeName,
                                  Formula dur, Formula period, Boolean restartDuration) {
        super(buffTypeName, effect, dur);
    }

    @Override
    public boolean applyThis() {
        // TODO NEW_TURN => replicateBuffEffect (duration?)

//        new AddTriggerEffect(event_type, getRetainConditions(), ability).apply(ref);

        return super.applyThis();
    }

}
