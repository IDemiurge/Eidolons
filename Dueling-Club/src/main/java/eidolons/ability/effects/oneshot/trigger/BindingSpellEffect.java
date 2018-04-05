package eidolons.ability.effects.oneshot.trigger;

import eidolons.ability.effects.DC_Effect;
import eidolons.ability.effects.continuous.triggered.DuplicateEffect;
import eidolons.ability.effects.continuous.triggered.DuplicateSpellEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.triggered.InterruptEffect;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.system.entity.ConditionMaster;

public class BindingSpellEffect extends DC_Effect {
    Boolean shareOrRedirect;
    private Conditions conditions;
    private BIND_TYPE type;

    public BindingSpellEffect(BIND_TYPE type, Condition c) {
        this.conditions = new Conditions(c);
    }

    @Override
    public boolean applyThis() {
        // TODO Auto-generated method stub


        Effects effects = null;
        if (!shareOrRedirect) {
            effects = new Effects(new CustomTargetEffect(new FixedTargeting(
             KEYS.TARGET2), new DuplicateEffect(true)),
             new CustomTargetEffect(new FixedTargeting(KEYS.TARGET),
              new InterruptEffect()));
        }

        Effect EFFECT = new DuplicateSpellEffect(KEYS.TARGET.name(), false,
         true);
        EFFECT.setTargetGroup(ref.getGroup());
        effects = new Effects(EFFECT);
        Event.STANDARD_EVENT_TYPE event_type = Event.STANDARD_EVENT_TYPE.SPELL_RESOLVED;
        conditions.add(ConditionMaster.getPropCondition("EVENT_SPELL",
         G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.MIND_AFFECTING.name()));
        return false;
    }
}
