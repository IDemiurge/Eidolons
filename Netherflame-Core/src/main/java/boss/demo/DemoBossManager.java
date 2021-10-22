package boss.demo;

import boss.anims.BossAnim3dHandler;
import boss.anims.view.BossViewFactory;
import boss.demo.anims.DemoAnimHandler3d;
import boss.demo.knight.DemoBossViewFactory;
import boss.demo.logic.*;
import boss.demo.logic.ai.DemoBossAi;
import boss.logic.BossCycle;
import boss.logic.action.BossActionMaster;
import boss.logic.rules.BossRulesImpl;
import boss.logic.rules.BossTargeter;
import boss.logic.rules.BossVision;
import boss.logic.rules.RoundRules;
import eidolons.game.core.game.DC_Game;
import boss.BossManager;
import boss.ai.BossAi;
import eidolons.netherflame.boss.demo.logic.*;

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
