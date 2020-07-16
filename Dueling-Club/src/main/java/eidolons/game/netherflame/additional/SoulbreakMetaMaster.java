package eidolons.game.netherflame.additional;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.main.death.NF_DefeatHandler;
import eidolons.game.netherflame.main.event.NF_EventHandler;
import eidolons.game.netherflame.main.soul.SoulforceMaster;
import eidolons.game.netherflame.main.story.IGG_TownMaster;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.stage.ConfirmationPanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/*
could be useful later
maybe for soulbreak ?
 */
public class SoulbreakMetaMaster extends  MetaGameMaster<LinearMeta> {

    private final SoulforceMaster soulforceMaster;

    public SoulbreakMetaMaster(String data) {
        super(data);
        soulforceMaster = new SoulforceMaster(this);
        eventHandler = new NF_EventHandler(this);
    }

    @Override
    protected DC_Game createGame() {
        return null;
    }

    @Override
    protected PartyManager createPartyManager() {
        return null;
    }

    public SoulforceMaster getSoulforceMaster() {
        return soulforceMaster;
    }

    @Override
    public NF_DefeatHandler getDefeatHandler() {
        return (NF_DefeatHandler) super.getDefeatHandler();
    }


    @Override
    protected DefeatHandler createDefeatHandler() {
        return new NF_DefeatHandler(this);
    }


    @Override
    protected MetaDataManager createMetaDataManager() {
        // if (EidolonsGame.BRIDGE)
        //     return new IGG_MetaDataManager(this);
        return new ScenarioMetaDataManager(this);
    }

    @Override
    protected MetaInitializer<LinearMeta> createMetaInitializer() {
        return null;
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
        ScreenData data = new ScreenData(SCREEN_TYPE.DUNGEON, mission);
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
//        GuiEventManager.trigger(GuiEventType.DISPOSE_TEXTURES);

        if (restart) {
            Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(true);
            Eidolons.setParty(null);
        } else {
            Eidolons.setParty(getPartyManager().getParty());
        }
        if (!Eidolons.initScenario(
                new SoulbreakMetaMaster(mission))) {
            return;
        }
        ConfirmationPanel.clearInstance();

        //TODO should not be necessary!
        Eidolons.mainGame.getMetaMaster().getMetaGame().setRestarted(restart);
//        ?  Eidolons.mainGame.getMetaMaster(). init();
        Eidolons.mainGame.getMetaMaster().getGame().getDungeonMaster().next();

        //TODO dangerous
        Eidolons.mainGame.getMetaMaster().getGame().initMasters();


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

}
