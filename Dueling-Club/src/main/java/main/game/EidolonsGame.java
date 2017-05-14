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

    public void setMetaMaster(ScenarioMetaMaster metaMaster) {
        this.metaMaster = metaMaster;
    }

    public ScenarioMetaMaster getMetaMaster() {
        return metaMaster;
    }

    public void init() {
        metaMaster.init();
    }
    // config for engine
    // ++ audiosystem, maybe some gdx interface?

}
