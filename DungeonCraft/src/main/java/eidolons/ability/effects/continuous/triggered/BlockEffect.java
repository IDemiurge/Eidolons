package eidolons.ability.effects.continuous.triggered;

import eidolons.ability.effects.attachment.AddTriggerEffect;
import main.ability.ActiveAbility;
import main.ability.effects.*;
import main.ability.effects.triggered.InterruptEffect;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class BlockEffect extends MicroEffect implements AttachmentEffect, TriggeredEffect {
    protected STANDARD_EVENT_TYPE event_type;
    protected BLOCK_TYPES BLOCK_TYPE;
    protected Conditions conditions = new Conditions();
    protected Effect effects;

    public BlockEffect(BLOCK_TYPES BLOCK_TYPE) {
        this.BLOCK_TYPE = BLOCK_TYPE;

    }

    @Override
    public boolean applyThis() {
        String OBJ_REF = null; // effect interrupt
        switch (BLOCK_TYPE) {
            case ATTACK:
                event_type = Event.STANDARD_EVENT_TYPE.UNIT_IS_BEING_ATTACKED;
                conditions.add(new RefCondition(KEYS.EVENT_TARGET, KEYS.SOURCE,
                 false));
                break;
            case HOSTILE_ACTION:
                event_type = Event.STANDARD_EVENT_TYPE.HOSTILE_ACTION;
                conditions.add(new RefCondition(KEYS.EVENT_TARGET, KEYS.SOURCE,
                 false));
                break;
            case DAMAGE:
                event_type = Event.STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_TOUGHNESS_DAMAGE;

                break;
            case HOSTILE_SPELLS:
                event_type = Event.STANDARD_EVENT_TYPE.SPELL_BEING_RESOLVED;
                conditions.add(new RefCondition(KEYS.EVENT_TARGET, KEYS.TARGET,
                 false));
                OBJ_REF = Ref.KEYS.SPELL.name();
                break;
            default:
                break;

        }

        effects = new Effects(new InterruptEffect(OBJ_REF));
        ActiveAbility abilities = new ActiveAbility(new FixedTargeting(
         KEYS.SOURCE), effects);
        abilities.setRef(ref);

        new AddTriggerEffect(event_type, conditions, abilities).apply(ref);

        return true;
    }

    @Override
    public void setRetainCondition(Condition c) {
        // TODO Auto-generated method stub

    }
}
