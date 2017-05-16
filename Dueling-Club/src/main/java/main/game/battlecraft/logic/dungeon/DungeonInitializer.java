package main.game.battlecraft.logic.dungeon;

import main.content.DC_TYPE;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;
import main.system.entity.FilterMaster;

import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public abstract class DungeonInitializer<E extends DungeonWrapper> extends DungeonHandler<E> {

    public static final String RANDOM_DUNGEON = "random";
    protected String presetDungeonType;
    private String dungeonPath;

    public static boolean RANDOM = false;
    public DungeonInitializer(DungeonMaster<E> master) {
        super(master);
    }


    public static DungeonData generateDungeonData(String dataString) {
        String formatted="";
        DungeonData.
            DUNGEON_VALUE value;
            if (dataString.contains(".xml")) {
                value = DUNGEON_VALUE.PATH;
            } else
                value = DUNGEON_VALUE.TYPE_NAME;
            formatted = value + "=" + dataString+",";


        return new DungeonData(formatted);
    }

    @Deprecated
    public E initDungeon(String path) {
        setDungeonPath(path);
        return (E) getBuilder().buildDungeon(path );
    }

    public  E initDungeon(){
        setDungeonPath(getGame().getDataKeeper().getDungeonData().getValue(DUNGEON_VALUE.PATH));
        setPresetDungeonType(getGame().getDataKeeper().getDungeonData().getValue(DUNGEON_VALUE.TYPE_NAME));

        if (getDungeonPath() != null) {
            return (E) getBuilder().buildDungeon(getDungeonPath()
             );
        }

        ObjType type = null;
        if (getPresetDungeonType()!=null ) {
            type = DataManager.getType(getPresetDungeonType(), DC_TYPE.DUNGEONS);
            return createDungeon(type);
        } else {
            if (RANDOM ) {
                type =
                 pickRandomDungeon();
                return createDungeon(type);
            } else if (type == null) {
//                    type = DataManager.getType(ListChooser.chooseType(DC_TYPE.DUNGEONS));
//                }
//                if (type == null) {
                return initDungeonLevelChoice();
            }
        }

        throw new RuntimeException();
    }
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


public String getPresetDungeonType() {
        return presetDungeonType;
    }
    public void setPresetDungeonType(String presetDungeonType) {
        this.presetDungeonType = presetDungeonType;
    }
}
