package main.game.battlecraft.logic.dungeon;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.VALUE;
import main.content.enums.DungeonEnums.DUNGEON_SUBFOLDER;
import main.content.enums.system.MetaEnums;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.travel.Encounter;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.GuiEventManager;
import main.system.OnDemandEventCallBack;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.FilterMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.test.frontend.FAST_DC;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/*
 * 
 */
public class DungeonMaster {

    public static final Integer testWidth = 15;
    public static final Integer testHeight = 11;
    public static final int BASE_WIDTH = 15;
    public static final int BASE_HEIGHT = 11;
    public static final String PRESET_PLAN = "Spire";
    static final VALUE[] encounterDungeonValues = {PROPS.DUNGEON_MAP_MODIFIER,
            PROPS.DUNGEON_MAP_TEMPLATE, PROPS.MAP_PRESET_OBJECTS, PROPS.MAP_OBJECTS,};
    // "Dungeon"; //
    // Arcane Tower
    private static final String DUNGEON_BACKGROUND_FOLDER = "\\big\\dungeons";
    private static final String RANDOM_DUNGEON_WORKSPACE_FILTER =
            MetaEnums.WORKSPACE_GROUP.FOCUS
                    + "" + MetaEnums.WORKSPACE_GROUP.COMPLETE;
    public static boolean RANDOM_DUNGEON = false;
    public static boolean CHOOSE_LEVEL = false;
    private static String DEFAULT_DUNGEON_LEVEL = "Forest.xml"; // "Arena.xml";
    private static String DEFAULT_DUNGEON = "Hidden Camp";// "Hidden Camp";//
    private static DC_Game game;
    private static List<Dungeon> dungeons;
    private static String DEFAULT_DUNGEON_PATH;
    // "Sanctuary";
    // StringMaster
    // .getWellFormattedString(MAP_BACKGROUND.CEMETARY + "");
    private Dungeon dungeon;
    private String presetDungeonType;
    private Dungeon rootDungeon;
    private boolean chooseLevel;
    private boolean initialized;
    // public static final int BASE_WIDTH = 23;
    // public static final int BASE_HEIGHT = 15;
    private Integer z;
    private String dungeonPath;
    private WORKSPACE_GROUP workspaceFilter;

    public DungeonMaster(DC_Game game) {
        DungeonMaster.game = game;
        setChooseLevel(CHOOSE_LEVEL);
        // setDungeonPath(DEFAULT_DUNGEON_LEVEL);
        presetDungeonType = getDEFAULT_DUNGEON();
        dungeonPath = DEFAULT_DUNGEON_PATH;
    }

    public static String getDungeonBackgroundFolder() {
        return DUNGEON_BACKGROUND_FOLDER;
    }

    public static String getDEFAULT_DUNGEON() {
        return DEFAULT_DUNGEON;
    }

    public static void setDEFAULT_DUNGEON(String string) {
        DEFAULT_DUNGEON = string;

    }

    public static List<Dungeon> generateDungeonsForPlace(Place place) {
        for (String s : StringMaster.openContainer(place.getProperty(MACRO_PROPS.DUNGEONS))) {
            // typeName(concealment level, linked dungeons
        }
        return null;
    }

    public static String getDEFAULT_DUNGEON_LEVEL() {
        return DEFAULT_DUNGEON_LEVEL;
    }

    public static void setDEFAULT_DUNGEON_LEVEL(String dEFAULT_DUNGEON_LEVEL) {
        DEFAULT_DUNGEON_LEVEL = dEFAULT_DUNGEON_LEVEL;
    }

    public static void goToDungeon(Dungeon newDungeon) {
        game.getMainHero().setDungeon(newDungeon);
        // coordinates? the dungeon's entrance..
        Coordinates coordinates = game.getMainHero().getCoordinates();
        if (newDungeon.getMainEntrance() != null) {
            coordinates = newDungeon.getMainEntrance().getCoordinates();
        }
        // exit?
        List<Unit> units = new LinkedList<>();
        if (game.getParty() != null) {
            units.addAll(game.getParty().getMembers());
        } else {

        }
        game.getArenaManager().getSpawnManager().spawnUnitsAt(units, coordinates);
        game.getBattleField().refresh();
    }

    public static List<Dungeon> getDungeons() {
        if (dungeons == null) {
            dungeons = new LinkedList<>();
        }
        return dungeons;
    }

    public static void setDungeons(List<Dungeon> dungeons) {
        DungeonMaster.dungeons = dungeons;
    }

    public static Integer getMinDungeonWidth() {
        return GuiManager.getBF_CompDisplayedCellsX();
    }

