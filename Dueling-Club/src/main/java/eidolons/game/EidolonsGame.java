package eidolons.game;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * Created by JustMe on 5/13/2017.
 */
public class EidolonsGame {
    // create on any launch!

    DC_Engine engine;
    private MetaGameMaster metaMaster;
    private boolean aborted;

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public void setMetaMaster(MetaGameMaster metaMaster) {
        this.metaMaster = metaMaster;
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
