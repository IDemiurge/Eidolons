package main.game.core.game;

import main.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.dungeon.location.LocationMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;

/**
 * Created by JustMe on 5/10/2017.
 */
public class ScenarioGame extends DC_Game {

    public ScenarioGame(ScenarioMetaMaster scenarioMetaMaster) {
        super(true);
        metaMaster=scenarioMetaMaster;
    }

    @Override
    public void start(boolean first) {
        simulation=false;
        super.start(first);
    }

    @Override
    protected DungeonMaster createDungeonMaster() {
        return new LocationMaster(this);
    }

    @Override
    protected BattleMaster createBattleMaster() {
        return new MissionBattleMaster(this);
    }

    @Override
    public LocationMaster getDungeonMaster() {
        return (LocationMaster) super.getDungeonMaster();
    }
}
