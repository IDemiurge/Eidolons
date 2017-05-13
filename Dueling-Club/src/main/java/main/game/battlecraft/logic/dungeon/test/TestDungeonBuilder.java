package main.game.battlecraft.logic.dungeon.test;

import main.content.PARAMS;
import main.game.battlecraft.logic.dungeon.DungeonBuilder;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.DungeonWrapper;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestDungeonBuilder<E extends DungeonWrapper> extends DungeonBuilder<E> {
    public static final Integer testWidth = 21;
    public static final Integer testHeight = 15;
    public static final int BASE_WIDTH = 21;
    public static final int BASE_HEIGHT = 15;

    public TestDungeonBuilder(DungeonMaster master) {
        super(master);
    }


    public int getLevelWidth() {
        if (getDungeon() == null) {
            if (testWidth != null) {
                return testWidth;
            }
            return BASE_WIDTH;
        }
        if (getDungeon().getIntParam(PARAMS.BF_WIDTH) <= 0) {
            return BASE_WIDTH;
        }
        return getDungeon().getIntParam(PARAMS.BF_WIDTH);
    }

    public int getLevelHeight() {
        if (getDungeon() == null) {
            if (testHeight != null) {
                return testHeight;
            }
            return BASE_HEIGHT;
        }
        if (getDungeon().getIntParam(PARAMS.BF_HEIGHT) <= 0) {
            return BASE_HEIGHT;
        }
        return getDungeon().getIntParam(PARAMS.BF_HEIGHT);
    }
}
