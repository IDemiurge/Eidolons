package main.ability.effects.oneshot.trigger;

import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.ability.effects.attachment.AddTriggerEffect;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.oneshot.DealDamageEffect;
import main.ability.effects.oneshot.misc.AlteringEffect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.conditions.standard.GroupCondition;
import main.elements.conditions.standard.NonTriggeredEventCondition;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

public class BindingDamageEffect extends MicroEffect implements OneshotEffect {
    Boolean shareOrRedirect;
    Boolean spellDmgOnly;
    Boolean physicalDmgOnly;
    DAMAGE_TYPE dmg_type = GenericEnums.DAMAGE_TYPE.PURE;
    private Conditions conditions;

    public BindingDamageEffect(Boolean shareOrRedirect, Formula formula,
                               Boolean spellDmgOnly, Boolean physicalDmgOnly) {
        this.formula = formula;
        this.shareOrRedirect = shareOrRedirect;
        this.spellDmgOnly = spellDmgOnly;
        this.physicalDmgOnly = physicalDmgOnly;
        this.setIgnoreGroupTargeting(true); // ???
    }

    @Override
    public boolean applyThis() {
        // Can be initialized() just once
        GroupImpl group = ref.getGroup();
        Effects effects = new Effects();
        STANDARD_EVENT_TYPE event_type;
        if (shareOrRedirect) {
            // TODO splitMode!
            event_type = STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE;
            if (spellDmgOnly != null) {
                if (spellDmgOnly) {
                    event_type = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE;
                }
            }
            if (physicalDmgOnly != null) {
                if (physicalDmgOnly) {
                    event_type = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE;
                }
            }
        } else {
            effects.add(new AlteringEffect(false, formula.getNegative()
             .toString()));
            event_type = Event.STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_DAMAGE;
            if (spellDmgOnly != null) {
                if (spellDmgOnly) {
                    event_type = STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_SPELL_DAMAGE;
                }
            }
            if (physicalDmgOnly != null) {
                if (physicalDmgOnly) {
                    event_type = STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE;
                }
            }
        }

        Targeting targeting_other_units = new AutoTargeting(new Conditions(
         new GroupCondition(Ref.KEYS.MATCH.name(), group),
         new RefCondition(KEYS.EVENT_TARGET, KEYS.MATCH, true)));// negative
        effects.add(new CustomTargetEffect(targeting_other_units,
         new DealDamageEffect(getDamageFormula(), GenericEnums.DAMAGE_TYPE.PURE)));

		/*
         * ensure there is no deadlock
		 */
        conditions = new Conditions();
        conditions.add(new NonTriggeredEventCondition());

        KEYS OBJ_REF = Ref.KEYS.EVENT_TARGET;
        conditions.add(new GroupCondition(OBJ_REF, group));
        Ref REF = Ref.getCopy(ref); // has the group...
        // REF.setTarget(null); // ???
        new AddTriggerEffect(event_type, conditions, OBJ_REF, effects)
         .apply(REF);

        return true;
    }

    private Formula getDamageFormula() {
        // TODO Auto-generated method stub
        return new Formula("{EVENT_AMOUNT}* "
         + StringMaster.wrapInParenthesis(formula.toString()));
    }
}
