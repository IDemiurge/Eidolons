package main.game.battlecraft.logic.dungeon;

import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;
import main.system.net.data.DataUnit;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonData extends DataUnit<DUNGEON_VALUE> {
    public enum DUNGEON_VALUE{
        TYPE_NAME,
        PATH,
        RANDOM,
        FILTER_VALUE_NAME,
        FILTER_VALUE,
        WORKSPACE_FILTER,
    }

    public DungeonData() {

    }
    public DungeonData(String data) {
        super(data);
    }
}
