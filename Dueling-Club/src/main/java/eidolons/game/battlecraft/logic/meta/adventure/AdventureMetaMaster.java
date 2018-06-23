package eidolons.game.battlecraft.logic.meta.adventure;

import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 2/7/2018.
 */
public class AdventureMetaMaster extends MetaGameMaster<AdventureMeta> {
    public AdventureMetaMaster(String data) {
        super(data);
    }

    @Override
    protected DC_Game createGame() {
        return null;
    }

    @Override
    protected PartyManager<AdventureMeta> createPartyManager() {
        return null;
    }

    @Override
    protected MetaDataManager<AdventureMeta> createMetaDataManager() {
        return null;
    }

    @Override
    protected ShopManager<AdventureMeta> createShopManager() {
        return null;
    }

    @Override
    protected MetaInitializer<AdventureMeta> createMetaInitializer() {
        return null;
    }
}
