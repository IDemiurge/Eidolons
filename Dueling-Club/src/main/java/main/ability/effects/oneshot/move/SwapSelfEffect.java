package main.ability.effects.oneshot.move;

import main.game.battlefield.Coordinates;
import main.game.battlefield.MovementManager.MOVE_MODIFIER;

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
            e.printStackTrace();
            return false;
        }
        boolean result = game.getMovementManager().move(ref.getSourceObj(), c,
                free, MOVE_MODIFIER.DISPLACEMENT, ref);
        getGame().getBattleField().getGrid()
                .addUnitObj(ref.getTargetObj());
        return result;
    }

    @Override
    public Coordinates getCoordinates() {
        return c;
    }
}
