package eidolons.ability.effects.oneshot.move;

import main.game.bf.Coordinates;
import main.game.bf.MovementManager.MOVE_MODIFIER;

public class SwapSelfEffect extends SelfMoveEffect {

    private Coordinates c;

    @Override
    public boolean applyThis() {
        c = ref.getTargetObj().getCoordinates();
        origin = c;
        try {
            game.getMovementManager().move(ref.getTargetObj(),
             ref.getSourceObj().getCoordinates(), free,
             MOVE_MODIFIER.DISPLACEMENT, ref);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        //      TODO   getGame() .getGrid()
//         .addUnitObj(ref.getTargetObj());
        return game.getMovementManager().move(ref.getSourceObj(), c,
         free, MOVE_MODIFIER.DISPLACEMENT, ref);
    }

    @Override
    public Coordinates getCoordinates() {
        return c;
    }
}
