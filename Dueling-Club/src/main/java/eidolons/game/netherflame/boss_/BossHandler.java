package eidolons.game.netherflame.boss_;

import eidolons.game.netherflame.boss_.ai.BossAi;
import eidolons.game.netherflame.boss_.anims.BossAnimHandler;
import eidolons.game.netherflame.boss_.anims.view.BossAssembly;
import eidolons.game.netherflame.boss_.logic.rules.BossRules;

public class BossHandler<T extends BossModel> {

    BossManager<T> manager;

    public BossHandler(BossManager<T> manager) {
        this.manager = manager;
    }

    public T getModel() {
        return manager.getModel();
    }

    public BossAi getAi() {
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
