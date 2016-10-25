package main.ability.effects.oneshot.special;

import main.ability.Interruptable;
import main.ability.effects.oneshot.MicroEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class InterruptEffect extends MicroEffect {

    private String OBJ_REF;

    public InterruptEffect() {

    }

    public InterruptEffect(String OBJ_REF) {
        this.OBJ_REF = OBJ_REF;
    }

    @Override
    public boolean applyThis() {
        // if (OBJ_REF == null) {
        // game.getState().setInterrupted(true);
        //
        // return true;
        // }
        Ref REF = ref.getEvent().getRef();
        Interruptable i = REF.getEffect();
        // if (OBJ_REF != null) {
        i = (Interruptable) REF.getObj(KEYS.ACTIVE);
        // }
        if (i == null)
            return false;
        i.setInterrupted(true);
        return true;
    }

}
