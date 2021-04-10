package eidolons.game.netherflame.main;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.eidolon.event.GameEventHandler;
import eidolons.game.netherflame.NF_Game;
import eidolons.game.netherflame.main.death.NF_DefeatHandler;
import eidolons.game.eidolon.event.NF_EventHandler;
import eidolons.game.netherflame.main.misc.SoloPartyManager;
import eidolons.game.netherflame.main.soul.SoulforceMaster;

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
public class NF_MetaMaster extends ScenarioMetaMaster<NF_Meta> {

    // private final boolean boss;
    private final SoulforceMaster soulforceMaster;

    public NF_MetaMaster(String data) {
        super(data);
        soulforceMaster = new SoulforceMaster(this);
        eventHandler = new NF_EventHandler(this);

    }
    protected GameEventHandler createEventHandler() {
        return new NF_EventHandler(this);
    }

    public SoulforceMaster getSoulforceMaster() {
        return soulforceMaster;
    }

    @Override
    public NF_DefeatHandler getDefeatHandler() {
        return (NF_DefeatHandler) super.getDefeatHandler();
    }

    @Override
    protected ScenarioGame createGame() {
        game = new NF_Game(this);
        return (NF_Game) game;
    }

    @Override
    protected MetaInitializer<ScenarioMeta> createMetaInitializer() {
        return new NF_MetaInitializer(this);
    }

    @Override
    protected DefeatHandler createDefeatHandler() {
        return new NF_DefeatHandler(this);
    }

    @Override
    protected PartyManager  createPartyManager() {
        if (isSoloLevel() || SoloPartyManager.TEST_MODE)
            return new SoloPartyManager(this);
        return new NF_PartyManager(this);
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
        // partyManager.initPlayerParty();
        getMetaDataManager().initData();
//        initQuests();
    }

    @Override
    public NF_PartyManager getPartyManager() {
        return (NF_PartyManager) super.getPartyManager();
    }

}
