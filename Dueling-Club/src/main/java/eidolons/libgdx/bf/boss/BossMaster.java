package eidolons.libgdx.bf.boss;

import eidolons.entity.handlers.bf.unit.*;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import main.entity.handlers.EntityChecker;
import main.entity.handlers.EntityHandler;
import main.entity.handlers.EntityMaster;

public class BossMaster extends UnitMaster {
    public BossMaster(BossUnit bossUnit) {
        super(bossUnit);
    }

    public static String getSpritePath(BattleFieldObject obj) {
        return "sprites/boss/iron/atlas.txt";
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
    protected UnitHandler createHandler() {
        return new BossHandler(getUnit(), this);
    }
}
