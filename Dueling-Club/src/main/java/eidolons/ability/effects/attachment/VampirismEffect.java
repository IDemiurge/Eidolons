package eidolons.ability.effects.attachment;

import eidolons.ability.conditions.DamageTypeCondition;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import main.ability.ActiveAbility;
import main.ability.effects.AttachmentEffect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.math.Formula;

public class VampirismEffect extends MicroEffect implements AttachmentEffect {

    Boolean restoreEndurance = true;
    Boolean restoreToughness = false;
    Boolean restoreToughnessAboveBase = false;
    Boolean restoreEnduranceAboveBase = true;
    Boolean fromEnduranceDamage = true;
    Boolean fromToughnessDamage = false;
    Boolean physical = true;
    Boolean magical = false;
    Boolean spell = false;
    private MOD code;
    private Conditions conditions;

    public VampirismEffect(Formula formula) {
        this.formula = new Formula("{EVENT_AMOUNT}*(" + formula + ")/100");
        this.code = MOD.MODIFY_BY_CONST;
    }

    public VampirismEffect(Formula formula, String damage_mods) {
        this(formula);
    }

    public VampirismEffect(Formula formula, Boolean spell) {
        this(formula);
    }

    private void initConditions() {
        conditions = new Conditions(new RefCondition(KEYS.EVENT_SOURCE,
         KEYS.SOURCE, false));
        if (!(physical && magical)) {
            conditions.add(new DamageTypeCondition(physical));
        }
    }

    @Override
    public boolean applyThis() {
        if (conditions == null) {
            initConditions();
        }
        // TODO Auto-generated method stub
        // endurance damage; modify endurance only? above base?
        // if (triggerEffect!=null) triggerEffect.apply(ref); else {
        Targeting targeting = new FixedTargeting(KEYS.SOURCE);
        Effects effects = new Effects();

        if (restoreEndurance) {
            ModifyValueEffect effect = new ModifyValueEffect(
             PARAMS.C_ENDURANCE, code, formula.toString());
            if (!restoreEnduranceAboveBase) {
                effect.setMaxParam(PARAMS.ENDURANCE);
            }
            effects.add(effect);
        }
        if (restoreToughness) {
            ModifyValueEffect effect = new ModifyValueEffect(
             PARAMS.C_TOUGHNESS, code, formula.toString());
            if (!restoreToughnessAboveBase) {
                effect.setMaxParam(PARAMS.TOUGHNESS);
            }
            effects.add(effect);
        }

        if (fromEnduranceDamage) {
            ActiveAbility ability = new ActiveAbility(targeting, effects);
            new AddTriggerEffect(
             STANDARD_EVENT_TYPE.UNIT_IS_DEALT_ENDURANCE_DAMAGE, // TODO
             // actual
             // damage?
             conditions, ability).apply(ref);
        }
        if (fromToughnessDamage) {

        }

        return true;
    }

    @Override
    public void setRetainCondition(Condition c) {

    }
}
