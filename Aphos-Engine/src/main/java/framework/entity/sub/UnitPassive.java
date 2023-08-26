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
 * Created by Alexander on 8/21/2023
 * Qualities are perhaps not part of this class?
 * LARGE for example...
 */
public class UnitPassive extends UnitSubEntity {
    EntityTypes.PassiveType type;
    // TriggerEvent event; //from templates (w/ param?) to real Events + base conditions
    // ConditionalEffects effects; //smart branching by condition
}
