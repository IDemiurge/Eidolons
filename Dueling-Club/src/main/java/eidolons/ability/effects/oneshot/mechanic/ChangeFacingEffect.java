package eidolons.ability.effects.oneshot.mechanic;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

import static main.game.logic.event.Event.STANDARD_EVENT_TYPE.*;

public class ChangeFacingEffect extends MicroEffect implements OneshotEffect {

    private Boolean clockwise;
    private Boolean self ;

    /*
     * for forcing the unit to face the target
     */
    public ChangeFacingEffect() {
        self=false;
    }

    public ChangeFacingEffect(Boolean clockwise) {
        this(clockwise, true);
    }

    public ChangeFacingEffect(Boolean clockwise, Boolean self) {
        this.setClockwise(clockwise);
        this.self = self;
    }

    @Override
    public boolean applyThis() {
        if (self!=null )
        if (!self)
            if (!(ref.getTargetObj() instanceof BattleFieldObject)) {
                return false;
            }

        BattleFieldObject obj = self ? (BattleFieldObject) ref.getSourceObj() : (BattleFieldObject) ref.getTargetObj();

        FACING_DIRECTION oldDirection = obj.getFacing();

        FACING_DIRECTION newDirection = null;
        if (isClockwise() == null) {
            for (FACING_DIRECTION f : main.game.bf.directions.FACING_DIRECTION.values()) {
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

        obj.setFacing(newDirection);
        if (clockwise != null) {
            game.fireEvent(new Event(clockwise ? UNIT_HAS_TURNED_CLOCKWISE : UNIT_HAS_TURNED_ANTICLOCKWISE, ref));
        }
        game.fireEvent(new Event(UNIT_HAS_CHANGED_FACING, ref));
        return true;
    }

    @Override
    public STANDARD_EVENT_TYPE getEventTypeDone() {
        if (clockwise == null) {
            return UNIT_HAS_CHANGED_FACING;
        }
        return clockwise ? UNIT_HAS_TURNED_CLOCKWISE : UNIT_HAS_TURNED_ANTICLOCKWISE;
    }

    public Boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(Boolean clockwise) {
        this.clockwise = clockwise;
    }

}
