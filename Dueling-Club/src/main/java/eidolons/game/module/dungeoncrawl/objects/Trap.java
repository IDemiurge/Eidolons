package eidolons.game.module.dungeoncrawl.objects;

import eidolons.entity.obj.unit.Unit;

public class Trap {
    // actives -> upon trigger (spec effects!)


    public void trigger(Unit trapper) {

    }

    public void disarmed(Unit trapper) {

    }

    public boolean isRemoveOnTrigger() {
        return true;
    }


    public enum TRAP_TAGS {
        DESTRUCTIBLE, STATIONARY, INFINITE,
    }

    public enum TRAP_TRIGGER_TYPE {
        TRIP, WEIGHT, SOUND, LIGHT, WARMTH, PHYSICAL_PRESENCE, MAGICAL_PRESENCE, ANY_PRESENCE,
    }

    public enum TRAP_TYPE {
        PHYSICAL, MAGICAL
    }
}
