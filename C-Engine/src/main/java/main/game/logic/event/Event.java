package main.game.logic.event;

import main.entity.Ref;
import main.entity.Referred;
import main.game.core.game.Game;
import main.game.core.game.MicroGame;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

/**
 * @author JustMe
 *         <portrait>
 *         <portrait>
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

        //ACTION
        COSTS_HAVE_BEEN_PAID,
        COSTS_ARE_BEING_PAID,
        ACTION_ACTIVATED,
        UNIT_ACTION_COMPLETE,
        HOSTILE_ACTION,
        ABILITY_BEING_RESOLVED,
        //ATTACKS
        UNIT_IS_BEING_ATTACKED,
        UNIT_HAS_BEEN_ATTACKED,
        ATTACK_CRITICAL,
        ATTACK_SNEAK,
        ATTACK_DODGED,
        ATTACK_PARRIED,
        ATTACK_BLOCKED,
        ATTACK_MISSED,

        ATTACK_OF_OPPORTUNITY,
        ATTACK_COUNTER,
        ATTACK_INSTANT,

        UNIT_HAS_BEEN_KILLED,
        UNIT_HAS_BEEN_ANNIHILATED,
        UNIT_HAS_CHANGED_FACING,
        UNIT_HAS_TURNED_CLOCKWISE,
        UNIT_HAS_TURNED_ANTICLOCKWISE,
        //        PARAMSMORALE_REDUCTION,
        MORALE_BOOST,
        MORALE_PANIC,
        MORALE_TREASON,

        //        EFFECT
        EFFECT_IS_BEING_APPLIED,
        EFFECT_HAS_BEEN_APPLIED,
        EFFECT_ADD_COUNTER,
        EFFECT_MODIFY_VALUE,
        //        DUNGEON

        //        ITEM
        ITEM_MANIPULATED,

        //GENERIC
        NEW_ROUND,
        ROUND_ENDS,

        //UNIT MAIN
        UNIT_TURN_STARTED,
        UNIT_NEW_ROUND_STARTED,
        UNIT_NEW_ROUND_BEING_STARTED,

        UNIT_IS_BEING_KILLED,
        UNIT_BEING_MOVED,
        UNIT_MOVED,
        UNIT_FINISHED_MOVING,

        UNIT_BEING_SUMMONED,
        UNIT_SUMMONED,

        //UNIT MISC
        COUNTER_MODIFIED,
        COUNTER_BEING_MODIFIED,
        UNIT_OWNERSHIP_CHANGED,
        UNIT_HAS_BEEN_HIT,

        //RULES
        UNIT_WAITS,
//EXTRA ATTACKS


        // SPELLS
        SPELL_ACTIVATED,
        SPELL_BEING_RESOLVED,
        SPELL_RESOLVED,
        // BUFFS
        BUFF_BEING_REMOVED,
        BUFF_REMOVED,

        PASSIVE_REMOVED,
        PASSIVE_BEING_REMOVED,

        // DAMAGE
        UNIT_IS_BEING_DEALT_DAMAGE,
        UNIT_IS_BEING_DEALT_SPELL_DAMAGE,
        UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE,
        UNIT_IS_DEALT_SPELL_DAMAGE,
        UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE,

        UNIT_IS_BEING_DEALT_ENDURANCE_DAMAGE,
        UNIT_IS_BEING_DEALT_TOUGHNESS_DAMAGE,
        UNIT_IS_DEALT_ENDURANCE_DAMAGE,
        UNIT_IS_DEALT_TOUGHNESS_DAMAGE,
        UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE,
        UNIT_IS_DEALT_PHYSICAL_TOUGHNESS_DAMAGE,
        UNIT_IS_DEALT_PHYSICAL_ENDURANCE_DAMAGE,
        UNIT_IS_DEALT_MAGICAL_ENDURANCE_DAMAGE,
        UNIT_IS_DEALT_MAGICAL_TOUGHNESS_DAMAGE,
        UNIT_HAS_BEEN_DEALT_PURE_DAMAGE, GAME_STARTED;
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
