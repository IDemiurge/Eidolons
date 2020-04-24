package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleMaster;
import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.netherflame.igg.death.IGG_DefeatHandler;
import eidolons.libgdx.launch.ScenarioLauncher;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/13/2017.
 */
public class ScenarioMetaMaster extends MetaGameMaster<ScenarioMeta> {

    public ScenarioMetaMaster(String data) {
        super(data);
    }

    @Override
    public ModuleMaster getModuleMaster() {
        return getDungeonMaster().getModuleMaster();
    }

    @Override
    public void preStart() {
        getMetaDataManager().initData();
        super.preStart();
    }

    @Override
    public void gameExited() {
        super.gameExited();
    }

    @Override
    public void next(Boolean outcome) {
        boolean restart = false;
        if (outcome == null) {
            restart = true;
        }
        if (outcome != null)
            if (outcome) {
                if (getMetaGame().isFinalLevel()) {
                    getBattleMaster().getOutcomeManager().victory();
                    return;
                }

                super.next(outcome);
                ScenarioLauncher.missionIndex++;

            }
        getMetaDataManager().setMissionName(null);
        getMetaDataManager().initData();
        ScreenData data = new ScreenData(SCREEN_TYPE.BATTLE, getMissionName());
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
        GuiEventManager.trigger(GuiEventType.DISPOSE_TEXTURES );

        if (restart) {
            Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(true);
            Eidolons.setParty(null);
        } else {
            Eidolons.setParty(getPartyManager().getParty());
        }
        if (!Eidolons.initScenario(
         new ScenarioMetaMaster(getData()))) {
            return;
        }

        //TODO should not be necessary!
        Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(restart);
//        ?  Eidolons.mainGame.getMetaMaster(). init();
        Eidolons.mainGame.getMetaMaster().getGame().getDungeonMaster().next();
        Eidolons.mainGame.getMetaMaster().getGame().battleInit();
        Eidolons.mainGame.getMetaMaster().getGame().start(restart);


        GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO);
        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, Eidolons.getMainHero());
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);

//        GuiEventManager.trigger(GuiEventType.SCREEN_LOADED);

    }

    @Override
    protected DefeatHandler createDefeatHandler() {
        return new IGG_DefeatHandler(this);
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

    protected String getScenarioInfo() {
        return getMetaGame().getMission().getName();
    }
}
