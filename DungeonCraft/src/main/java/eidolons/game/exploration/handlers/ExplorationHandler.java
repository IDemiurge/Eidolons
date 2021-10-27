package eidolons.game.exploration.handlers;

import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationHandler {
    protected ExplorationMaster master;
    protected float timer;

    public ExplorationHandler(ExplorationMaster master) {
        this.master = master;
    }

    public void act(float delta) {
        if (getTimerPeriod() > 0) {
            timer += delta;
            if (timer >= getTimerPeriod()) {
                timer = 0;
                timerEvent();
            }
        }
    }

    protected void timerEvent() {

    }

    protected float getTimerPeriod() {
        return 0;
    }


    protected boolean isCombat() {
        return master.isInCombat();
    }

    public ExplorePartyMaster getPartyMaster() {
        return master.getPartyMaster();
    }

    public ExploreEnemyPartyMaster getEnemyPartyMaster() {
        return master.getEnemyPartyMaster();
    }

    public DC_Game getGame() {
        return master.getGame();
    }

    public ExplorationAiMaster getAiMaster() {
        return master.getAiMaster();
    }

    public ExplorationTimeMaster getTimeMaster() {
        return master.getTimeMaster();
    }

    public ExplorationResetHandler getResetter() {
        return master.getResetter();
    }

    public ExploreCleaner getCleaner() {
        return master.getCleaner();
    }

    public ExplorationActionHandler getActionHandler() {
        return master.getActionHandler();
    }

    public ExploreGameLoop getLoop() {
        return master.getLoop();
    }

}
