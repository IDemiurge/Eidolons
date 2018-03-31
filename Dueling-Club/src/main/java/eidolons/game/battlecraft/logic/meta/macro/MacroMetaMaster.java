package eidolons.game.battlecraft.logic.meta.macro;

import eidolons.game.battlecraft.logic.meta.universal.*;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 2/7/2018.
 */
public class MacroMetaMaster extends MetaGameMaster<MacroMeta> {
    public MacroMetaMaster(String data) {
        super(data);
    }

    @Override
    protected DC_Game createGame() {
        return null;
    }

    @Override
    protected PartyManager<MacroMeta> createPartyManager() {
        return null;
    }

    @Override
    protected MetaDataManager<MacroMeta> createMetaDataManager() {
        return null;
    }

    @Override
    protected ShopManager<MacroMeta> createShopManager() {
        return null;
    }

    @Override
    protected MetaInitializer<MacroMeta> createMetaInitializer() {
        return null;
    }
}
