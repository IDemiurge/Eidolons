package main.ability.effects.common;

import main.ability.ActiveAbility;
import main.ability.conditions.DamageTypeCondition;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.MicroEffect;
import main.ability.effects.oneshot.common.AddTriggerEffect;
import main.ability.effects.oneshot.common.AttachmentEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.math.Formula;

public class DamageInversionEffect extends MicroEffect implements
        AttachmentEffect {

    Boolean restoreEndurance = true;
    Boolean restoreToughness = false;
    Boolean restoreToughnessAboveBase = false;
    Boolean restoreEnduranceAboveBase = true;
    private Boolean spell;
    private Boolean physical;

    private MOD code;
    private Conditions conditions = new Conditions(new RefCondition(
            KEYS.EVENT_TARGET, KEYS.SOURCE, false));

    private STANDARD_EVENT_TYPE event_type;

    // public DamageInversionEffect( condition) {

    public DamageInversionEffect(Boolean physical, Boolean spell,
                                 Formula formula) {
        this.spell = spell;
        this.physical = physical;

        conditions.add(new DamageTypeCondition(physical));
        event_type = (physical) ? STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE
                : STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_SPELL_DAMAGE;

        this.formula = new Formula("{EVENT_AMOUNT}*" + formula + "/100");
        this.code = MOD.MODIFY_BY_CONST;
    }

    @Override
    public boolean applyThis() {

        // endurance damage; modify endurance only? above base?
        Targeting targeting = new FixedTargeting(KEYS.SOURCE);
        Effects effects = new Effects();

        if (restoreEndurance) {
            ModifyValueEffect effect = new ModifyValueEffect(PARAMS.C_ENDURANCE, code, formula.toString());
            if (!restoreEnduranceAboveBase) {
                effect.setMaxParam(PARAMS.ENDURANCE);
            }
            effects.add(effect);
        }
        if (restoreToughness) {
            ModifyValueEffect effect = new ModifyValueEffect(PARAMS.C_TOUGHNESS, code, formula.toString());
            if (!restoreToughnessAboveBase) {
                effect.setMaxParam(PARAMS.TOUGHNESS);
            }
            effects.add(effect);
        }

        ActiveAbility ability = new ActiveAbility(targeting, effects);
        new AddTriggerEffect(event_type, conditions, ability).apply(ref);

        return true;
    }

    @Override
    public void setRetainCondition(Condition c) {
        // TODO Auto-generated method stub

    }
}
