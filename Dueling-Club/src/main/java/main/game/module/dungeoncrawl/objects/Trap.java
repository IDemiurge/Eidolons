package main.game.module.dungeoncrawl.objects;

import main.entity.obj.unit.Unit;

public class Trap {
    // actives -> upon trigger (spec effects!)


    public void disarmed(Unit trapper) {

    }


    public enum TRAP_TRIGGER_TYPE {
        TRIP, WEIGHT, SOUND, LIGHT, WARMTH, PHYSICAL_PRESENCE, MAGICAL_PRESENCE, ANY_PRESENCE,
    }

    public enum TRAP_TYPE {
        PHYSICAL, MAGICAL
    }

    public enum TRAP_TAGS {
        DESTRUCTIBLE, STATIONARY, INFINITE,
    }
}
