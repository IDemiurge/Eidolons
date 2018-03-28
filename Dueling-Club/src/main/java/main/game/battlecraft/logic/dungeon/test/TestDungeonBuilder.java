package main.game.battlecraft.logic.dungeon.test;

import main.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonWrapper;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestDungeonBuilder<E extends DungeonWrapper> extends DungeonBuilder<E> {
    public static final int BASE_WIDTH = 15;
    public static final int BASE_HEIGHT = 11;

    public TestDungeonBuilder(DungeonMaster master) {
        super(master);
    }

    public int getDefaultHeight() {
        return BASE_HEIGHT;
    }

    public int getDefaultWidth() {
        return BASE_WIDTH;
    }
}
