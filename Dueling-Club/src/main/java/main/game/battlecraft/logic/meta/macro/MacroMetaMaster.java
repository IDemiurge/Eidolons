package main.game.battlecraft.logic.meta.macro;

import main.game.battlecraft.logic.meta.universal.*;
import main.game.core.game.DC_Game;

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
