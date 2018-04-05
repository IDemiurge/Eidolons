package eidolons.ability.effects.attachment;

import eidolons.client.dc.Launcher;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingRunner;
import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.effects.AttachmentEffect;
import main.ability.effects.ContainerEffect;
import main.ability.effects.Effect;
import main.data.xml.XML_Converter;
import main.elements.conditions.Condition;
import main.elements.targeting.FixedTargeting;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import org.w3c.dom.Node;

public class AddTriggerEffect extends MultiEffect implements
 AttachmentEffect, ContainerEffect {
    // ONLY FOR PARAMS LIKE MANA, HP, STA, IN ... (CURRENT VALUES)
    protected EVENT_TYPE event_type;
    protected Ability ability;
    protected Condition conditions;
    private Condition retainCondition;

    public AddTriggerEffect(String event_type, Condition conditions, Ability ability) {
        this.conditions = conditions;
        this.event_type = Event.getEventType(event_type);
        this.ability = ability;
    }

    public AddTriggerEffect(STANDARD_EVENT_TYPE event_type, Condition conditions, Ability ability) {
        this.conditions = conditions;
        this.event_type = event_type;
        this.ability = ability;
    }

    public AddTriggerEffect(STANDARD_EVENT_TYPE event_type, Condition conditions, KEYS target,
                            Effect effect) {
        this(event_type, conditions, new ActiveAbility(new FixedTargeting(target), effect));
    }

    public AddTriggerEffect(String event_type, Condition conditions, String target, Effect effect) {
        this(Event.getEventType(event_type), conditions, new EnumMaster<KEYS>().retrieveEnumConst(
         KEYS.class, target), effect);
    }

    // continuous
    @Override
    public boolean applyThis() {
        if (!CoreEngine.isCombatGame()) {
            return true;
        }
        if (trigger == null) {
            trigger = new Trigger(event_type, conditions, ability);

            if (!Launcher.DEV_MODE)
                if (!AiTrainingRunner.running)
                    if (conditions != null)
                        if (conditions.toXml() == null)
                            if (!StringMaster.isEmpty(toXml()))
                                try {
                                    Node xml = XML_Converter.findNode(toXml(), "Conditions");
                                    if (xml != null)
                                        conditions.setXml(XML_Converter.getStringFromXML(xml));
                                } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
                                }
        }
        trigger.setRetainCondition(retainCondition);
        ref.getGame().getManager().addTrigger(trigger);
        return true;
    }

    public void remove() {
        if (getTrigger() != null) {
            ref.getGame().getState().removeTrigger(getTrigger());
        }

    }

    @Override
    public boolean isContinuousWrapped() {
        return true;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        if (ability != null) {
            ability.setRef(ref);
        }
    }

    @Override
    public void addEffect(Effect effect) {
        ability.addEffect(effect);
    }

    public Trigger getTrigger() {
        return trigger;
    }

    @Override
    public void setRetainCondition(Condition c) {
        this.retainCondition = c;

    }

    @Override
    public Effect getEffect() {
        // TODO Auto-generated method stub
        return effect;
    }

}
