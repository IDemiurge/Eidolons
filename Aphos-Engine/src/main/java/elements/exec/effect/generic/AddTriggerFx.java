package elements.exec.effect.generic;

import elements.content.enums.EnumFinder;
import elements.exec.EntityRef;
import elements.exec.ExecBuilder;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;
import elements.exec.trigger.ExecTrigger;
import framework.data.TypeData;
import logic.execution.event.combat.CombatEventType;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/24/2023
 */
public class AddTriggerFx extends Effect {
    private CombatEventType eventType;
    private ExecTrigger trigger;

    @Override
    public void setData(TypeData data) {
        super.setData(data);
        eventType = EnumFinder.get(CombatEventType.class, data.get("event_type"));
        //TODO
        // trigger = new Trigger(ExecBuilder.initExecutable());
    }

    public AddTriggerFx(CombatEventType eventType, Condition condition, String execData) {
        this.eventType = eventType;
        this.trigger = new ExecTrigger(condition, ExecBuilder.getOrCreateExecutable(execData));
    }


    @Override
    protected void applyThis(EntityRef ref) {
        //clone trigger?!
        //continuous addTriggerEffect does not make much sense now, does it?
        trigger.setTargetRef(ref);
        combat().getEventHandler().addTrigger(trigger, eventType);
    }
}
