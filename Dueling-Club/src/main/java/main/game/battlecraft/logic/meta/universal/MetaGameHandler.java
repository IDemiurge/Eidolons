package main.game.battlecraft.logic.meta.universal;

import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/10/2017.
 */
public class MetaGameHandler<E extends MetaGame >  {
    protected MetaGameMaster<E > master;

    public MetaGameHandler(MetaGameMaster master) {
        this.master = master;
    }

    public PartyManager<E> getPartyManager() {
        return master.getPartyManager();
    }

    public String getData() {
        return master.getData();
    }

    public E getMetaGame() {
        return master.getMetaGame();
    }

    public DC_Game getGame() {
        return master.getGame();
    }

    public MetaInitializer getInitializer() {
        return master.getInitializer();
    }

    public ShopManager getShopManager() {
        return master.getShopManager();
    }

    public MetaDataManager getMetaDataManager() {
        return master.getMetaDataManager();
    }
}
