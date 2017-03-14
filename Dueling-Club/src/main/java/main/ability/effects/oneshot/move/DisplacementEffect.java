package main.ability.effects.oneshot.move;

import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.MovementManager.MOVE_MODIFIER;
import main.system.auxiliary.RandomWizard;

public class DisplacementEffect extends SelfMoveEffect {

    private Coordinates c;

    @Override
    public boolean applyThis() {
        c = ref.getTargetObj().getCoordinates();
        try {
            if (!moveTarget()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return super.applyThis();
    }

    private boolean moveTarget() {
        Unit obj = (Unit) ref.getTargetObj();
        origin = obj.getCoordinates();
        Coordinates coordinate = obj.getCoordinates().getAdjacentCoordinate(
                obj.getFacing().getDirection());
        Boolean result = tryMove(obj, coordinate);
        if (result != null) {
            return result;
        }
        boolean clockwise = RandomWizard.random();
        coordinate = obj.getCoordinates().getAdjacentCoordinate(
                DirectionMaster.rotate45(obj.getFacing().getDirection(),
                        clockwise));
        result = tryMove(obj, coordinate);
        if (result != null) {
            return result;
        }
        clockwise = !clockwise;
        coordinate = obj.getCoordinates().getAdjacentCoordinate(
                DirectionMaster.rotate45(obj.getFacing().getDirection(),
                        clockwise));
        result = tryMove(obj, coordinate);
        if (result == null) {
            return false;
        }
        return result;
    }

    private Boolean tryMove(Unit obj, Coordinates coordinate) {
        Boolean result = null;
        if (game.getBattleFieldManager().isCellVisiblyFree(coordinate)) {
            try {
                result = game.getMovementManager().move(obj, coordinate, free,
                        MOVE_MODIFIER.DISPLACEMENT, ref);
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
        }
        return result;
    }

    @Override
    public Coordinates getCoordinates() {
        return c;
    }
}
