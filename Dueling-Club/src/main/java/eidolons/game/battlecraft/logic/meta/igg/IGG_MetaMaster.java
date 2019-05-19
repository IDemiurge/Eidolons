package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.igg.death.IGG_DefeatHandler;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.igg.story.IGG_TownMaster;
import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.ScenarioGame;

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
    ShadowMaster shadowMaster= new ShadowMaster(this);

    public IGG_MetaMaster(String data) {
        super(data);
        boss = data.equalsIgnoreCase(IGG_Demo.IGG_MISSION.FINALE.toString());
    }

    public ShadowMaster getShadowMaster() {
        return shadowMaster;
    }

    @Override
    public IGG_DefeatHandler getDefeatHandler() {
        return (IGG_DefeatHandler) super.getDefeatHandler();
    }

    @Override
    protected DC_Game createGame() {
        game= new IGG_Game(this){
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
    protected MetaDataManager<IGG_Meta> createMetaDataManager() {
        return new IGG_MetaDataManager(this);
    }

    @Override
    protected MetaInitializer<IGG_Meta> createMetaInitializer() {
        return new IGG_MetaInitializer(this);
    }


    public void next(Boolean outcome) {
        super.next(outcome);

//        IGG_Demo.nextMission();
//        checkNewAct(getMetaGame().getActIndex());
//        if (actIndex!=newAct){
//
//        }


        //epilogue
        //

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
        return  true;
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
