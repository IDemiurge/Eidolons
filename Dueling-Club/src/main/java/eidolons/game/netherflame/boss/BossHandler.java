package eidolons.game.netherflame.boss;

import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.game.netherflame.boss.logic.action.BossActionMaster;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.game.netherflame.boss.logic.rules.BossRules;
import eidolons.game.netherflame.boss.logic.rules.BossTargeter;
import eidolons.game.netherflame.boss.logic.rules.BossVision;
import eidolons.game.netherflame.boss.logic.rules.RoundRules;

import java.util.Map;
import java.util.Set;

public class BossHandler<T extends BossModel> {

    BossManager<T> manager;

    public BossHandler(BossManager<T> manager) {
        this.manager = manager;
    }

    protected DC_Game getGame() {
        return manager.getGame();
    }

    public T getModel() {
        return manager.getModel();
    }

    protected BossVisual getVisual(BossCycle.BOSS_TYPE type) {
        return manager.getFactory().get(type);
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

    public BossActionMaster getActionMaster() {
        return manager.getActionMaster();
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

    public BossCycle getCycle() {
        return manager.getCycle();
    }

    public String getName(BossCycle.BOSS_TYPE type) {
        return getModel().getName(type);
    }

    public Set<BossHandler> getHandlers() {
        return manager.getHandlers();
    }

    public void roundStarts() {
    }

    public String getName() {
        return getModel().getName();
    }


    public Map<BossCycle.BOSS_TYPE, BossUnit> getEntities() {
        return getModel().getEntities();
    }

    public BossUnit getEntity(BossCycle.BOSS_TYPE type) {
        return getModel().getEntity(type);
    }

    public Set<BossCycle.BOSS_TYPE> getEntitiesSet() {
        return getModel().getEntitiesSet();
    }
    public void init() {

    }

    public void afterInit() {
    }

    public void battleStarted() {

    }
}
