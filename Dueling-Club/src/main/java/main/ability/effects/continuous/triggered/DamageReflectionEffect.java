package main.ability.effects.continuous.triggered;

import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.math.Formula;

public class DamageReflectionEffect extends TriggerEffect {

    Boolean restoreEndurance = true;
    Boolean restoreToughness = false;
    Boolean restoreToughnessAboveBase = false;
    Boolean restoreEnduranceAboveBase = true;
    Boolean fromEnduranceDamage = true;
    Boolean fromToughnessDamage = false;
    private MOD code;

    public DamageReflectionEffect(Formula formula) {
        super();
        this.formula = new Formula("{EVENT_AMOUNT}*" + formula + "/100");
        this.code = MOD.MODIFY_BY_CONST;
    }

    @Override
    protected void initEffects() {
        effects = new Effects();

        if (restoreEndurance) {
            effects.add(new ModifyValueEffect(PARAMS.C_ENDURANCE, code,
                    formula.toString(), PARAMS.ENDURANCE));
        }
        if (restoreToughness) {
            effects.add(new ModifyValueEffect(PARAMS.C_TOUGHNESS, code,
                    formula.toString()));
        }

    }

    @Override
    protected void initTargeting() {
        targeting = new FixedTargeting(KEYS.SOURCE);

    }

    @Override
    protected void initConditions() {
        conditions = new RefCondition(KEYS.EVENT_TARGET, KEYS.SOURCE, false);
    }

    @Override
    protected void initEventType() {
        if (fromEnduranceDamage) {
            event_type = STANDARD_EVENT_TYPE.UNIT_IS_DEALT_ENDURANCE_DAMAGE.name();
        }
        if (fromToughnessDamage) {

        }
    }

    @Override
    public void setRetainCondition(Condition c) {

    }
}
