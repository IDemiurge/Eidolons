package boss.demo.logic;

import boss.logic.BossCycle;
import boss.logic.action.BOSS_ACTION;
import boss.logic.action.BossActionMaster;
import boss.BossManager;

import static boss.demo.logic.DemoBossActionManager.KNIGHT_ACTION.Inferno_Blade;

public class DemoBossActionManager extends BossActionMaster<DemoBossActionManager.KNIGHT_ACTION> {
    public DemoBossActionManager(BossManager manager) {
        super(manager);
    }

    @Override
    protected boolean check(BossCycle.BOSS_TYPE bossType, KNIGHT_ACTION value) {
        switch (value) {
            case FIRE_STORM:
                return bossType== BossCycle.BOSS_TYPE.caster;
            case Inferno_Blade:
                return bossType== BossCycle.BOSS_TYPE.melee;
        }
        return false;
    }

    @Override
    protected Class<KNIGHT_ACTION> getActionsEnum() {
        return KNIGHT_ACTION.class;
    }

    @Override
    public String getMainAttack() {
        return Inferno_Blade.getName();
    }
    public enum KNIGHT_ACTION implements BOSS_ACTION {
        FIRE_STORM,
        Inferno_Blade


    }
}
