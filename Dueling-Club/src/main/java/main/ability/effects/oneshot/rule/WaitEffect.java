package main.ability.effects.oneshot.rule;

import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.conditions.WaitingRetainCondition;
import main.ability.effects.OneshotEffect;
import main.ability.effects.attachment.AddBuffEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.buff.RemoveBuffEffect;
import main.ability.effects.MicroEffect;
import main.ability.effects.attachment.AddTriggerEffect;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.rules.mechanics.WaitRule;

public class WaitEffect extends MicroEffect implements OneshotEffect{

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() == ref.getSourceObj()) {
            ref.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_WAITS, game));
            return true;
        }
        Ref REF = Ref.getCopy(ref);
        REF.setValue(KEYS.TARGET2, ref.getTarget() + "");
        REF.setTarget(ref.getSource());

        boolean result = new AddBuffEffect(getRetainConditions(), getBuffName(), getEffects(REF))
                .apply(REF);
        if (!result) {
            return false;
        }

        try {
            WaitRule.addWaitingUnit((Unit) ref.getSourceObj(), (Unit) ref
                    .getTargetObj());
        } catch (Exception e) {
            return false;
        }
        return result;

    }

    protected String getBuffName() {
        return WaitRule.WAIT_BUFF;
    }

    protected Effect getEffects(Ref ref) {
        return new Effects(new AddStatusEffect(getStatus()), new AddTriggerEffect(
                STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE, getConditions(), getAbility(ref)));
    }

    protected Condition getConditions() {
        return new RefCondition(KEYS.EVENT_SOURCE,
         KEYS.TARGET2, false);
    }

    protected Ability getAbility(Ref ref) {
        Effect effect = new Effects(new RemoveBuffEffect(getBuffName()));
        Ability ability = new ActiveAbility(new FixedTargeting(KEYS.SOURCE), effect);
        ability.setRef(ref);
        return ability;
    }

    protected String getStatus() {
        return UnitEnums.STATUS.WAITING.toString();
    }

    protected Condition getRetainConditions() {
        return new WaitingRetainCondition();
    }
}
