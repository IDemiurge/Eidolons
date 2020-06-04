package eidolons.game.netherflame.boss.demo;

import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnim3dHandler;
import eidolons.game.netherflame.boss.demo.anims.DemoAnimHandler3d;
import eidolons.game.netherflame.boss.demo.logic.DemoBossRules;
import eidolons.game.netherflame.boss.demo.logic.DemoBossTargeter;
import eidolons.game.netherflame.boss.demo.logic.DemoBossVision;
import eidolons.game.netherflame.boss.demo.logic.DemoRoundRules;
import eidolons.game.netherflame.boss.demo.logic.ai.DemoBossAi;
import eidolons.game.netherflame.boss.logic.rules.BossRulesImpl;
import eidolons.game.netherflame.boss.logic.rules.BossTargeter;
import eidolons.game.netherflame.boss.logic.rules.BossVision;
import eidolons.game.netherflame.boss.logic.rules.RoundRules;

public class DemoBossManager extends BossManager<DemoBoss> {

    @Override
    protected BossVision createVisionRules() {
        return new DemoBossVision(this);
    }

    @Override
    protected RoundRules createRoundRules() {
        return new DemoRoundRules(this);
    }

    @Override
    protected BossTargeter createTargeter() {
        return new DemoBossTargeter(this);
    }

    @Override
    protected DemoBoss createModel() {
        return new DemoBoss();
    }

    @Override
    protected BossAnim3dHandler createAnimHandler() {
        return new DemoAnimHandler3d(this);
    }

    @Override
    protected BossRulesImpl createRules() {
        return new DemoBossRules(this);
    }

    @Override
    protected BossAi createAi() {
        return new DemoBossAi(this);
    }
}
