package eidolons.entity.obj.unit;

import main.entity.Entity;
import main.entity.handlers.EntityMaster;
import main.game.bf.directions.FACING_DIRECTION;

public abstract class DummyUnit extends Entity implements FacingEntity {
    @Override
    public FACING_DIRECTION getFacing() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    protected EntityMaster initMaster() {
        return null;
    }
}
