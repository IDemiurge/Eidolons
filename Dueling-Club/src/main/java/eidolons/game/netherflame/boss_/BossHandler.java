package eidolons.game.netherflame.boss_;

import eidolons.game.netherflame.boss_.ai.BossAI;
import eidolons.game.netherflame.boss_.anims.BossAnimHandler;
import eidolons.game.netherflame.boss_.anims.BossAssembly;

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
