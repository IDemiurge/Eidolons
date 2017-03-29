package main.elements.triggers;

import main.ability.Ability;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.game.core.game.Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.system.auxiliary.log.LogMaster;

import java.util.List;

public class Trigger {
    protected Condition conditions;
    protected Integer basis;
    protected List<EVENT_TYPE> eventTypes;
    protected EVENT_TYPE eventType;
    protected Ability abilities;
    protected boolean replacing = false;
    protected boolean altering = false;
    private boolean saving = false;
    private boolean removeAfterTriggers = false;
    private boolean forceTargeting = true;
    private Condition retainCondition;
    private Game game;
    private Event event;

    public Trigger(EVENT_TYPE eventType, Condition conditions, Ability abilities) {
        // construct abilities?
        this.conditions = conditions;
        this.eventType = eventType;
        this.abilities = abilities;
        game = abilities.getRef().getGame();
        abilities.getEffects().setTrigger(this);
basis =abilities.getRef().getTarget();
    }

    @Override
    public String toString() {
        return "Trigger: " + abilities.getEffects().toString() + " on " + eventType.toString();
    }

    public boolean trigger() {
        LogMaster.log(LogMaster.TRIGGER_DEBUG, toString()
         + " has been triggered!");
        abilities.setForceTargeting(forceTargeting);
        if (removeAfterTriggers) {
            remove();
        }
        Ref REF = abilities.getRef().getCopy();
        REF.setEvent(event);
        boolean result = abilities.activatedOn(REF);
        if (result && game.isStarted()) {
            // if (
            game.getManager().checkForChanges(true);
            // game.getManager().refreshAll();
        }
        return result;

    }

    public boolean check(Event event) {

        Ref ref = getAbilities().getRef();
        ref.setEvent(event);
        if (retainCondition != null) {
            if (!retainCondition.check(ref)) {
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
                return trigger();
            }
            ref.getGame().getManager().setTriggerBeingChecked(true);
            try {
                if (conditions.check(ref)) {
                    this.event=event;
                    ref.getGame().getManager().setTriggerBeingActivated(true);
                    return trigger();
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                ref.getGame().getManager().setTriggerBeingChecked(false);
                ref.getGame().getManager().setTriggerBeingActivated(false);
            }

        } else {
            return false;
        }
    }

    public List<EVENT_TYPE> getEventTypes() {
        return eventTypes;
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

    private void remove() {
        game.getState().removeTrigger(this);
    }

    public boolean isRemoveAfterTriggers() {
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

}
