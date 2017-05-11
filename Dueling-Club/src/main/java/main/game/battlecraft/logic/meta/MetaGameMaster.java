package main.game.battlecraft.logic.meta;

import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public class MetaGameMaster<E extends MetaGame> {

    PartyManager<E> partyManager;

    public MetaGameMaster(DC_Game dc_game) {

    }
//    ShopManager<E> shopManager;
//    AfterCombatManager<E> afterCombatManager;
//    MetaDataManager<E> metaDataManager;
//    PrecombatManager<E> precombatManager;

    public PartyManager<E> getPartyManager() {
        return partyManager;
    }
}
