package main.libgdx.gui.panels.headquarters.datasource;

import main.game.battlecraft.logic.battle.mission.Mission;
import main.game.battlecraft.logic.meta.scenario.Scenario;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/2/2017.
 */
public class AvailableMissionsDataSource {
    Scenario scenario;
ScenarioMetaMaster master;
    public AvailableMissionsDataSource(Scenario scenario) {
        this.scenario = scenario;
    }

    MissionDataSource getCurrentData() {
        return new MissionDataSourceImpl(
         master.getBattleMaster().getBattle().getMission());
    }

    public List<MissionDataSource> getData() {
        List<Mission> availableMissions =   scenario.getAvailableMissions();
        return availableMissions.stream().map(mission -> new MissionDataSourceImpl(mission)).collect(Collectors.toList());
    }
}
