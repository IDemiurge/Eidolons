package main.elements.triggers;

import main.ability.Ability;
import main.data.xml.XML_Converter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.game.core.game.Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;

public class Trigger {
    protected Condition conditions;
    protected Integer basis;
    protected EVENT_TYPE eventType;
    protected Ability abilities;
    protected Runnable callback;
    protected boolean replacing = false;
    protected boolean altering = false;
    protected boolean saving = false;
    protected boolean removeAfterTriggers = false;
    protected boolean forceTargeting = true;
    protected Condition retainCondition;
    protected Game game;
    protected Event event;

    public Trigger(EVENT_TYPE eventType, Condition conditions, Ability abilities) {
        this(eventType, conditions, abilities, abilities.getRef().getGame(),
         abilities.getRef().getTarget());
    }

    public Trigger(EVENT_TYPE eventType, Condition conditions ) {
        this(eventType, conditions,null, Game.game, null);
    }
    public Trigger( EVENT_TYPE eventType, Condition conditions, Ability abilities, Game game, Integer basis ) {
        this.conditions = conditions;
        this.basis = basis;
        this.eventType = eventType;
        this.abilities = abilities;
        this.game = game;

        if (abilities != null) {
        if (abilities.getEffects() != null)
            abilities.getEffects().setTrigger(this);
        }
    }

    @Override
    public String toString() {
        if (abilities == null) {
            return "Custom Trigger";
        }
        return "Trigger: " + abilities.getEffects().toString() + " on " + eventType.toString();
    }

    public boolean trigger() {
//        if (LogMaster.TRIGGER_DEBUG_ON)
            LogMaster.log( LOG_CHANNEL.TRIGGER_DEBUG, toString()
             + " has been triggered!");
        if (callback!=null )
        {
            callback.run();
        }
        if (abilities==null )
            return true;
        abilities.setForceTargeting(forceTargeting);

        Ref REF = getRef(event).getCopy();
        REF.setEvent(event);
        boolean result = abilities.activatedOn(REF);
        if (result && game.isStarted()) {
            // if (
            game.getManager().checkForChanges(true);
            // game.getManager().refreshAll();
        } if (isRemoveAfterTriggers(result)) {
            remove();
        }
        return result;

    }

    public boolean check(Event event) {

        Ref ref = getRef(event);
        ref.setEvent(event);
        if (retainCondition != null) {
            if (!retainCondition.preCheck(ref)) {
                remove();
                return false;
            }
        }
        if (!getEventType().equals(event.getType())) {
            return false;
        }
        LogMaster.log(LogMaster.CORE_DEBUG, "checking trigger for event: "
         + event.getType().name());
        // return true;
        if (eventType.equals((event.getType()))) {
            ref.setEvent(event);
            if (conditions == null) {
                this.event = event;
                return trigger();
            }
            ref.getGame().getManager().setTriggerBeingChecked(true);
            try {
                if (conditions.preCheck(ref)) {
                    this.event = event;
                    ref.getGame().getManager().setTriggerBeingActivated(true);
                    return trigger();
                } else {
                    return false;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return false;
            } finally {
                ref.getGame().getManager().setTriggerBeingChecked(false);
                ref.getGame().getManager().setTriggerBeingActivated(false);
            }

        } else {
            return false;
        }
    }

    protected Ref getRef(Event event) {
        if (getAbilities()==null )
            return event.getRef();
        return getAbilities().getRef();
    }


    // public void addCondition(Condition condition) {
    // conditions.add(condition);
    // }

    public EVENT_TYPE getEventType() {
        return eventType;
    }


    // @Override
    // public Integer getBasis() {
    // return basis;
    // }

    public boolean isAltering() {
        return altering;
    }

    public void setAltering(boolean altering) {
        this.altering = altering;
    }

    public boolean isReplacing() {
        return replacing;
    }

    // @Override
    // public void remove() {
    // ref.getGame().getState().removeAttachment(this);
    //
    // }

    public boolean isSaving() {
        return saving;
    }

    public void setSaving(boolean s) {
        saving = s;
    }

    protected void remove() {
        game.getState().removeTrigger(this);
    }

    public boolean isRemoveAfterTriggers(boolean result) {
        return removeAfterTriggers;
    }

    public void setRemoveAfterTriggers(boolean b) {
        this.removeAfterTriggers = b;
    }

    public synchronized Condition getConditions() {
        return conditions;
    }

    public synchronized Integer getBasis() {
        return basis;
    }

    public synchronized Ability getAbilities() {
        return abilities;
    }

    public synchronized boolean isForceTargeting() {
        return forceTargeting;
    }

    public synchronized void setForceTargeting(boolean forceTargeting) {
        this.forceTargeting = forceTargeting;
    }

    public void setRetainCondition(Condition retainCondition) {
        this.retainCondition = retainCondition;

    }

    public boolean isRemoveOnReset() {
        return true;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public String toXml() {
        StringBuilder builder = new StringBuilder(120);
        return builder.append(XML_Converter.openXml("Trigger")).
         append(XML_Converter.wrap("STANDARD_EVENT_TYPE",
          getEventType().toString())).
         append(XML_Converter.wrap("Conditions", getConditions() == null ? "" : getConditions().toXml())).
         append(XML_Converter.wrap("Abilities", getAbilities().toXml())).
         append(XML_Converter.closeXml("Trigger")).toString();
    }
}
