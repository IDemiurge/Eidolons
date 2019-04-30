package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;

public class IGG_MetaInitializer extends MetaInitializer<IGG_Meta> {
    public IGG_MetaInitializer(IGG_MetaMaster master) {
        super(master);
    }

    @Override
    public IGG_Meta initMetaGame(String data) {
        return new IGG_Meta( master);
    }
}
