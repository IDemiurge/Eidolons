package eidolons.game.core.game;

import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.mission.quest.QuestMissionMaster;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;

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
    public QuestMissionMaster getMissionMaster() {
        return (QuestMissionMaster) super.getMissionMaster();
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
    protected MissionMaster createBattleMaster() {
        return new QuestMissionMaster(this);
    }

    @Override
    public LocationMaster getDungeonMaster() {
        return (LocationMaster) super.getDungeonMaster();
    }
}
