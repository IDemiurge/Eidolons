package main.game.battlecraft.logic.meta.scenario;

import main.game.battlecraft.logic.meta.MetaGame;
import main.game.battlecraft.logic.meta.MetaGameMaster;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ScenarioMeta extends MetaGame {
    private   Scenario scenario;

    public ScenarioMeta(Scenario scenario, MetaGameMaster<ScenarioMeta> master ) {
        super(master );
        this.scenario = scenario;
    }

    /*
    number of mission

    save/load data


     */

    public Scenario getScenario() {
        return scenario;
    }



}
