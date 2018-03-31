package main.libgdx.gui.datasource;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;

/**
 * Created by JustMe on 3/30/2018.
 */
public class FullUnitDataSource extends EntityDataSource<Unit> {
    public FullUnitDataSource(Unit entity) {
        super(entity);
    }

    public FACING_DIRECTION getFacingOrNull() {
        return entity.getFacingOrNull();
    }

    public DIRECTION getDirection() {
        return entity.getDirection();
    }

    public FACING_DIRECTION getFacing() {
        return entity.getFacing();
    }

    public boolean isHero() {
        return entity.isHero();
    }

    public boolean isLiving() {
        return entity.isLiving();
    }

    public boolean isUnconscious() {
        return entity.isUnconscious();
    }

    public boolean isIncapacitated() {
        return entity.isIncapacitated();
    }

    public boolean isImmobilized() {
        return entity.isImmobilized();
    }

    public DC_ActiveObj getTurnAction(boolean clockwise) {
        return entity.getTurnAction(clockwise);
    }
}
