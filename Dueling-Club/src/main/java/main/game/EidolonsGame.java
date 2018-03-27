package main.game;

import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;

/**
 * Created by JustMe on 5/13/2017.
 */
public class EidolonsGame {
    // create on any launch!

    DC_Engine engine;
    private ScenarioMetaMaster metaMaster;
    private boolean aborted;

    public void setMetaMaster(ScenarioMetaMaster metaMaster) {
        this.metaMaster = metaMaster;
    }

    public ScenarioMetaMaster getMetaMaster() {
        return metaMaster;
    }

    public void init() {
        metaMaster.init();
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        if (aborted) main.system.auxiliary.log.LogMaster.log
         (1, "game aborted!!!!!!");
        this.aborted = aborted;
    }
    // config for engine
    // ++ audiosystem, maybe some gdx interface?

}
