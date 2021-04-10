package eidolons.game.battlecraft.ai.anew;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.content.enums.system.AiEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.DistanceCondition;
import main.entity.Entity;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.system.auxiliary.data.ArrayMaster;

public class AiImpulseHandler extends AiUnitHandler {
    AiEnums.IMPULSE_TYPE[] types = AiEnums.IMPULSE_TYPE.values();

    public AiImpulse createImpulse() {
        AiImpulse impulse= null ;
        ActionSequence impulseSequence = createSequence(impulse);
        // impulseSequence.setExertion(true);

        return impulse;
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
            case VENGEANCE:
                break;
            case FEAR:
                break;
            case PROTECTIVENESS:
                break;
            case FINISH:
                break;
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
            case FEAR:
                return new Event.STANDARD_EVENT_TYPE[]{
                        Event.STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE,
                };
            case PROTECTIVENESS:
                return new Event.STANDARD_EVENT_TYPE[]{
                        Event.STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE,
                };
        }
        return new Event.STANDARD_EVENT_TYPE[0];
    }
}
