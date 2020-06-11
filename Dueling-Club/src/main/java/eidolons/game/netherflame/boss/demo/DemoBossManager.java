package eidolons.game.netherflame.boss.demo;

import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnim3dHandler;
import eidolons.game.netherflame.boss.anims.view.BossViewFactory;
import eidolons.game.netherflame.boss.demo.anims.DemoAnimHandler3d;
import eidolons.game.netherflame.boss.demo.knight.DemoBossViewFactory;
import eidolons.game.netherflame.boss.demo.logic.*;
import eidolons.game.netherflame.boss.demo.logic.ai.DemoBossAi;
import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.game.netherflame.boss.logic.action.BossActionMaster;
import eidolons.game.netherflame.boss.logic.rules.BossRulesImpl;
import eidolons.game.netherflame.boss.logic.rules.BossTargeter;
import eidolons.game.netherflame.boss.logic.rules.BossVision;
import eidolons.game.netherflame.boss.logic.rules.RoundRules;

public class DemoBossManager extends BossManager<DemoBoss> {

    public DemoBossManager(DC_Game game) {
        super(game);
    }

    @Override
    protected BossActionMaster createActionMaster() {
        return new DemoBossActionManager(this);
    }

    @Override
    protected BossViewFactory createFactory() {
        return new DemoBossViewFactory(this);
    }

    @Override
    protected BossCycle createCycle() {
        return new DemoBossCycle(this);
    }

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
