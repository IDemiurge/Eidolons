package main.game.battlecraft.logic.dungeon.test;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.VALUE;
import main.content.enums.DungeonEnums.DUNGEON_SUBFOLDER;
import main.content.enums.system.MetaEnums;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;
import main.game.battlecraft.logic.dungeon.DungeonInitializer;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.entity.FilterMaster;
import main.system.launch.CoreEngine;
import main.test.frontend.FAST_DC;

import java.io.File;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestDungeonInitializer extends DungeonInitializer<TestDungeon> {
    public static final String PRESET_PLAN = "Spire";
    static final VALUE[] encounterDungeonValues = {PROPS.DUNGEON_MAP_MODIFIER,
            PROPS.DUNGEON_MAP_TEMPLATE, PROPS.MAP_PRESET_OBJECTS, PROPS.MAP_OBJECTS,};
    private static final String DUNGEON_BACKGROUND_FOLDER = "\\big\\dungeons";
    private static final String RANDOM_DUNGEON_WORKSPACE_FILTER =
            MetaEnums.WORKSPACE_GROUP.FOCUS
                    + "" + MetaEnums.WORKSPACE_GROUP.COMPLETE;
    public static boolean RANDOM_DUNGEON = false;
    public static boolean CHOOSE_LEVEL = false;
    private static String DEFAULT_DUNGEON_LEVEL = "Forest.xml"; // "Arena.xml";
    private static String DEFAULT_DUNGEON = "Hidden Camp";// "Hidden Camp";//
    private static String DEFAULT_DUNGEON_PATH;
    private WORKSPACE_GROUP workspaceFilter;
    private String presetDungeonType;
    private boolean chooseLevel;

    public TestDungeonInitializer(DungeonMaster master) {
        super(master);
    }


    @Override
    public TestDungeon createDungeon(ObjType type) {
        return new TestDungeon(new Dungeon(type), master);
    }

    @Override
    public TestDungeon initDungeon() {
        setDungeonPath(getGame().getDataKeeper().getDungeonData().getValue(DUNGEON_VALUE.PATH));
        setPresetDungeonType(getGame().getDataKeeper().getDungeonData().getValue(DUNGEON_VALUE.TYPE_NAME));

        if (FAST_DC.getTestLauncher().getSUPER_FAST_MODE()) {
//        setDungeonPath(FAST_DC.DEFAULT_TEST_DUNGEON);
        }
        if (getDungeonPath() != null) {
            return (TestDungeon) getBuilder().buildDungeon(getDungeonPath());
        }

        if (CoreEngine.isArcaneVault()) {
            return createDummyDungeon();
        }
        ObjType type = null;
        if (!FAST_DC.isRunning()) {
            type = DataManager.getType(getPresetDungeonType(), DC_TYPE.DUNGEONS);
            return createDungeon(type);
        } else {
            if (RANDOM_DUNGEON) {
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

        return createDummyDungeon();
    }


    private TestDungeon createDummyDungeon() {
        return createDungeon(new ObjType("Test Dungeon", DC_TYPE.DUNGEONS));
    }

    private ObjType pickRandomDungeon() {
        ObjType type;
        List<ObjType> list = DataManager.getTypes(DC_TYPE.DUNGEONS);

        FilterMaster.filterByProp(list,
                G_PROPS.WORKSPACE_GROUP.getName(),
                RANDOM_DUNGEON_WORKSPACE_FILTER);
        if (list.isEmpty()) {
            list = DataManager.getTypes(DC_TYPE.DUNGEONS);
            FilterMaster.filterByProp(list, G_PROPS.WORKSPACE_GROUP.getName(),
                    MetaEnums.WORKSPACE_GROUP.FOCUS + "");
        }
        type = list.get(RandomWizard.getRandomListIndex(list));
        return type;
    }

    public String getPresetDungeonType() {
        if (presetDungeonType == null) {
            return DEFAULT_DUNGEON;
        }
        return presetDungeonType;
    }

    public void setPresetDungeonType(String presetDungeonType) {
        this.presetDungeonType = presetDungeonType;
    }

    public TestDungeon initDungeonLevelChoice() {

        if (RANDOM_DUNGEON) {
            return (TestDungeon) getBuilder().buildDungeon(getRandomDungeonPath());
        }
        List<ObjType> types = DataManager.getTypes(DC_TYPE.DUNGEONS);
        if (workspaceFilter != null) {
            FilterMaster.filterByPropJ8(types, G_PROPS.WORKSPACE_GROUP.getName(), workspaceFilter.toString());
        }
        ObjType type = ListChooser.chooseType(types);
        if (type != null) {
            return createDungeon(type);
        }
        if (getDungeonPath() == null) {
            setDungeonPath(chooseDungeonLevel());
        }
        if (getDungeonPath() != null) {
            return (TestDungeon) getBuilder().buildDungeon(getDungeonPath());
        }
        return null;
    }

    private String getRandomDungeonPath() {
        return FileManager.getRandomFile(
                FileManager.getFilesFromDirectory(PathFinder.getDungeonLevelFolder()
                        + getDungeonLevelSubfolder(), false)).getPath();
    }

    public String chooseDungeonLevel() {
        String path = PathFinder.getDungeonLevelFolder();
        String subFolder = ListChooser.chooseEnum(DUNGEON_SUBFOLDER.class, SELECTION_MODE.SINGLE);
        // DialogMaster.inputText("generic folder (empty for root", "");
        if (!StringMaster.isEmpty(subFolder)) {
            path += subFolder.replace(";", "") + "\\";
        }
        File folder = FileManager.getFile(path);
        List<String> files = FileManager.getFileNames(FileManager.findFiles(folder, ".xml", false,
                false));

        ListChooser listChooser = new ListChooser(SELECTION_MODE.SINGLE, files, false);
        listChooser.setMaxColumnNumber(4);
        listChooser.setMaxRowCount(40);
        String level = listChooser.choose();
        if (level == null) {
            return null;
        }
        if (subFolder == null) {
            return level;
        }
        return subFolder.replace(";", "") + "\\" + level;
    }
}
