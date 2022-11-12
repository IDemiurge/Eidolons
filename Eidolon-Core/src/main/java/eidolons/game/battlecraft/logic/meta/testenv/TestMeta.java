package eidolons.game.battlecraft.logic.meta.testenv;

import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

public class TestMeta extends MetaGame<TestMeta> {

    String trueForm;
    String chain;
    String dungeonName;
    String encounter;
    // TestMods mods; //flags etc

    public TestMeta(MetaGameMaster<TestMeta> master) {
        super(master);
    }


}
