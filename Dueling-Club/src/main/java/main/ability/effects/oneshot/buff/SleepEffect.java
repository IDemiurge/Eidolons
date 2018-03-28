package main.ability.effects.oneshot.buff;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effects;
import main.ability.effects.OneshotEffect;
import main.ability.effects.attachment.AddBuffEffect;
import main.ability.effects.attachment.AddTriggerEffect;
import main.ability.effects.common.AddStatusEffect;
import main.ability.effects.oneshot.mechanic.RollEffect;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.MetaEnums;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class SleepEffect extends DC_Effect implements OneshotEffect {

    private String buffName;

    public SleepEffect(String buffName) {
        this.buffName = buffName;
    }

    @Override
    public boolean applyThis() {
        // makes another throw each time when hit?
        // statically parsed Spellpower/Mastery?

        // add roll on hit - dmg vs max toughness
        RollEffect rollEffect = new RollEffect(GenericEnums.ROLL_TYPES.MIND_AFFECTING,
         new RemoveBuffEffect(getBuffName()));

        Conditions conditions = new Conditions(new RefCondition(
         KEYS.EVENT_TARGET, KEYS.TARGET)

        );
        return new AddBuffEffect(getBuffName(), new Effects(
         new AddStatusEffect(UnitEnums.STATUS.ASLEEP), new AddTriggerEffect(
         STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE,
         conditions, KEYS.EVENT_TARGET, rollEffect))).apply(ref);
        // roll ref needs to be tested!
    }

    private String getBuffName() {
        if (buffName != null) {
            return buffName;
        }
        return MetaEnums.STD_BUFF_NAMES.Asleep.name();
    }
}
