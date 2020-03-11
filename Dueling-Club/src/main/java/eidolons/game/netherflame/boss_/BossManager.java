package eidolons.game.netherflame.boss_;

import eidolons.game.netherflame.boss_.ai.BossAI;
import eidolons.game.netherflame.boss_.anims.BossAnimHandler;
import eidolons.game.netherflame.boss_.anims.BossAssembly;

public abstract class BossManager<T extends BossModel> {
    T model; //full status?
    BossAI ai;
    BossAnimHandler animHandler;
    //2d impl is important especially for modders - if it works, they can make 100 bosses...
    BossAssembly assembly;
    BossRules rules; //should handle most of the 'special' cases

    public BossManager() {
        model = createModel();
        ai = createAi();
        rules = createRules();
        animHandler = createAnimHandler();
        BOSS_PART[] parts = createParts();
        assembly = new BossAssembly(parts);
    }

    protected abstract BOSS_PART[] createParts();

    protected abstract T createModel();

    protected abstract BossAnimHandler createAnimHandler();

    protected abstract BossRules createRules();

    protected abstract BossAI createAi();

    public T getModel() {
        return model;
    }

    public BossAI getAi() {
        return ai;
    }

    public BossAnimHandler getAnimHandler() {
        return animHandler;
    }

    public BossAssembly getAssembly() {
        return assembly;
    }

    public BossRules getRules() {
        return rules;
    }
}
