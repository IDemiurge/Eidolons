package eidolons.ability.effects.continuous.triggered.sub;

import eidolons.ability.effects.attachment.AddTriggerEffect;
import main.ability.Ability;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;

public class CooldownTriggerEffect extends AddTriggerEffect {

    public CooldownTriggerEffect(String event_type, Condition conditions, Ability ability) {
        super(event_type, conditions, ability);
    }

    @Override
    protected Trigger createTriggerObject() {
        Trigger trigger = super.createTriggerObject();
        trigger.setCooldownCheck(()-> checkCD());
        trigger.setAfterTriggeredCallback(()-> triggered());
        return trigger;
    }

    private Boolean checkCD() {
       return  getActiveObj().getCooldown()==0;
    }

    private void triggered() {
         getActiveObj().cooldownActivated();
    }

}
