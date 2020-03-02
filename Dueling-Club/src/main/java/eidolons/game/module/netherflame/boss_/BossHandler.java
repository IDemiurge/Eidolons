package eidolons.game.module.netherflame.boss_;

import eidolons.game.module.netherflame.boss_.ai.BossAI;
import eidolons.game.module.netherflame.boss_.anims.BossAnimHandler;
import eidolons.game.module.netherflame.boss_.anims.BossAssembly;

public class BossHandler<T extends BossModel> {

    BossManager<T> manager;

    public BossHandler(BossManager<T> manager) {
        this.manager = manager;
    }

    public T getModel() {
        return manager.getModel();
    }

    public BossAI getAi() {
        return manager.getAi();
    }

    public BossAnimHandler getAnimHandler() {
        return manager.getAnimHandler();
    }

    public BossAssembly getAssembly() {
        return manager.getAssembly();
    }

    public BossRules getRules() {
        return manager.getRules();
    }
}
