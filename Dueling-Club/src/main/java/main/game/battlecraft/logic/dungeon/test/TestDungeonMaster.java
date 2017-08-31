package main.game.battlecraft.logic.dungeon.test;

import main.game.battlecraft.logic.dungeon.location.LocationBuilder;
import main.game.battlecraft.logic.dungeon.universal.*;
import main.game.core.game.DC_Game;
import main.test.frontend.FAST_DC;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestDungeonMaster extends DungeonMaster<TestDungeon> {

    public TestDungeonMaster(DC_Game game) {
        super(game);
//        setChooseLevel(CHOOSE_LEVEL);
//        // setDungeonPath(DEFAULT_DUNGEON_LEVEL);
//        presetDungeonType = getDEFAULT_DUNGEON();
//        dungeonPath = DEFAULT_DUNGEON_PATH;
    }

    @Override
    protected DungeonBuilder createBuilder() {
        if (isLocation())
            return new LocationBuilder(this);
        return new TestDungeonBuilder<>(this);
    }

    private boolean isLocation() {
        return !FAST_DC.TEST_MODE;
    }

    @Override
    protected FacingAdjuster<TestDungeon> createFacingAdjuster() {
        return new FacingAdjuster<>(this);
    }

    @Override
    protected Positioner createPositioner() {
        return new TestPositioner(this);
    }

    @Override
    protected Spawner createSpawner() {
        return new TestSpawner(this);
    }

    @Override
    protected DungeonInitializer<TestDungeon> createInitializer() {
        return new TestDungeonInitializer(this);
    }
}
