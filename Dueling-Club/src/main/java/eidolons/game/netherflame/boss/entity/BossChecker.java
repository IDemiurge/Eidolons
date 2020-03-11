package eidolons.game.netherflame.boss.entity;

import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.obj.unit.Unit;

public class BossChecker extends UnitChecker {
    public BossChecker(BossUnit unit, BossMaster bossMaster) {
        super(unit, bossMaster);
    }

    @Override
    public boolean checkInSight() {
        return true;
    }

    @Override
    public boolean checkInSightForUnit(Unit unit) {
        return true;
    }

    @Override
    public boolean checkVisible() {
        return true;
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
        return false;
    }

    @Override
    public boolean canMove() {
        return false;
    }
}
