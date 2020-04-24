package eidolons.game.netherflame.boss.lords.harvester;

import eidolons.game.netherflame.boss.BOSS_PART;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.logic.rules.BossRules;

public class HarvesterManager extends BossManager<Harvester> {
    @Override
    protected BOSS_PART[] createParts() {
        return new BOSS_PART[0];
    }

    @Override
    protected Harvester createModel() {
        return new Harvester();
    }

    @Override
    protected BossAnimHandler createAnimHandler() {
        return null;
    }

    @Override
    protected BossRules createRules() {
        return new HarvesterRules();
    }

    @Override
    protected BossAi createAi() {
        return null;
    }
}
