package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/8/2017.
 */
public abstract class DungeonInitializer extends DungeonHandler {

    private String dungeonPath;

    public DungeonInitializer(DungeonMaster master) {
        super(master);
    }

    public static DungeonData generateDungeonData(String dataString) {
        String formatted = "";
        DungeonData.DUNGEON_VALUE value;
        if (dataString.contains(".xml")) {
            value = DUNGEON_VALUE.PATH;
        } else
            value = DUNGEON_VALUE.TYPE_NAME;
        formatted = value + "=" + dataString + ",";

        return new DungeonData(formatted);
    }

    public abstract Location initDungeon();

    public abstract Location createDungeon(ObjType type);

    public String getDungeonPath() {
        return dungeonPath;
    }

    public void setDungeonPath(String dungeonPath) {
        this.dungeonPath = dungeonPath;
    }

}
