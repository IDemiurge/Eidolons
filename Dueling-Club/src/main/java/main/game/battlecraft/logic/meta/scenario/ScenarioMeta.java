package main.game.battlecraft.logic.meta.scenario;

import main.game.battlecraft.logic.battle.mission.Mission;
import main.game.battlecraft.logic.meta.universal.MetaGame;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;

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

    @Override
    public ScenarioMetaMaster getMaster() {

        return (ScenarioMetaMaster) super.getMaster();
    }

    public Scenario getScenario() {
        return scenario;
    }


    public Mission getMission() {
        return getMaster().getBattleMaster().getBattle().getMission();
    }

}
