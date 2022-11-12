package eidolons.game.battlecraft.ai.anew;

import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.content.enums.system.AiEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.DistanceCondition;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.system.auxiliary.data.ArrayMaster;

public class AiImpulseHandler extends AiUnitHandler {
    AiEnums.IMPULSE_TYPE[] types = AiEnums.IMPULSE_TYPE.values();

    /*
    Impulse enforces a goal-type and adds
    restrictions (protection) - we can't just filter, we need to create tasks that align;
    maybe we can do some abstract filtering- we know some tasks will always break the rule etc

    or
    opposite (e.g. berserk)
    it goes away if its condition is met OR some mind-affecting is done

    while under Impulse, intention is overridden,
     */
    public AiImpulse createImpulse() {
        AiImpulse impulse= null ;
        ActionSequence impulseSequence = createSequence(impulse);
        // impulseSequence.setExertion(true);

        return impulse;
    }

    public boolean checkClearImpulse(){
       AiImpulse impulse= ai.getCombatAI().getImpulse();
        if (getCondition(impulse).check(new Ref(ai.getUnit()).setMatch(ai.getUnit().getId()))) {
            return true;
        }
        return false;
    }

    private Condition getCondition(AiImpulse impulse) {
        switch (impulse.type) {
            case HATRED:
                break;
            case VENGEANCE:
            case FINISH:
               // return new PlayerRespawningCondition();
            case PROTECTION:
                // return new IsDeadCondition((Obj) impulse.arg);
            case BULLY_CHASE:
                break;
        }
        return null;
    }

    private ActionSequence createSequence(AiImpulse impulse) {
        return null;
    }

    public void processImpulse() {

        //AI will use Exert to execute impulses?!
    }

    public AiImpulse processEvent(Event event) {
        for (AiEnums.IMPULSE_TYPE type : types) {
            Event.EVENT_TYPE[] eventTypes = getEventTypesForImpulse(type);
            if (ArrayMaster.contains_(eventTypes, event.getType())) {
                if (checkChance(event, type ))
                    if (checkConditions(event, type )) {
                        return createImpulse();

                    }
            }
        }
        return null;
    }

    private boolean checkChance(Event event, AiEnums.IMPULSE_TYPE type ) {
        return false;
    }

    private boolean checkConditions(Event event, AiEnums.IMPULSE_TYPE type ) {
        return getConditions(event.getType(), type).check(getRef(event));
    }

    private Ref getRef(Event event) {
        Ref ref = new Ref(ai.getUnit());
        ref.setMatch(event.getRef().getSource());
        return ref;
    }

    private Condition getConditions(Event.EVENT_TYPE type, AiEnums.IMPULSE_TYPE impulseType) {
        switch (impulseType) {
            case HATRED:
                return new DistanceCondition("3");
        }
        return null;
    }

    private Event.STANDARD_EVENT_TYPE[] getEventTypesForImpulse(AiEnums.IMPULSE_TYPE type) {
        switch (type) {
            case FINISH:
                return new Event.STANDARD_EVENT_TYPE[]{
                        Event.STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE,
                };
            case HATRED:
                return new Event.STANDARD_EVENT_TYPE[]{
                        Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING,

                };
            case VENGEANCE:
                return new Event.STANDARD_EVENT_TYPE[]{
                        Event.STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED,
                };
        }
        return new Event.STANDARD_EVENT_TYPE[0];
    }
}
