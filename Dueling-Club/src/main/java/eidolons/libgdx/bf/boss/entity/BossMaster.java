package eidolons.libgdx.bf.boss.entity;

import eidolons.entity.handlers.bf.unit.*;
import eidolons.entity.obj.BattleFieldObject;

public class BossMaster extends UnitMaster {
    public BossMaster(BossUnit bossUnit) {
        super(bossUnit);
    }


    @Override
    public BossUnit getUnit() {
        return (BossUnit) super.getUnit();
    }

    @Override
    protected UnitChecker createEntityChecker() {
        return new BossChecker(getUnit(), this);
    }

    @Override
    protected UnitResetter createResetter() {
        return new UnitResetter(getUnit(), this);
    }

    @Override
    protected UnitCalculator createCalculator() {
        return new UnitCalculator(getUnit(), this);
    }

    @Override
    protected BossInitializer createInitializer() {
        return new BossInitializer(getEntity(), this);
    }

    @Override
    protected UnitHandler createHandler() {
        return new BossHandler(getUnit(), this);
    }
}
