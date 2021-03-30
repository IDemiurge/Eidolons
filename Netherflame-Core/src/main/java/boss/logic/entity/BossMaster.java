package boss.logic.entity;

import eidolons.entity.handlers.bf.unit.*;
import boss.BossManager;
import main.game.logic.generic.ActionManager;

public class BossMaster extends UnitMaster {
    private BossManager manager;

    public BossMaster(BossUnit bossUnit ) {
        super(bossUnit);
    }

    public void setManager(BossManager manager) {
        this.manager = manager;
    }

    @Override
    public ActionManager getActionManager() {
        return manager.getActionMaster();
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
        return new BossUnitHandler(getUnit(), this);
    }
}
