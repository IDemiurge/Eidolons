package eidolons.game.netherflame.boss_.lords.harvester;

import eidolons.game.netherflame.boss_.BOSS_PART;
import eidolons.game.netherflame.boss_.BossManager;
import eidolons.game.netherflame.boss_.ai.BossAi;
import eidolons.game.netherflame.boss_.anims.BossAnimHandler;
import eidolons.game.netherflame.boss_.logic.rules.BossRules;

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
