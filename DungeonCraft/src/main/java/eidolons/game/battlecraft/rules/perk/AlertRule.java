package eidolons.game.battlecraft.rules.perk;

import eidolons.ability.effects.containers.LoggedEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.ability.effects.oneshot.mechanic.DelayedEffect;
import main.ability.effects.Effect;
import main.content.mode.STD_MODES;
import main.elements.conditions.*;
import main.elements.conditions.standard.GroupCondition;
import main.elements.conditions.standard.ListSizeCondition;
import main.elements.conditions.standard.OwnershipCondition;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.entity.ConditionMaster;

public class AlertRule {


    public static Condition getWakeUpConditions() {
        // TODO will be checked with a ref from UNIT_ACTION_COMPLETE event!
        Conditions conditions = new OrConditions();

        // adjacent enemy condition - the problem is that it will then be
        // impossible to be *alert* if there are enemies adjacent already... or
        // is it a good design?

        // action was targeting *source* - directly or as one of the targets -
        // the problem is that it won't work for allies which could be exploited
        // largely

        // all in all, Alert Mode is probably not for *complicated situations*,
        // it's for the simple ones when you just wait for the fight
        // patiently...

        // new PropCondition(prop, str2)

        conditions.add(new Conditions(new NotCondition(new OwnershipCondition(KEYS.SOURCE,
         KEYS.EVENT_SOURCE)), new OrConditions(
         // "Active_Target"

         new RefCondition(KEYS.EVENT_TARGET, KEYS.SOURCE), new GroupCondition(KEYS.SOURCE
         .toString(), true))));

        conditions.add(new ListSizeCondition(true, new Conditions(ConditionMaster
         .getAliveAndConsciousFilterCondition(), ConditionMaster.getEnemyCondition(),
         ConditionMaster.getAdjacentCondition())

         , "1"));

        return conditions;

    }

    public static Effect getWakeUpTriggerEffect() {
        return
                new DelayedEffect(STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE,
                        new LoggedEffect("{source_name} wakes up from Alert Mode!",
                                new RemoveBuffEffect(
         STD_MODES.ALERT.getBuffName())), AlertRule.getWakeUpConditions());
    }

}
