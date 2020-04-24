package main.level_editor.backend.sim;

import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;

public class LE_Initializer extends MetaInitializer {
    public LE_Initializer(MetaGameMaster master) {
        super(master);
    }

    @Override
    public MetaGame initMetaGame(String data) {
        return null;
    }
}
