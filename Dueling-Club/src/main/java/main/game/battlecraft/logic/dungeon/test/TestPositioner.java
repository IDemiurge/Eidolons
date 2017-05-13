package main.game.battlecraft.logic.dungeon.test;

import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.DungeonWrapper;
import main.game.battlecraft.logic.dungeon.Positioner;

/**
 * Created by JustMe on 5/10/2017.
 */
public class TestPositioner<E extends DungeonWrapper> extends Positioner<E> {
    public TestPositioner(DungeonMaster master) {
        super(master);
    }
}
