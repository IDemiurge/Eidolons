package main.ability.effects;

import main.ability.effects.oneshot.MicroEffect;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.entity.Ref.KEYS;
import main.entity.obj.BattlefieldObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.FacingMaster;

public class ChangeFacingEffect extends MicroEffect {

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
        if (!(ref.getTargetObj() instanceof DC_HeroObj))
            return false;
        DC_HeroObj obj = (DC_HeroObj) ref.getTargetObj();

        FACING_DIRECTION oldDirection = obj.getFacing();

        FACING_DIRECTION newDirection = null;
        if (isClockwise() == null) {
            for (FACING_DIRECTION f : FACING_DIRECTION.values()) {
                Obj active = ref.getObj(KEYS.ACTIVE);
                if (active == null) {
                    return false;
                }
                if (FacingMaster.getSingleFacing(f, obj,
                        (BattlefieldObj) active.getRef().getTargetObj()) == FACING_SINGLE.IN_FRONT) {
                    newDirection = f;
                    break;
                }

            }
        } else
            newDirection = FacingMaster.rotate(oldDirection, isClockwise());

        obj.setFacing(newDirection);
        return true;
    }

    public Boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(Boolean clockwise) {
        this.clockwise = clockwise;
    }

}
