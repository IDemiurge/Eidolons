package main.game.event;

import main.game.event.Event.EVENT_TYPE;
import main.system.auxiliary.StringMaster;

public class EventType implements EVENT_TYPE {
    private CONSTRUCTED_EVENT_TYPE typeBasis;
    private String arg;
    public EventType(CONSTRUCTED_EVENT_TYPE typeBasis, String arg) {
        this.typeBasis = typeBasis;
        this.arg = arg;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String name() {
        return typeBasis.name() + StringMaster.FORMULA_REF_SEPARATOR + arg;
    }

    public boolean equals(EVENT_TYPE type) {
        if (!(type instanceof EventType)) {
            return false;
        }

        return (this.getType() == ((EventType) type).getType())
                && (StringMaster.compare(getArg(), type.getArg(), true));

    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public CONSTRUCTED_EVENT_TYPE getType() {
        return typeBasis;
    }

    public void setType(CONSTRUCTED_EVENT_TYPE type) {
        this.typeBasis = type;
    }

    public enum CONSTRUCTED_EVENT_TYPE {
        PARAM_BEING_MODIFIED,
        PARAM_MODIFIED,
        PROP_BEING_ADDED,
        PROP_ADDED,
        PROP_BEING_REMOVED,
        PROP_REMOVED,
        UNIT_IS_BEING_DEALT_DAMAGE_OF_TYPE,
        UNIT_IS_DEALT_DAMAGE_OF_TYPE,
        UNIT_HAS_BEEN_DEALT_DAMAGE_OF_TYPE
    }

}
