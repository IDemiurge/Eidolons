package main.game.battlecraft.logic.dungeon;

import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;

/**
 * Created by JustMe on 5/8/2017.
 */
public abstract class DungeonInitializer<E extends DungeonWrapper> extends DungeonHandler<E> {

    public static final String RANDOM_DUNGEON = "random";
    private String dungeonPath;

    public DungeonInitializer(DungeonMaster<E> master) {
        super(master);
    }


    public static DungeonData generateDungeonData(String dataString) {
        String formatted="";
        if (dataString.contains(";")){
            DUNGEON_VALUE value;
            if (dataString.contains(".xml")){
                value = DUNGEON_VALUE.PATH;
            } else
                value = DUNGEON_VALUE.TYPE_NAME;
            formatted = value + "=" + dataString;
        }
        return new DungeonData(formatted);
    }
@Deprecated
    public E initDungeon(String path) {
        setDungeonPath(path);
        return (E) getBuilder().buildDungeon(path);
    }

    public abstract E initDungeon();
    public abstract E createDungeon(ObjType type);
    //TODO different for each Type?
    protected String getDungeonLevelSubfolder() {
        return "battle\\";
    }


    public String getDungeonPath() {
        return dungeonPath;
    }

    public void setDungeonPath(String dungeonPath) {
        this.dungeonPath = dungeonPath;
    }



}
