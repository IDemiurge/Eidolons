package eidolons.game.netherflame.boss;

import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnim3dHandler;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.logic.rules.*;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class BossManager<T extends BossModel> {
    protected final BossTargeter targeter;
    protected final RoundRules roundRules;
    protected final BossVision visionRules;
    protected final T model; //full status?
    protected final BossAi ai;
    protected final BossAnim3dHandler animHandler3d;
    protected final BossRulesImpl  rules; //should handle most of the 'special' cases
    protected final Set<BossHandler> handlers = new LinkedHashSet<>();
    // protected final  BossAssembly assembly;
    //2d impl is important especially for modders - if it works, they can make 100 bosses...

    public BossManager() {
        model = createModel();
        handlers.add(  ai = createAi());
        handlers.add( rules = createRules());
        handlers.add( animHandler3d = createAnimHandler());
        handlers.add(targeter = createTargeter());
        handlers.add( roundRules = createRoundRules());
        handlers.add( visionRules = createVisionRules());

        for (BossHandler handler : getHandlers()) {
            handler.init();
        }
        // BOSS_PART[] parts = createParts();
        // assembly = new BossAssembly(parts);
    }

    protected abstract BossVision createVisionRules();

    protected abstract RoundRules createRoundRules();

    protected abstract BossTargeter createTargeter();

    protected abstract T createModel();

    protected abstract BossAnim3dHandler createAnimHandler();

    protected abstract BossRulesImpl createRules();

    protected abstract BossAi createAi();

    public T getModel() {
        return model;
    }

    public BossAi getAi() {
        return ai;
    }

    public BossAnimHandler getAnimHandler3d() {
        return animHandler3d;
    }

    public BossRules getRules() {
        return rules;
    }

    public BossTargeter getTargeter() {
        return targeter;
    }

    public RoundRules getRoundRules() {
        return roundRules;
    }

    public BossVision getVisionRules() {
        return visionRules;
    }

    public Set<BossHandler> getHandlers() {
        return handlers;
    }
}
