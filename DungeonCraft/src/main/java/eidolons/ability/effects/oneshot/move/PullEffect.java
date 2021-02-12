package eidolons.ability.effects.oneshot.move;

import eidolons.entity.obj.BattleFieldObject;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.MovementManager;
import main.game.bf.directions.UNIT_DIRECTION;

public class PullEffect extends SelfMoveEffect {

    public PullEffect( ) {
        super(UNIT_DIRECTION.BACKWARDS, MovementManager.MOVE_MODIFIER.DISPLACEMENT );
    }

    @Override
    public boolean apply(Ref ref) {
        // if (EntityCheckMaster.isOverlaying(ref.getTargetObj())) {
        //    OverlayingMaster.moveOverlaying(getTarget(),getSource(), false);
        //     return true;
        // }
        return super.apply(ref);
    }

    @Override
    public Coordinates getCoordinates() {
        return getSource().getCoordinates();
    }

    @Override
    protected BattleFieldObject getObjToMove() {
        return getTarget();
    }
}
