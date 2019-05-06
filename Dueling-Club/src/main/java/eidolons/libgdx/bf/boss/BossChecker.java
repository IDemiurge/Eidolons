package eidolons.libgdx.bf.boss;

import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.obj.unit.Unit;

public class BossChecker extends UnitChecker {
    public BossChecker(BossUnit unit, BossMaster bossMaster) {
        super(unit, bossMaster);
    }

    @Override
    public boolean checkInSight() {
        return super.checkInSight();
    }

    @Override
    public boolean checkInSightForUnit(Unit unit) {
        return super.checkInSightForUnit(unit);
    }

    @Override
    public boolean checkVisible() {
        return super.checkVisible();
    }

    @Override
    public boolean isHero() {
        return super.isHero();
    }

    @Override
    public boolean isHuge() {
        return super.isHuge();
    }

    @Override
    public boolean isTurnable() {
        return super.isTurnable();
    }

    @Override
    public boolean canMove() {
        return super.canMove();
    }
}
