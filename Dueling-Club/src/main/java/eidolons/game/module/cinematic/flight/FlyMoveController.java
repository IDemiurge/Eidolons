package eidolons.game.module.cinematic.flight;

import eidolons.libgdx.bf.grid.moving.MoveController;
import main.game.bf.Coordinates;

public class FlyMoveController extends MoveController {
    public FlyMoveController(FlyingObj.FlyObjData data) {
        super(data);
    }

    @Override
    protected void coordinatesChanged() {

    }
    @Override
    protected boolean canArrive() {
        return false;
    }

    @Override
    protected Coordinates calcCurrent() {
        return null;
    }


    @Override
    protected void arrived() {

    }

    @Override
    protected boolean isArrived() {
        return false;
    }

    @Override
    protected boolean isInterpolated() {
        return false;
    }
}
