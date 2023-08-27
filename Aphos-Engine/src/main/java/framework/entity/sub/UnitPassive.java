package framework.entity.sub;

import elements.content.enums.EnumFinder;
import elements.content.enums.types.EntityTypes;
import elements.exec.EntityRef;
import elements.exec.ExecBuilder;
import elements.exec.Executable;
import elements.exec.condition.Condition;
import elements.exec.condition.ConditionBuilder;
import elements.exec.targeting.TargetingTemplates;
import elements.exec.trigger.PassiveTrigger;
import framework.data.DataManager;
import framework.entity.field.Unit;
import logic.execution.event.combat.CombatEventType;

import java.util.Map;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/21/2023 Qualities are perhaps not part of this class? LARGE for example...
 */
public class UnitPassive extends UnitSubEntity {
    EntityTypes.PassiveType type;
    Condition activationCondition;

    public UnitPassive(Map<String, Object> valueMap, Unit unit) {
        super(valueMap, unit);
    }

    public EntityTypes.PassiveType getType() {
        if (type == null) {
            type = getEnum("Passive_type", EntityTypes.PassiveType.class);
        }
        return type;
    }

    public void apply() {
        //on each reset!
        //qualities are in fact just ... props! maybe effects can grant them too... conditional qualities?
        if (data.has("exec_data")) {
            EntityRef ref = new EntityRef(unit);
            ref.setMatch(unit);
            if (activationCondition.check(ref)) {
                //are execs free of side-effects, can they be cached?
                Executable exec = ExecBuilder.getExecutable(getS("exec_data"));
                TargetingTemplates.ConditionTemplate tmplt = null;
                Condition condition = ConditionBuilder.build(tmplt, DataManager.deconstructDataString(getS("condition_args")));
                PassiveTrigger trigger = new PassiveTrigger(condition, exec);
                ref.setMatch(null);
                ref.setTarget(unit);
                trigger.setTargetRef(ref);
                CombatEventType event_type = EnumFinder.get(CombatEventType.class, getS("event_type"));
                //so ... aren't these supposed to be in Exec's DATA? Maybe not.
                combat().getEventHandler().addTrigger(trigger, event_type);
            }
        }

        //we don't need to use addTriggerFx, or? yes, apply directly a PassiveTrigger
    }
    // TriggerEvent event; //from templates (w/ param?) to real Events + base conditions
    // ConditionalEffects effects; //smart branching by condition
}
