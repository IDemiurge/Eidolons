package libgdx.gui.dungeon.datasource;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;

/**
 * Created by JustMe on 3/30/2018.
 */
public class FullUnitDataSource extends EntityDataSource<Unit> {
    public FullUnitDataSource(Unit entity) {
        super(entity);
    }

    public DIRECTION getDirection() {
        return entity.getDirection();
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
