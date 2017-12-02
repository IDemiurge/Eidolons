package main.game.battlecraft.logic.meta.scenario;

import main.entity.DataModel;
import main.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import main.game.battlecraft.logic.dungeon.location.LocationMaster;
import main.game.battlecraft.logic.meta.scenario.hq.HqShopManager;
import main.game.battlecraft.logic.meta.universal.*;
import main.game.core.Eidolons;
import main.game.core.game.ScenarioGame;
import main.libgdx.launch.ScenarioLauncher;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/13/2017.
 */
public class ScenarioMetaMaster extends MetaGameMaster<ScenarioMeta> {

    ScenarioData data;

    public ScenarioMetaMaster(String data) {
        super(data);

    }

    /*
        on clicking a mission...
        full loading 
        > create dungeon(s)
        > create party units (maybe  place them at once)
         */
    @Override
    public void preStart() {
        getMetaDataManager().initMissionName();
//        String levelPath = DataManager.getType(getMissionName(), DC_TYPE.MISSIONS).
//         getProperty(PROPS.MISSION_FILE_PATH);
//        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
//         levelPath);

        super.preStart();
    }

    @Override
    public void gameExited() {
        ScenarioPartyManager.setSelectedHero(null);
        super.gameExited();
    }

    @Override
    public void next(Boolean outcome) {
        boolean restart = false;
        if (outcome == null) {
            restart = true;
        }
        super.next(outcome);
        if (outcome != null)
            if (outcome) {
                if (getMetaGame().isFinalLevel()) {
                    getBattleMaster().getOutcomeManager().victory();
                    return;
                }

                ScenarioLauncher.missionIndex++;

            }
//        if (ScenarioLauncher.missionIndex >= 6) {
//            ScreenData data = new ScreenData(ScreenType.BATTLE, getMissionName(),
//             new SceneFactory("Test"));
//            GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
//            return;
//        }
//   TODO       getDialogueManager().getDialogueForMission(getMissionName());
        getMetaDataManager().setMissionName(null);
        getMetaDataManager().initMissionName();
        ScreenData data = new ScreenData(ScreenType.BATTLE, getMissionName());
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        if (restart)
            Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(true);
        if (!Eidolons.initScenario(
         new ScenarioMetaMaster(getData()))) {
            return;
        }

//        if (restart){
//            Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(true);
//             Eidolons.mainGame.getMetaMaster(). init();
//        }
        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
        Eidolons.mainGame.getMetaMaster().getGame().battleInit();
        Eidolons.mainGame.getMetaMaster().getGame().start(true);
    }

    @Override
    public DataModel getEntity() {
        return getMetaGame().getScenario();
    }

    public void chosenMission(String data) {
        getMetaDataManager().setMissionName(data);
    }

    public void loadMission(String data) {
        getGame().getDungeonMaster().init(); //create dungeon

        getGame().getBattleMaster().init(); //create Mission
        //game.load()
        //precombat?
        getGame().start(true); //game loop??
        getMetaGame().getScenario().next();
//        getPartyManager().getParty().getIntParam(mission_index);
//        Mission mission= (Mission) getGame().getBattleMaster().getBattle();
//        getMetaGame().getScenario().setMission(mission);
        //for Mission
//        getDialogueManager().startDialogue(); //block game?

    }

    @Override
    protected ScenarioGame createGame() {
        return new ScenarioGame(this);
    }

    @Override
    protected PartyManager<ScenarioMeta> createPartyManager() {
        return new ScenarioPartyManager(this);
    }

    @Override
    protected MetaDataManager<ScenarioMeta> createMetaDataManager() {
        return new ScenarioMetaDataManager(this);
    }

    @Override
    protected ShopManager<ScenarioMeta> createShopManager() {
        return new HqShopManager(this);
    }

    @Override
    protected MetaInitializer<ScenarioMeta> createMetaInitializer() {
        return new ScenarioInitializer(this);
    }

    @Override
    public ScenarioMetaDataManager getMetaDataManager() {
        return (ScenarioMetaDataManager) super.getMetaDataManager();
    }

    @Override
    public MissionBattleMaster getBattleMaster() {
        return (MissionBattleMaster) super.getBattleMaster();
    }

    @Override
    public LocationMaster getDungeonMaster() {
        return (LocationMaster) super.getDungeonMaster();
    }

    public String getMissionName() {
        if (getMetaDataManager().getMissionName() != null)
            return getMetaDataManager().getMissionName();
        return getPartyManager().getParty().getMission();
    }
}
