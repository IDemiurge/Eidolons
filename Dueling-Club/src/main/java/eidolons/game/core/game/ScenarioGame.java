package eidolons.game.core.game;

import eidolons.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;

/**
 * Created by JustMe on 5/10/2017.
 */
public class ScenarioGame extends DC_Game {

    public ScenarioGame(ScenarioMetaMaster scenarioMetaMaster) {
        super(false, false);
        metaMaster = scenarioMetaMaster;
        setGameMode(GAME_MODES.DUNGEON_CRAWL);
        firstInit();
    }

    @Override
    public MissionBattleMaster getBattleMaster() {
        return (MissionBattleMaster) super.getBattleMaster();
    }

    @Override
    public ScenarioMetaMaster getMetaMaster() {
        return (ScenarioMetaMaster) super.getMetaMaster();
    }

    public static ScenarioGame getGame(){
        return (ScenarioGame) game;
    }

    @Override
    public void start(boolean first) {
        simulation = false;
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
