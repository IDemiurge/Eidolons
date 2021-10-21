package eidolons.game.battlecraft.logic.meta.testenv;

import eidolons.entity.obj.unit.Unit;
import eidolons.entity.obj.unit.netherflame.TrueForm;
import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.battlecraft.logic.meta.universal.SpawnManager;
import eidolons.game.core.launch.TestLaunch;
import eidolons.game.eidolon.chain.EidolonChain;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

public class TestSpawner extends SpawnManager {

    private EidolonChain chain;
    private TrueForm hero;

    public TestSpawner(TestEnvMGM testEnvMGM) {
        super(testEnvMGM);
    }

    @Override
    public void preStart() {
        TestLaunch launch = EngineLauncher.getInstance().getCustomLaunch();
        String chainType = launch.getValue(TestLaunch.TestValue.chain);
        String heroType =launch.getValue(TestLaunch.TestValue.hero);
        // String encounterType =launch.getValue(TestLaunch.TestValue.encounter);

        hero = createMainHero(heroType);
        // ObjType type = DataManager.getType(chainType, DC_TYPE.PARTY);
        // chain = new EidolonChain(hero, type);
        // getMetaDataManager().getData();
        super.preStart();
    }

    private TrueForm createMainHero(String heroType) {
        ObjType type = DataManager.getType(heroType, DC_TYPE.CHARS);
        return new TrueForm(type);
    }

    @Override
    public void gameStarted() {
        super.gameStarted();
    }

    @Override
    protected Unit findMainHero() {
        return hero;
    }
}
