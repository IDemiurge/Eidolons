package eidolons.game.netherflame.boss;

import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.anims.view.BossAssembly;
import eidolons.game.netherflame.boss.logic.rules.BossRules;

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
