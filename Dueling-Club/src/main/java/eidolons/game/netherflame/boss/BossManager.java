package eidolons.game.netherflame.boss;

import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.anims.view.BossAssembly;
import eidolons.game.netherflame.boss.logic.rules.BossRules;

public abstract class BossManager<T extends BossModel> {
    T model; //full status?
    BossAi ai;
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

    protected abstract BossAi createAi();

    public T getModel() {
        return model;
    }

    public BossAi getAi() {
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