    public static Integer getMinDungeonHeight() {
        return GuiManager.getBF_CompDisplayedCellsY();
    }

    public static int getDungeonPowerTotal(Dungeon dungeon) {
        // TODO dungeon.getType()
        if (dungeon.isSublevel()) {

        } else {

        }
        return 0;
    }

    public static String getRawDungeonName(String levelFilePath) {
        if (levelFilePath == null) {
            return "null dungeon";
        }
        String name = StringMaster.getLastPathSegment(levelFilePath);
        name = StringMaster.cropFormat(name);
        name = StringMaster.cropVersion(name);
        return name;
    }

    public static void setDEFAULT_DUNGEON_PATH(String string) {
        DEFAULT_DUNGEON_PATH = string;
    }

    public void initDungeon(String path) {
        setDungeon(DungeonBuilder.loadDungeon(path));
    }

    public void initDungeon() {
        ObjType type = null;

        if (FAST_DC.getGameLauncher().getSUPER_FAST_MODE()) {
            setDungeonPath(FAST_DC.DEFAULT_TEST_DUNGEON);
        }
        if (getDungeonPath() != null) {
            setDungeon(DungeonBuilder.loadDungeon(getDungeonPath()));

            return;
        }

        if (CoreEngine.isArcaneVault()) {
            ObjType objType = new ObjType("Test Dungeon", DC_TYPE.DUNGEONS);
            objType.setParam(PARAMS.BF_WIDTH, 3);
            objType.setParam(PARAMS.BF_HEIGHT, 3);
//            setDungeon(new SimulationDungeon(objType));
            return;
        }
//        if (FAST_DC.isRunning()) {
//            if (type==null ) {
//                type = DataManager.getType(getPresetDungeonType(), OBJ_TYPES.DUNGEONS);
//            }}
//        if (getDEFAULT_DUNGEON() != null
//                || getDungeonPath() != null) {
//            if (!CoreEngine.isLevelEditor())
//                game.setSimulation(false);
//            if (getDEFAULT_DUNGEON() != null) {
//              TODO   ObjType t = DataManager.getType(getDEFAULT_DUNGEON(),  OBJ_TYPES. DUNGEONS);
//                setDungeon(DungeonBuilder.loadDungeon(getDEFAULT_DUNGEON()));
//            } else
//                setDungeon(DungeonBuilder.loadDungeon(getDungeonPath()));
//        }
//        else

        if (type == null) {
            if (!FAST_DC.isRunning()) {
                type = DataManager.getType(getPresetDungeonType(), DC_TYPE.DUNGEONS);
                setDungeon(new Dungeon(type));
            } else {
                if (RANDOM_DUNGEON) {
                    type =
                            pickRandomDungeon();
                    setDungeon(new Dungeon(type));
                } else if (type == null) {
//                    type = DataManager.getType(ListChooser.chooseType(DC_TYPE.DUNGEONS));
//                }
//                if (type == null) {
                    initDungeonLevelChoice();
                }
            }
            getDungeons().add(dungeon);
            rootDungeon = getDungeon();
        }
        if (dungeon != null) {

            //IDEA simulationDungeon subclass
            //LEVEL EDITOR NEEDS GRID???
//            grid = new DC_BattleFieldGrid( getDungeon());


            initialized = true;
        }
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

    public void initSublevel(Dungeon subLevel) {
        setDungeon(subLevel);
//        game.getBattleField().getBuilder().newDungeon(subLevel);
        if (!getDungeons().contains(subLevel)) {
            getDungeons().add(subLevel);
        }
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

    public boolean initDungeon(String typeName, Place place) {
        // set Dungeon obj here so that launch() will be proper
        // set some other parameters perhaps...

        ObjType type = DataManager.getType(typeName, DC_TYPE.DUNGEONS);
        if (type == null) {
            type = getDungeonTypeFromPlace(place);
        }
        this.dungeon = new Dungeon(type);
        getDungeons().add(dungeon);
        //

        return true;

    }

    public void initEncounterDungeon(Encounter e) {
        ObjType type = new ObjType(e.getRoute().getName(), DC_TYPE.DUNGEONS);
        type.initType();
        String value = e.getRoute().getProperty(PROPS.MAP_BACKGROUND);
        if (!ImageManager.isImage(value)) {
            value = e.getRoute().getArea().getProperty(PROPS.MAP_BACKGROUND);
        }
        type.setProperty(PROPS.MAP_BACKGROUND, value);
        for (VALUE p : encounterDungeonValues) {
            type.copyValue(p, e.getRoute());
            if (type.checkValue(p)) {
                type.copyValue(p, e.getRoute().getArea());
            }
        }

        // 'rewards' ? encounters ?

        setDungeon(new Dungeon(type));
        getDungeons().add(dungeon);
    }

    private ObjType getDungeonTypeFromPlace(Place place) {
        // place.getProperty(prop)
        ObjType type = DataManager.getType(place.getName(), DC_TYPE.DUNGEONS);
        if (type == null) {
            type = DataManager.findType(place.getName(), DC_TYPE.DUNGEONS);
        }
        return type;
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

    public void reloadDungeon() {
        try {
            game.exit(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initDungeonLevelChoice();

        game.battleInit();
        game.start(false);

    }

    public void initDungeonLevelChoice() {
        setDungeon(null);
        if (RANDOM_DUNGEON) {
            setDungeon(DungeonBuilder.loadDungeon(getRandomDungeonPath()));
            return;
        }
        List<ObjType> types = DataManager.getTypes(DC_TYPE.DUNGEONS);
        if (workspaceFilter != null) {
            FilterMaster.filterByPropJ8(types, G_PROPS.WORKSPACE_GROUP.getName(), workspaceFilter.toString());
        }
        ObjType type = ListChooser.chooseType(types);
        if (type != null) {
            setDungeon(new Dungeon(type));
        }
        if (getDungeon() == null) {
            if (getDungeonPath() == null) {
                setDungeonPath(chooseDungeonLevel());
            }
            if (getDungeonPath() != null) {
                setDungeon(DungeonBuilder.loadDungeon(getDungeonPath()));
            }
        }
//        else //if (isChooseLevel())
//            setDungeonPath(chooseDungeonLevel());
    }

    private String getRandomDungeonPath() {
        return FileManager.getRandomFile(
                FileManager.getFilesFromDirectory(PathFinder.getDungeonLevelFolder()
                        + getDungeonLevelSubfolder(), false)).getPath();
    }

    private String getDungeonLevelSubfolder() {
        return "battle\\";
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

    public void addDungeon() {
        String level = chooseDungeonLevel();
        if (level == null) {
            return;
        }
        setZ(rootDungeon.getNextZ());
        Dungeon subLevel = DungeonBuilder.loadDungeon(level);
        subLevel.setSublevel(true);
        rootDungeon.addSublevel(level, subLevel);
        initSublevel(subLevel);
        goToDungeon(subLevel);
    }

    public void subLevelEntered(String name) {
        // what if some units are left behind?
        // gonna need some global preCheck of sublevel to prevent interactions

    }

    public boolean isExtendedBattlefield() {
        if (testHeight != null) {
            return true;
        }
        if (testWidth != null) {
            return true;
        }
        return getDungeon().isExtendedBattlefield();
    }

    public Dungeon getRootDungeon() {
        if (rootDungeon == null) {
            return dungeon;
        }
        return rootDungeon;
    }

    public void initDungeon(ObjType selectedItem) {
        Dungeon dungeon = new Dungeon(selectedItem);
        setDungeon(dungeon);
        getDungeons().add(dungeon);
    }

    public Dungeon getDungeonNeverInit() {
        return dungeon;
    }

    public Dungeon getDungeon() {
        if (dungeon == null) {
            initDungeon();
        }
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        if (dungeon == null) {
            return;
        }
        if (this.dungeon == null) {
            rootDungeon = dungeon;
        }
        this.dungeon = dungeon;
        GuiManager.setCurrentLevelCellsX(getLevelWidth());
        GuiManager.setCurrentLevelCellsY(getLevelHeight());
        if (!ImageManager.isImage(dungeon.getMapBackground())) {
            LogMaster.log(1,
                    dungeon.getMapBackground() + " is not a valid image! >> " + dungeon);
            return;
        }

        GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND, new OnDemandEventCallBack<>(dungeon.getMapBackground()));
    }

    public G_Panel getMinimapComponent() {
        return dungeon.getMinimap().getComp();
    }

    public Integer getZ() {
        if (z == null) {
            if (dungeon != null) {
                return dungeon.getZ();
            }
        }
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getDungeonPath() {
        return dungeonPath;
    }

    public void setDungeonPath(String name) {
        dungeonPath = name;
    }

    public boolean isChooseLevel() {
        return chooseLevel;
    }

    public void setChooseLevel(boolean chooseLevel) {
        this.chooseLevel = chooseLevel;
    }

    public WORKSPACE_GROUP getWorkspaceFilter() {
        return workspaceFilter;
    }

    public void setWorkspaceFilter(WORKSPACE_GROUP workspaceFilter) {
        this.workspaceFilter = workspaceFilter;
    }
}
