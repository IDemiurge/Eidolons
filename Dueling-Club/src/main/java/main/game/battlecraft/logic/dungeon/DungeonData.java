package main.game.battlecraft.logic.dungeon;

import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonData extends DataUnit<DUNGEON_VALUE> {
    private static final Boolean FORMAT =false ;

    public enum DUNGEON_VALUE{
        TYPE_NAME,
        PATH,
        RANDOM,
        FILTER_VALUE_NAME,
        FILTER_VALUE,
        WORKSPACE_FILTER,
    }

    @Override
    public Boolean getFormat() {
        return FORMAT;
    }

    public DungeonData() {

    }
    public DungeonData(String data) {
        super(data);
    }
}
