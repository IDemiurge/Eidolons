package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.igg.death.IGG_DefeatHandler;
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
    public IGG_MetaMaster(String data) {
        super(data);
    }

    @Override
    protected DC_Game createGame() {
        return new IGG_Game(this); //TODO
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
    protected boolean isTownEnabled() {
        if (getMetaGame().getMission().isTown())
            return true;
        return false;
    }

    @Override
    public boolean isQuestsEnabled() {
        return false;
    }

    @Override
    public void preStart() {
        partyManager.preStart();
        partyManager.initPlayerParty();
        getMetaDataManager().initData();
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
