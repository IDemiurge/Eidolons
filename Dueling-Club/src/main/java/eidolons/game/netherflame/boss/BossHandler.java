package eidolons.game.netherflame.boss;

import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.logic.rules.BossRules;
import eidolons.game.netherflame.boss.logic.rules.BossTargeter;
import eidolons.game.netherflame.boss.logic.rules.BossVision;
import eidolons.game.netherflame.boss.logic.rules.RoundRules;

import java.util.Set;

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
        return manager.getAnimHandler3d();
    }

    public BossRules getRules() {
        return manager.getRules();
    }

    public BossTargeter getTargeter() {
        return manager.getTargeter();
    }

    public RoundRules getRoundRules() {
        return manager.getRoundRules();
    }

    public BossVision getVisionRules() {
        return manager.getVisionRules();
    }

    public Set<BossHandler> getHandlers() {
        return manager.getHandlers();
    }

    public void roundStarts() {
    }

    public void init() {

    }
}
