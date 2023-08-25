package logic.rules;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import logic.execution.event.combat.CombatEventType;

/**
 * Created by Alexander on 8/22/2023
 */
public abstract class TriggerRule {

    public void initTrigger(){
        // Trigger trigger = new Trigger(getCondition(), ref -> execute(ref));
        // trigger.setName(getName());
        // combat().getEventHandler().addTrigger(trigger, getEventType());
    }

    protected abstract Condition getCondition();

    protected abstract boolean execute(EntityRef ref);

    protected abstract CombatEventType getEventType();
}
