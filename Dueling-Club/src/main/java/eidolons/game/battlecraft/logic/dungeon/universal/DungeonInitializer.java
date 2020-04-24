package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.igg.CustomLaunch;
import eidolons.libgdx.launch.MainLauncher;
import main.content.DC_TYPE;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
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
    public static boolean RANDOM = false;
    protected String presetDungeonType;
    private String dungeonPath;

    public DungeonInitializer(DungeonMaster<E> master) {
        super(master);
    }


    public static DungeonData generateDungeonData(String dataString) {
        String formatted = "";
        DungeonData.
         DUNGEON_VALUE value;
        if (dataString.contains(".xml")) {
            value = DUNGEON_VALUE.PATH;
        } else
            value = DUNGEON_VALUE.TYPE_NAME;
        formatted = value + "=" + dataString + ",";


        return new DungeonData(formatted);
    }

    public E initDungeon() {
        String data =
         Eidolons.getGame().getMetaMaster().getMetaDataManager().getMissionPath();
        if (data == null) {
            if (MainLauncher.getCustomLaunch()!=null ){
                main.system.auxiliary.log.LogMaster.important("*******Custom Launch xml path: " +
                        MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path));
                data= MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path);
            }
        }
        if (data!=null )
            setDungeonPath(data);
        else
            setDungeonPath(getGame().getDataKeeper().getDungeonData().getValue(DUNGEON_VALUE.PATH));

        setPresetDungeonType(getGame().getDataKeeper().getDungeonData().getValue(DUNGEON_VALUE.TYPE_NAME));

        if (getDungeonPath() != null) {
            return (E) getBuilder().buildDungeon(getDungeonPath()
            );
        }

        ObjType type = null;
        if (getPresetDungeonType() != null) {
            type = DataManager.getType(getPresetDungeonType(), DC_TYPE.DUNGEONS);
            return createDungeon(type);
        } else {
            if (RANDOM) {
                type =
                 pickRandomDungeon();
                return createDungeon(type);
            } else if (type == null) {
                return initDungeonLevelChoice();
            }
        }

        throw new RuntimeException();
    }

    public abstract E createDungeon(ObjType type);


    protected ObjType pickRandomDungeon() {
        ObjType type;
        List<ObjType> list = DataManager.getTypes(DC_TYPE.DUNGEONS);

        if (list.isEmpty()) {
            list = DataManager.getTypes(DC_TYPE.DUNGEONS);
            FilterMaster.filterByProp(list, G_PROPS.WORKSPACE_GROUP.getName(),
             MetaEnums.WORKSPACE_GROUP.FOCUS + "");
        }
        type = list.get(RandomWizard.getRandomIndex(list));
        return type;
    }

    public E initDungeonLevelChoice() {

        if (RANDOM) {
            return (E) getBuilder().buildDungeon(getRandomDungeonPath());
        }
        List<ObjType> types = DataManager.getTypes(DC_TYPE.DUNGEONS);
        ObjType type = ListChooser.chooseType(types);
        if (type != null) {
            return createDungeon(type);
        }

        return null;
    }

    protected String getRandomDungeonPath() {
        return FileManager.getRandomFile(
         FileManager.getFilesFromDirectory(PathFinder.getDungeonLevelFolder()
          + getDungeonLevelSubfolder(), false)).getPath();
    }

    //TODO different for each Type?
    protected String getDungeonLevelSubfolder() {
        return "battle/";
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
