package eidolons.ability.effects.oneshot.move;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import main.entity.EntityCheckMaster;
import main.game.bf.Coordinates;
import main.game.bf.MovementManager;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.bf.directions.UNIT_DIRECTION;

public class PushEffect extends SelfMoveEffect {

    public PushEffect( ) {
        super(UNIT_DIRECTION.AHEAD, MovementManager.MOVE_MODIFIER.DISPLACEMENT );
    }

    @Override
    public boolean applyThis() {
        if (EntityCheckMaster.isOverlaying(ref.getTargetObj())) {
            OverlayingMaster.moveOverlaying(getTarget(),getSource(), true);
            return true;
        }
        return super.applyThis();
    }

    //TODO what to do with dia cases?
    @Override
    protected FACING_DIRECTION getFacing() {
        return getSource().getFacing();
    }
    @Override
    protected BattleFieldObject getObjToMove() {
        return getTarget();
    }
}
