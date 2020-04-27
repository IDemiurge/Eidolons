package eidolons.game.netherflame.igg;

import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.mission.quest.QuestMissionMaster;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.ScenarioGame;

public class IGG_Game extends DC_Game {
    public IGG_Game(IGG_MetaMaster metaGameMaster) {
        super(false, false);
        metaMaster = metaGameMaster;
        setGameMode(GAME_MODES.DUNGEON_CRAWL);
        firstInit();
    }

    @Override
    public QuestMissionMaster getMissionMaster() {
        return (QuestMissionMaster) super.getMissionMaster();
    }

    @Override
    public IGG_MetaMaster getMetaMaster() {
        return (IGG_MetaMaster) super.getMetaMaster();
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
