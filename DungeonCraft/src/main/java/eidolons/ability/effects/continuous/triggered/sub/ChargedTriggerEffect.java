package eidolons.ability.effects.continuous.triggered.sub;

import eidolons.ability.effects.attachment.AddTriggerEffect;
import eidolons.ability.effects.continuous.triggered.TriggerEffect;
import main.ability.Ability;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;

public class ChargedTriggerEffect extends AddTriggerEffect {

    public ChargedTriggerEffect(String event_type, Condition conditions, Ability ability) {
        super(event_type, conditions, ability);
    }

    @Override
    protected Trigger createTriggerObject() {
        Trigger trigger = super.createTriggerObject();
        trigger.setCharges(()-> getCharges());
        trigger.setAfterTriggeredCallback(()-> chargeUsed());
        return trigger;
    }

    private void chargeUsed() {
         getActiveObj().chargeUsed();
    }

    private Integer getCharges() {
        return getActiveObj().getCharges();
    }
}
