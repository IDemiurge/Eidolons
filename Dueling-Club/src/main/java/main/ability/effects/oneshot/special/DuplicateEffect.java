package main.ability.effects.oneshot.special;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.MicroEffect;

public class DuplicateEffect extends MicroEffect {
    boolean event;

    public DuplicateEffect(Boolean event) {
        this.event = event;
    }

    @Override
    public boolean applyThis() {

        Effect copy;
        if (event)
            copy = ref.getEvent().getRef().getEffect().getCopy();
        else
            copy = ref.getEffect().getCopy();
        copy.setAltered(true); // !
        // copy.setQuietMode(true); and why would I do this? I only need to
        // check on triggers...

        // there got to be another way! This blocks
        // *everything* ...

        return copy.apply(ref);
    }

}
