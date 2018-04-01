package eidolons.ability.effects.attachment;

import eidolons.ability.ActivesConstructor;
import eidolons.ability.effects.continuous.triggered.TriggerEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.entity.ConditionMaster;

public class TrapEffect extends TriggerEffect {

    private String abilName;
    private boolean friendlyFire = true;

    public TrapEffect(String abilName) {
        this.abilName = abilName;
    }

    // ++ duration?
    public TrapEffect(Effect e, Boolean friendlyFire) {
        this.effects = new Effects(e);
        this.friendlyFire = friendlyFire;
    }

    @Override
    public boolean applyThis() {
        Effect effect = new AddStatusEffect(UnitEnums.STATUS.TRAPPED.name());
        new AddBuffEffect(effect).apply(ref);
        return super.applyThis();
    }

    @Override
    protected void initEffects() {
        if (effects == null) {
            Effect e = ActivesConstructor.getEffectsFromAbilityType(abilName);
            this.effects = new Effects(e);
        }
    }

    @Override
    protected void initTargeting() {
        targeting = new FixedTargeting(KEYS.EVENT_SOURCE);

    }

    @Override
    protected void initConditions() {
        conditions = new Conditions(new RefCondition(KEYS.EVENT_TARGET,
         KEYS.TARGET, false));
        if (!friendlyFire) {
            ((Conditions) conditions).add(ConditionMaster.getEnemyCondition());
        }
    }

    @Override
    protected void initEventType() {
        event_type = STANDARD_EVENT_TYPE.UNIT_MOVED.name();

    }

    @Override
    public void setRetainCondition(Condition c) {
        // TODO Auto-generated method stub

    }
}
