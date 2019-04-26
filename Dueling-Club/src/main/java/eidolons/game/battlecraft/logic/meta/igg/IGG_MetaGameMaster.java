package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.igg.death.IGG_DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.game.DC_Game;

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
public class IGG_MetaGameMaster extends  MetaGameMaster<IGG_Meta> {
    public IGG_MetaGameMaster(String data) {
        super(data);
    }

    @Override
    protected DC_Game createGame() {
        return null ; //TODO
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

    @Override
    public void next(Boolean outcome) {
        super.next(outcome);

        //epilogue
        //

    }
}
