package eidolons.ability.effects.oneshot.move;

import eidolons.entity.unit.Unit;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.MovementManager.MOVE_MODIFIER;
import main.system.auxiliary.RandomWizard;

public class DisplacementEffect extends SelfMoveEffect {

    private Coordinates c;

    public DisplacementEffect() {
    }

    @Override
    public boolean applyThis() {
        c = ref.getTargetObj().getCoordinates();
        try {
            if (!moveTarget()) {
                return false;
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return super.applyThis();
    }

    private boolean moveTarget() {
        Unit obj = (Unit) ref.getTargetObj();
        origin = obj.getCoordinates();
        DIRECTION direction = DIRECTION.NONE;
        Coordinates coordinate = obj.getCoordinates().getAdjacentCoordinate(
                //TODO LC 2.0 review
         direction);
        Boolean result = tryMove(obj, coordinate);
        if (result != null) {
            return result;
        }
        boolean clockwise = RandomWizard.random();
        coordinate = obj.getCoordinates().getAdjacentCoordinate(
         DirectionMaster.rotate45(direction,
          clockwise));
        result = tryMove(obj, coordinate);
        if (result != null) {
            return result;
        }
        clockwise = !clockwise;
        coordinate = obj.getCoordinates().getAdjacentCoordinate(
         DirectionMaster.rotate45(direction,
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
                main.system.ExceptionMaster.printStackTrace(e);
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
