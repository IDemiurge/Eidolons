package main.ability.effects.oneshot.move;

import main.entity.Ref.KEYS;
import main.game.bf.Coordinates;
import main.game.bf.MovementManager.MOVE_MODIFIER;

public class SwapEffect extends MoveEffect {
    private Coordinates c;

    @Override
    public boolean applyThis() {
        c = ref.getTargetObj().getCoordinates();
        try {
            game.getMovementManager().move(ref.getTargetObj(),
             ref.getSourceObj().getCoordinates(), free,
             MOVE_MODIFIER.DISPLACEMENT, ref);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return game.getMovementManager().move(ref.getObj(KEYS.TARGET2), c,
         free, MOVE_MODIFIER.DISPLACEMENT, ref);
    }

}
