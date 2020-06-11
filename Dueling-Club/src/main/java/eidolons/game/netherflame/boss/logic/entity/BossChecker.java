package eidolons.game.netherflame.boss.logic.entity;

import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.UnitEnums;

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

    @Override
    public boolean checkImmunity(UnitEnums.IMMUNITIES type) {
        return super.checkImmunity(type);
    }
}
