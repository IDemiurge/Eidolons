package eidolons.ability.ignored.oneshot.rule;

import eidolons.ability.conditions.WaitingRetainCondition;
import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.attachment.AddTriggerEffect;
import eidolons.ability.effects.continuous.SetCustomModeEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.AlertRule;
import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.enums.entity.UnitEnums;
import main.content.mode.STD_MODES;
import main.elements.conditions.Condition;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class WaitEffect extends MicroEffect implements OneshotEffect {

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
            AlertRule.addWaitingUnit((Unit) ref.getSourceObj(), (Unit) ref
             .getTargetObj());
        } catch (Exception e) {
            return false;
        }
        return result;

    }

    protected String getBuffName() {
        return AlertRule.WAIT_BUFF;
    }

    protected Effect getEffects(Ref ref) {
        return new Effects(new SetCustomModeEffect(STD_MODES.WAITING), new AddTriggerEffect(
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
