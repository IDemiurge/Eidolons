package main.ability.effects.oneshot.mechanic;

import main.ability.effects.OneshotEffect;
import main.ability.effects.MicroEffect;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.FacingMaster;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class ChangeFacingEffect extends MicroEffect  implements OneshotEffect {

    private Boolean clockwise;

    /*
     * for forcing the unit to face the target
     */
    public ChangeFacingEffect() {

    }

    public ChangeFacingEffect(Boolean clockwise) {
        this.setClockwise(clockwise);
    }

    @Override
    public boolean applyThis() {
        if (!(ref.getTargetObj() instanceof Unit)) {
            return false;
        }
        Unit obj = (Unit) ref.getTargetObj();

        FACING_DIRECTION oldDirection = obj.getFacing();

        FACING_DIRECTION newDirection = null;
        if (isClockwise() == null) {
            for (FACING_DIRECTION f : FACING_DIRECTION.values()) {
                Obj active = ref.getObj(KEYS.ACTIVE);
                if (active == null) {
                    return false;
                }
                if (FacingMaster.getSingleFacing(f, obj,
                        (BfObj) active.getRef().getTargetObj()) == UnitEnums.FACING_SINGLE.IN_FRONT) {
                    newDirection = f;
                    break;
                }

            }
        } else {
            newDirection = FacingMaster.rotate(oldDirection, isClockwise());
        }

        obj.resetFacing(newDirection);
        game.fireEvent(new Event(getEventTypeDone(), ref));
        return true;
    }

    @Override
    public STANDARD_EVENT_TYPE getEventTypeDone() {
        if (clockwise == null) {
            return STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING;
        }
        return clockwise ? STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE : STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE;
    }

    public Boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(Boolean clockwise) {
        this.clockwise = clockwise;
    }

}
