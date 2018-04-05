package main.entity.obj;

import main.ability.effects.Effects;

public interface Attachment {
    double getDuration();

    Double tick();

    boolean isRetainAfterDeath();

    void setRetainAfterDeath(boolean retainAfterDeath);

    boolean checkRetainCondition();

    void remove();

    Effects getEffects();

    // public Trigger getTriggers();

    boolean isTransient();

    void setTransient(boolean b);

    Obj getBasis();
}
