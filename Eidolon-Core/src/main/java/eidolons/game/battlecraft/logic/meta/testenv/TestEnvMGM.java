package eidolons.game.battlecraft.logic.meta.testenv;

import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.SpawnManager;
import eidolons.game.core.game.DC_Game;

/*
use the old way for test_env - static values or maybe from swing ui choices

deeper in:
- launchButtonClicked: how initial data string is picked, which screen is switched (maybe we can have
some variability, e.g. waitInput and loading tips

 */
public class TestEnvMGM extends MetaGameMaster<TestMeta> {

    public TestEnvMGM(String data) {
        super(data);
    }

    @Override
    protected SpawnManager createSpawnManager() {
        return new TestSpawner(this);
    }

    @Override
    public void preStart() {
        super.preStart();
    }

    @Override
    protected DC_Game createGame() {
        // return new TestGame(this);
        return new DC_Game(false);
    }

    @Override
    protected MetaDataManager createMetaDataManager() {
        return null;
    }

    @Override
    protected MetaInitializer createMetaInitializer() {
        return new TestMetaSetupper(this);
    }
}
