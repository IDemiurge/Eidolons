package eidolons.ability.effects.containers.customtarget;

import eidolons.ability.effects.DC_Effect;
import eidolons.ability.effects.attachment.AddTriggerEffect;
import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.effects.Effect;
import main.content.C_OBJ_TYPE;
import main.elements.conditions.*;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;

public class ActiveAuraEffect extends DC_Effect {

    Formula radius;
    private String event_type = STANDARD_EVENT_TYPE.NEW_ROUND.toString();
    private Condition conditions;
    private Boolean onlyEnemiesOrAllies;
    private Effect effects;

    public ActiveAuraEffect() {

    }

    @Override
    public boolean applyThis() {
        AutoTargeting targeting = new AutoTargeting(new Conditions(
         new ObjTypeComparison(C_OBJ_TYPE.BF_OBJ),
         new DistanceCondition("" + radius.getInt(ref))));
        targeting.getConditions().add(
         ConditionMaster.getAliveCondition(KEYS.MATCH));
        if (onlyEnemiesOrAllies != null) {
            if (onlyEnemiesOrAllies) {
                targeting.getConditions().add(
                 ConditionMaster.getEnemyCondition());
            } else {
                targeting.getConditions().add(
                 ConditionMaster.getAllyCondition());
            }

        }
        targeting.getConditions().add(
         new NotCondition(new RefCondition(KEYS.MATCH, KEYS.SOURCE)));
        Ability ability = new ActiveAbility(targeting, effects);

        new AddTriggerEffect(event_type, conditions, ability).apply(ref);

        return true;
    }

}
