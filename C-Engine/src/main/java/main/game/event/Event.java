package main.game.event;

import main.entity.Ref;
import main.entity.Referred;
import main.game.Game;
import main.game.MicroGame;
import main.game.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

/**
 * @author JustMe
 *         <p>
 *         <p>
 *         Event firing - where in code should the firing occur? How can it be
 *         made more generic? IDEA: events must be constructed on the fly
 *         whenever an entity's method (like "modifyValue", "destroy" or
 *         "activate") is called. Thus event will have a generic type and
 *         specific parameters and references.
 */
public class Event implements Referred {
    protected Ref ref;

    protected EVENT_TYPE type;

    public Event(String type, Ref ref) {

        this.type = STANDARD_EVENT_TYPE.valueOf(type);
        this.ref = ref;
    }

    public Event(EVENT_TYPE t, Game game) {
        this(t, new Ref(game));
    }

    public Event(EVENT_TYPE t, Ref ref2) {
        this.type = t;
        this.ref = ref2;
    }

    public Event(CONSTRUCTED_EVENT_TYPE typeBasis, String string, Ref ref) {
        this(new EventType(typeBasis, string), ref);
    }

    public static STANDARD_EVENT_TYPE getEventType(String type) {
        return new EnumMaster<STANDARD_EVENT_TYPE>().retrieveEnumConst(STANDARD_EVENT_TYPE.class,
                type);
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public boolean canBeInterrupted() {
        if (type instanceof STANDARD_EVENT_TYPE) {
            STANDARD_EVENT_TYPE stdType = (STANDARD_EVENT_TYPE) type;
            switch (stdType) {
                case ROUND_ENDS:
                case NEW_ROUND:
                case UNIT_TURN_STARTED:
                case UNIT_NEW_ROUND_STARTED:
                case UNIT_NEW_ROUND_BEING_STARTED:
                    return false;
            }
            return true;
        }
        return false;
    }

    public EVENT_TYPE getType() {
        return type;
    }

    public Event getEvent() {
        return ref.event;
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<GETTERS&SETTERS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // \\

    public void setEvent(Event event) {
        ref.event = event;
    }

    public boolean isBase() {
        return ref.base;
    }

    public void setBase(boolean base) {
        ref.base = base;
    }

    public void setGame(MicroGame game) {
        ref.game = game;
    }

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
        ref.setEvent(this);
    }

    public boolean fire() {
        return ref.getGame().fireEvent(this);
    }

    public boolean isTriggered() {
        return ref.isTriggered();
    }

    public void setTriggered(boolean triggered) {
        ref.setTriggered(triggered);
    }

    public enum STANDARD_EVENT_TYPE implements EVENT_TYPE {

        MORALE_REDUCTION,
        MORALE_BOOST,

        MORALE_PANIC,
        MORALE_TREASON,

        EFFECT_IS_BEING_APPLIED,
        EFFECT_HAS_BEEN_APPLIED,
        EFFECT_ADD_COUNTER,
        EFFECT_MODIFY_VALUE,

        ABILITY_BEING_RESOLVED,
        ACTION_ACTIVATED,
        ACTION_COMPLETE,

        OBJECT_ENTERS_BATTLEFIELD,
        UNIT_HAS_BEEN_KILLED,


        UNIT_HAS_TURNED,
        UNIT_HAS_TURNED_CLOCKWISE,
        UNIT_HAS_TURNED_ANTICLOCKWISE,

        ITEM_MANIPULATED,
        DUMMY,

        NEW_ROUND,
        ROUND_ENDS,
        UNIT_TURN_STARTED,
        UNIT_NEW_ROUND_STARTED,
        UNIT_NEW_ROUND_BEING_STARTED,

        UNIT_IS_BEING_KILLED,
        UNIT_MOVES,
        UNIT_BEING_MOVED,
        UNIT_MOVED,
        UNIT_SUMMONED,

        // SPELL_BEING_ACTIVATED,
        SPELL_ACTIVATED,
        // SPELL_BEING_RESOLVED,
        BUFF_BEING_REMOVED,
        BUFF_REMOVED,
        UNIT_IS_BEING_DEALT_DAMAGE,
        UNIT_IS_BEING_DEALT_SPELL_DAMAGE,
        UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE,
        UNIT_IS_DEALT_SPELL_DAMAGE,
        UNIT_IS_BEING_ATTACKED,
        UNIT_HAS_BEEN_ATTACKED,
        UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE,

        UNIT_IS_BEING_DEALT_ENDURANCE_DAMAGE,
        UNIT_IS_BEING_DEALT_TOUGHNESS_DAMAGE,
        UNIT_IS_DEALT_ENDURANCE_DAMAGE,
        UNIT_IS_DEALT_TOUGHNESS_DAMAGE,
        SPELL_BEING_RESOLVED,
        SPELL_RESOLVED,
        PASSIVE_REMOVED,
        PASSIVE_BEING_REMOVED,
        HOSTILE_ACTION,
        COUNTER_MODIFIED,
        COUNTER_BEING_MODIFIED,
        UNIT_BEING_SUMMONED,
        UNIT_FINISHED_MOVING,
        UNIT_OWNERSHIP_CHANGED,
        UNIT_ACTION_COMPLETE,
        UNIT_HAS_BEEN_HIT,
        UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE,
        UNIT_IS_DEALT_PHYSICAL_TOUGHNESS_DAMAGE,
        UNIT_IS_DEALT_PHYSICAL_ENDURANCE_DAMAGE,
        UNIT_WAITS,;
        private String arg = "";

        STANDARD_EVENT_TYPE() {

        }

        // EVENT_TYPE(String arg) {
        // this.setArg(arg);
        // }

        public String getFullName() {
            return name() + StringMaster.FORMULA_REF_SEPARATOR + getArg();
        }

        public boolean equals(EVENT_TYPE type) {
            if (type instanceof EventType) {
                return false;
            }
            if (arg.equals("")) {
                return (this == type);
            }

            return (this == type) && (arg.equals(type.getArg()));

        }

        public String getArg() {
            return arg;
        }

        public void setArg(String arg) {
            this.arg = arg;
        }

    }

    public interface EVENT_TYPE {
        boolean equals(EVENT_TYPE e);

        String name();

        String getArg();
    }

}
