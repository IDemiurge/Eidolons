package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.elements.ReferredElement;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.event.Event;

public class AbilityImpl extends ReferredElement implements Ability {

    protected Targeting targeting;
    protected Effects effects;

    private boolean interrupted;
    private boolean forceTargeting = true;
    private boolean forcePresetTargeting;

    public AbilityImpl(Targeting t, Effect e) {
        targeting = t;
        if (e == null) {
            main.system.auxiliary.LogMaster.log(1, "null abil effects!");
        }
        if (e instanceof Effects)
            effects = (Effects) e;
        else {
            effects = new Effects(e);
        }
    }

    @Override
    public String toString() {
        return "Ability has effects: " + effects.toString();
    }

    @Override
    public boolean isInterrupted() {
        if (interrupted) {
            interrupted = false;
            return true;
        }
        boolean result = false;
        for (Effect effect : effects) {
            if (effect != null)
                result |= effect.isInterrupted();
        }
        return result;
    }

    @Override
    public void setInterrupted(boolean b) {
        this.interrupted = b;

    }

    @Override
    public boolean activate() {
        return activate(ref);
    }

    @Override
    public boolean activate(boolean transmit) {
        return false;
    }

    @Override
    public boolean activate(Ref ref) {
        setRef(ref);

        if (!(targeting instanceof AutoTargeting))
            if (!(targeting instanceof FixedTargeting))
                if (isForcePresetTargeting() || targeting == null)
                    if (ref.getTarget() != null || ref.getGroup() != null) {
                        return resolve();
                    } else
                        return false;

        boolean selectResult = targeting.select(ref);
        ActiveObj a = ref.getActive();
        if (selectResult) {
            if (a != null)
                a.setCancelled(null);
            return resolve();
        } else {
            if (a != null)
                if (a.isCancelled() != null)
                    a.setCancelled(true);
            return false;
        }

    }

    @Override
    public boolean resolve() {
        main.system.auxiliary.LogMaster.log(0, "ABILITY_BEING_RESOLVED "
                + getClass().getSimpleName());
        Event event = new Event("ABILITY_BEING_RESOLVED", ref);
        if (game.fireEvent(event)) {
            return effects.apply(ref);
        } else
            return false;
    }

    @Override
    public void addEffect(Effect effect) {
        effects.add(effect);

    }

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        // CLONE?!
        this.game = ref.getGame();
        this.ref = ref;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
    }

    public Effects getEffects() {
        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        return true;
    }

    public boolean isForceTargeting() {
        return forceTargeting;
    }

    public void setForceTargeting(boolean forceTargeting) {
        this.forceTargeting = forceTargeting;
    }

    public boolean isForcePresetTargeting() {
        return forcePresetTargeting;
    }

    public void setForcePresetTargeting(boolean forcePresetTargeting) {
        this.forcePresetTargeting = forcePresetTargeting;
    }

}
