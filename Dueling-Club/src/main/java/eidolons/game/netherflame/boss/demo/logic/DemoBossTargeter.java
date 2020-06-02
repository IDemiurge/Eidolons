package eidolons.game.netherflame.boss.demo.logic;

import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.demo.DemoHarvester;
import eidolons.game.netherflame.boss.logic.rules.BossTargeter;

public class DemoBossTargeter extends BossTargeter {
    public DemoBossTargeter(BossManager<DemoHarvester> manager) {
        super(manager);
    }
}
