package main.game.logic.dungeon.special;

import main.entity.obj.unit.DC_HeroObj;

public class Trap {
    // actives -> upon trigger (spec effects!)


    public void disarmed(DC_HeroObj trapper) {

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
