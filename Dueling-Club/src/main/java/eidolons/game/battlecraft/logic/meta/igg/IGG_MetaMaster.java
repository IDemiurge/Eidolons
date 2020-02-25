package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PROPS;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.death.IGG_DefeatHandler;
import eidolons.game.battlecraft.logic.meta.igg.event.IGG_EventHandler;
import eidolons.game.battlecraft.logic.meta.igg.soul.SoulforceMaster;
import eidolons.game.battlecraft.logic.meta.igg.story.IGG_TownMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.stage.ConfirmationPanel;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/*
custom party rules
save/load capabilities

briefing handling

Town/Quest specifics

Mission structure and flow
Stats and victory screen

override death rules
- create a special class for this!
defeatHandler...
 */
public class IGG_MetaMaster extends MetaGameMaster<IGG_Meta> {

    private final boolean boss;
    private final SoulforceMaster soulforceMaster;

    public IGG_MetaMaster(String data) {
        super(data);
        ObjType scenario = DataManager.getType(data, DC_TYPE.SCENARIOS);
        ObjType mission = DataManager.getType(data, DC_TYPE.MISSIONS);
        if (mission == null) {
            mission = DataManager.getType(scenario.getProperty(PROPS.SCENARIO_MISSIONS), DC_TYPE.MISSIONS);
            if (mission != null) {
                this.data = mission.getName();
            }
        }
        boss = data.equalsIgnoreCase(IGG_Demo.IGG_MISSION.FINALE.toString()) ||
                        data.toLowerCase().contains("boss "); //TODO igg demo fix

        soulforceMaster = new SoulforceMaster(this);


        eventHandler = new IGG_EventHandler(this);
    }

    public boolean isBoss() {
        return boss;
    }

    public SoulforceMaster getSoulforceMaster() {
        return soulforceMaster;
    }


    @Override
    public IGG_DefeatHandler getDefeatHandler() {
        return (IGG_DefeatHandler) super.getDefeatHandler();
    }

    @Override
    protected DC_Game createGame() {
        game = new IGG_Game(this) {
            @Override
            public boolean isBossFight() {
                return boss;
            }
        }; //TODO
        return game;
    }

    @Override
    protected DefeatHandler createDefeatHandler() {
        return new IGG_DefeatHandler(this);
    }

    @Override
    protected PartyManager<IGG_Meta> createPartyManager() {
        return new IGG_PartyManager(this);
    }

    @Override
    protected MetaDataManager createMetaDataManager() {
        if (EidolonsGame.BRIDGE)
            return new IGG_MetaDataManager(this);
        return new ScenarioMetaDataManager(this);
    }

    @Override
    protected MetaInitializer<IGG_Meta> createMetaInitializer() {
        return new IGG_MetaInitializer(this);
    }

    @Override
    public MetaDataManager getMetaDataManager() {
        return  super.getMetaDataManager();
    }

    public void next(Boolean outcome) {
        super.next(outcome);
        String mission = getMetaDataManager().nextMission();
//        IGG_Demo.nextMission();
//        checkNewAct(getMetaGame().getActIndex());
//        if (actIndex!=newAct){
//
//        }
        boolean restart = false;
//        if (outcome == null) {
//            restart = true;
//        }
//        if (outcome != null)
//            if (outcome) {
//                if (getMetaGame().isFinalLevel()) {
//                    getBattleMaster().getOutcomeManager().victory();
//                    return;
//                }
//
//                super.next(outcome);
//                ScenarioLauncher.missionIndex++;
//            }
//        getMetaDataManager().setMissionName(null);
//        getMetaDataManager().initMissionName();
        ScreenData data = new ScreenData(SCREEN_TYPE.BATTLE, mission);
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
//        GuiEventManager.trigger(GuiEventType.DISPOSE_TEXTURES);

        if (restart) {
            Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(true);
            Eidolons.setParty(null);
        } else {
            Eidolons.setParty(getPartyManager().getParty());
        }
        if (!Eidolons.initScenario(
                new IGG_MetaMaster(mission))) {
            return;
        }
        ConfirmationPanel.clearInstance();

        //TODO should not be necessary!
        Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(restart);
//        ?  Eidolons.mainGame.getMetaMaster(). init();
        Eidolons.mainGame.getMetaMaster().getGame().getDungeonMaster().next();

        //TODO dangerous
        Eidolons.mainGame.getMetaMaster().getGame().initMasters();


        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
        Eidolons.mainGame.getMetaMaster().getGame().battleInit();
        Eidolons.mainGame.getMetaMaster().getGame().start(restart);


        GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO);
        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, Eidolons.getMainHero());
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
    }

    @Override
    protected TownMaster createTownMaster() {
        return new IGG_TownMaster(this);
    }

    @Override
    protected boolean isTownEnabled() {
        if (getMetaGame().getMission().isTown())
            return true;
        return false;
    }

    public boolean isCustomQuestsEnabled() {
        return true;
    }

    @Override
    public boolean isRngQuestsEnabled() {
        return false;
    }

    @Override
    public void preStart() {
        partyManager.preStart();
        partyManager.initPlayerParty();
        getMetaDataManager().initData();
//        initQuests();
    }

    private void initQuests() {
        getQuestMaster().initQuests();
    }

    @Override
    public IGG_PartyManager getPartyManager() {
        return (IGG_PartyManager) super.getPartyManager();
    }

    @Override
    public void gameStarted() {
        super.gameStarted();
    }

    @Override
    public void gameExited() {
        super.gameExited();
    }
}
