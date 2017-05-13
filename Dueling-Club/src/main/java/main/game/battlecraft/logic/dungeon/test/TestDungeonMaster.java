package main.game.battlecraft.logic.dungeon.test;

import main.game.battlecraft.logic.dungeon.*;
import main.game.core.game.DC_Game;

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
