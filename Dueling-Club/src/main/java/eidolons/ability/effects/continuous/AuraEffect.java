package eidolons.ability.effects.continuous;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.attachment.AddTriggerEffect;
import eidolons.ability.effects.containers.customtarget.ZoneEffect;
import main.ability.ActiveAbility;
import main.ability.effects.AttachmentEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.ability.effects.container.ConditionalEffect;
import main.ability.effects.continuous.CustomTargetEffect;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.*;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;

//Untested
public class AuraEffect extends MicroEffect implements AttachmentEffect {
    STANDARD_EVENT_TYPE event_type; // ++ UNIT MOVES!!!!
    private final Effect effect;
    private final boolean initialized = false;
    private AddTriggerEffect trigger;
    private final boolean notSelf;
    private final Boolean onlyEnemiesOrAllies;
    private final Formula radius;
    private final Boolean continuous;
    private final Condition additionalConditions;
    private AddBuffEffect auraEffect;
    private boolean on;

	/*
     * let's reconsider this effect:
	 * 
	 * Immolation "coming in contact" estimates how? What's wrong with
	 * END_OF_TURN ?
	 * 
	 * 
	 * Aura of Fear/Virtue/... - a continuous effect that creates buffs (must be
	 * zero layer?) Isn't it just how Cadence rule works?
	 * 
	 * AddBuffEffect(new CustomTargetingEffect(adjacents, buff(effects));
	 */

    @AE_ConstrArgs(argNames = {"effect", "radius",})
    public AuraEffect(Effect effect, Formula radius) {
        this(true, effect, radius, null, false, null);
    }

    @AE_ConstrArgs(argNames = {"continuous", "effect", "radius", "onlyEnemiesOrAllies", "notSelf",
     "additionalConditions"})
    public AuraEffect(Boolean continuous, Effect effect, Formula radius,
                      Boolean onlyEnemiesOrAllies, Boolean notSelf, Condition additionalConditions) {
        this.continuous = continuous; // TODO ??? how to ensure that c_effects
        // never apply twice to a single target?
        this.effect = effect;
        this.radius = radius;
        this.onlyEnemiesOrAllies = onlyEnemiesOrAllies;
        this.notSelf = notSelf;
        this.additionalConditions = additionalConditions;
    }

    @AE_ConstrArgs(argNames = {"effect", "radius", "onlyEnemiesOrAllies"})
    public AuraEffect(Effect effect, Formula radius, Boolean onlyEnemiesOrAllies) {
        this(true, effect, radius, onlyEnemiesOrAllies, false, null);
    }

    @AE_ConstrArgs(argNames = {"effect",})
    public AuraEffect(Effect effect) {
        this(true, effect, new Formula("1"), null, false, null);
    }

    /*
     *
     * so what really happens?
     *
     * maybe it's OK if it's not Spirit?
     *
     * There aren't really non-ValueMod effect variants, are there?
     * "Damage Aura"? Property Aura, e.g. demonic! :)
     */
    public boolean applyThis() {
        if (!on) {
            return true;
        }
        if (game.isSimulation()) {
            return false;
        }
        if (continuous) {
            AutoTargeting targeting = new AutoTargeting(new DistanceCondition(radius.toString()));
            targeting.getConditions().add(ConditionMaster.getAliveCondition(KEYS.MATCH));

            if (onlyEnemiesOrAllies != null) {
                if (onlyEnemiesOrAllies) {
                    targeting.getConditions().add(ConditionMaster.getEnemyCondition());
                } else {
                    targeting.getConditions().add(ConditionMaster.getAllyCondition());
                }

            }
            //remove aura-bearer from targets list
            targeting.getConditions().add(
             new NotCondition(new RefCondition(KEYS.MATCH, KEYS.SOURCE)));
            AddBuffEffect buffEffect = new AddBuffEffect(getBuffType(), effect, true);
            Effects auraEffects = new Effects(
             new ConditionalEffect(ConditionMaster
              .getAliveCondition(KEYS.SOURCE),
              new CustomTargetEffect(targeting, buffEffect)));
            auraEffect = new AddBuffEffect(auraEffects);
            // auraEffect.setTransient(false);
            boolean results = auraEffect.apply(ref);
            if (results) {
                if (!notSelf) {
                    Effect copy = effect.getCopy();
                    copy.apply(Ref.getSelfTargetingRefCopy(ref.getSourceObj()));
                }
            }
            return results;
        }

        // use zoneEffect?
        // if only modifies non-dynamic values, then it's continuous. How to
        // preCheck?
        if (!initialized) {
            init();
        }
        return trigger.apply(ref);
    }

    private String getBuffType() {
        if (ref.getObj(KEYS.ABILITY) == null) {
            return null;
        }
        return ref.getObj(KEYS.ABILITY).getName();
    }

    private void init() {

        event_type = (continuous) ? STANDARD_EVENT_TYPE.UNIT_MOVED
         : STANDARD_EVENT_TYPE.UNIT_TURN_READY;

        Targeting t = new FixedTargeting(KEYS.SOURCE);

        Conditions conditions = new Conditions();
        if (additionalConditions != null) {
            conditions.add(additionalConditions);
        }
        if (!continuous) {
            conditions.add(ConditionMaster.getSelfTriggerCondition());

        } else {
            // Condition retainCondition = ConditionMaster
            // .getDistanceCondition("TARGET", "SOURCE", radius);
            // effect = new AddBuffEffect(retainCondition, null, effect);
            // conditions.add(ConditionMaster
            // .getDistanceCondition("EVENT_SOURCE", "SOURCE", radius));

        }

        ZoneEffect zone_effect = new ZoneEffect(effect, radius, onlyEnemiesOrAllies, notSelf);
        ActiveAbility ability = new ActiveAbility(t, zone_effect);

        trigger = new AddTriggerEffect(event_type, conditions, ability);
    }

    @Override
    public void setRetainCondition(Condition c) {
        // TODO Auto-generated method stub

    }
}
