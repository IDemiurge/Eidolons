package main;

import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.TypeMaster;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.enums.StatEnums;
import main.file.VersionMaster;
import main.game.core.game.Game;
import main.gui.GatewayButtonHandler;
import main.gui.GatewayWindow;
import main.gui.SessionWindow;
import main.io.AT_Keys;
import main.launch.ArcaneVault;
import main.logic.*;
import main.logic.ArcaneRef.AT_KEYS;
import main.logic.entity.Day;
import main.logic.value.AT_ContentManager;
import main.music.ahk.AHK_Master;
import main.session.Session;
import main.session.SessionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.time.ZeitMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ArcaneTower {

    public static final String IMG_PATH = "tower\\";
    private static final String ICON_PATH = "UI\\arcane tower\\AT.png"; // glyphs\\Twilight
    private static final String[] LAUNCH_OPTIONS = {"Full", "Music Core", "Music Core",};
    public static boolean sessionTest = false;
    static String musicTypes = "";
    static String workTypes = "";
    private static GatewayWindow gateWindow;
    private static List<Task> tasks;
    private static List<Goal> goals;
    private static List<Direction> directions;
    private static AT_Simulation sim;
    private static boolean av = false;
    private static ArcaneRef ref;
    private static ArcaneEntity selectedEntity;
    private static boolean selectiveLaunch = true;
    private static Day day;

    private static AT_ContentManager contentManager;

    private static boolean dcSync;

    static {
        List<String> list = ListMaster.toStringList(AT_OBJ_TYPE.values());
        list.remove(AT_OBJ_TYPE.MUSIC_LIST.toString());
        list.remove(AT_OBJ_TYPE.TRACK.toString());
        list.remove(AT_OBJ_TYPE.SCRIPT.toString());
        workTypes = StringMaster.constructContainer(list);
        list = ListMaster.toStringList(AT_OBJ_TYPE.MUSIC_LIST, AT_OBJ_TYPE.TRACK,
                AT_OBJ_TYPE.SCRIPT);
        musicTypes = StringMaster.constructContainer(list);
    }

    public static void main(String[] args) {
        CoreEngine.setArcaneTower(true);
        // GuiManager.setGuiDebug(isTestMode());

        // launch options
        // selective types
        // String launchOption =null ;
        // if (args!=null ){
        // selectiveLaunch=false;
        //
        // }
        // if (selectiveLaunch) {
        //
        // int init = DialogMaster.optionChoice("Launch Options",
        // LAUNCH_OPTIONS);
        // if (init == -1)
        // return;
        // WorkspaceManager.ADD_WORKSPACE_TAB_ON_INIT = workspaceLaunch;
        //
        // launchOption = LAUNCH_OPTIONS[init];
        // selectiveInit = launchOption != "Full";
        // if (selectiveInit) {
        //
        // switch (launchOption) {
        // case "":
        // types = musicTypes;
        // CoreEngine.setSelectivelyReadTypes(types);
        // }
        // }

        CoreEngine.setSelectivelyReadTypes(workTypes);

        genericInit(av);
        initDynamicEntities();
        showEnterGui();
        initGlobalKeys();

        if (sessionTest) {
            getGateWindow().getButtonHandler().handleCommand(GatewayButtonHandler.CONTINUE_SESSION,
                    ActionEvent.ALT_MASK);
        }

        ImageIcon img = ImageManager.getIcon(ICON_PATH);
        getGateWindow().getWindow().setIconImage(img.getImage());

        ZeitMaster.checkCreateTimeTypes();
    }

    public static void genericInit(boolean av) {

        EnumMaster.getAdditionalEnumClasses().add(StatEnums.class);

        ContentManager.setTypeMaster(new TypeMaster() {
            public OBJ_TYPE getOBJ_TYPE(String typeName) {
                return new EnumMaster<AT_OBJ_TYPE>().retrieveEnumConst(AT_OBJ_TYPE.class, typeName);// AT_OBJ_TYPE.valueOf(typeName);
            }
        });

        XML_Reader.setCustomTypesPath(getTypesPath());
        contentManager = new AT_ContentManager(dcSync);

        if (av) {
            launchAV();
        }
        // launchBackground();

        initSimulation();
        if (!av) {
            contentManager.contentInit();
            CoreEngine.systemInit();
            XML_Reader.loadXml(getTypesPath());
        }
        sim.initObjTypes();
        ContentManager.setInstance(contentManager);
    }

    public static void launchMC() {
        // MusicCore.ma
        AHK_Master.main(null);
    }

    public static void launchAV() {
        ArcaneVault.CUSTOM_LAUNCH = true;
        ArcaneVault.selectiveLaunch = false;
        ArcaneVault.setContentManager(contentManager);
        ArcaneVault.main(null);
        ImageIcon img = ImageManager.getIcon(ICON_PATH);
        ArcaneVault.getWindow().setIconImage(img.getImage());
        ArcaneVault.getWindow().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Game.game = sim;

    }

    private static void initSimulation() {
        sim = new AT_Simulation();

    }

    public static String getTypesPath() {
        return getAT_Path() + "types\\";

    }

    public static String getAT_Path() {
        return PathFinder.getXML_PATH() + "\\Arcane Tower\\";
    }

    public static void initTasks() {
        tasks = new ArrayList<>();
        for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.TASK)) {
            // if (isTestMode()
            // || type.checkProperty(AT_PROPS.TASK_STATUS,
            // TASK_STATUS.PENDING.name()))
            tasks.add((Task) getEntity(type));
            getEntity(type).toBase();
        }
    }

    public static void initDirections() {
        directions = new ArrayList<>();
        for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.DIRECTION)) {
            // if (checkDirection)
            directions.add((Direction) getEntity(type));
        }
    }

    public static void initGoals() {
        goals = new ArrayList<>();
        for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.GOAL)) {
            goals.add((Goal) getEntity(type));
        }
    }

    public static SessionWindow getSessionWindow() {
        return getSessionWindow(null);
    }

    public static SessionWindow getSessionWindow(ArcaneEntity entity) {
        // if (entity != null) {
        // Session session = (Session) entity.getSession();
        // if (session != null)
        // return session.getWindow();
        // }
        return getSession().getWindow();
    }

    public static Session getSession() {
        return SessionMaster.getSession();
    }

    public static Goal getSelectedGoal() {
        if (getSelectedEntity() == null) {
            return null;
        }
        Goal goal = (Goal) getSelectedEntity().getRef().getObj(AT_KEYS.GOAL);
        return goal;
    }

    public static boolean isTestMode() {
        return true;
    }

    public static Ref getRef() {
        if (ref == null) {
            ref = new ArcaneRef(sim);
        }
        return ref;
    }

    public static void saveAll() {
        calcStats();
        XML_Writer.saveAll();
        initDynamicEntities();
    }

    public static void initDynamicEntities() {
        initTasks();
        initGoals();
        initDirections();
    }

    private static void calcStats() {
        // day.beforeSave();
        for (ArcaneEntity ae : SessionMaster.getSessions()) {
            ae.beforeSave();
        }
        for (ArcaneEntity ae : tasks) {
            ae.beforeSave();
        }
        for (ArcaneEntity ae : goals) {
            ae.beforeSave();
        }
        for (ArcaneEntity ae : directions) {
            ae.beforeSave();
        }

    }

    public static void saveEntity(ArcaneEntity entity) {
        saveEntity(entity, false); // preCheck remove from t/g
    }

    public static void saveEntity(ArcaneEntity entity, boolean saveVersion) {
        entity.beforeSave();
        XML_Writer.writeXML_ForType(entity.getType());
        if (saveVersion) {
            VersionMaster.saveVersion(entity);
        }
    }

    public static ArcaneEntity getSelectedEntity() {
        return selectedEntity;
    }

    public static void setSelectedEntity(ArcaneEntity entity) {
        selectedEntity = entity;

    }

    public static ArcaneEntity getEntity(ObjType type) {
        return getSimulation().getInstance(type);
    }

    public static AT_Simulation getSimulation() {
        return sim;
    }

    private static void initGlobalKeys() {
        AT_Keys.initKeys();
    }

    private static void showEnterGui() {
        createAndShowGatewayWindow();
        // checkUnfinishedSessions();
    }

    public static Day getDay() {
        // if (day != null)
        // return day;
        // VersionMaster.getVersion()
        ObjType type = DataManager.getType(ZeitMaster.getNameForPeriod(AT_OBJ_TYPE.DAY),
                AT_OBJ_TYPE.DAY);

        day = new Day(type);
        // pre-select some params ? plan, expectations, keyrules, decisions
        return day;
    }

    private static void createAndShowGatewayWindow() {
        setGateWindow(new GatewayWindow());

    }

    public static void checkAutosave(ArcaneEntity entity) {
        VersionMaster.saveVersionFolder(true);
    }

    public static void saveVersion() {
        VersionMaster.saveVersionFolder();
    }

    public static List<Goal> getGoals(List<ObjType> list) {
        // return new ListMaster<Task>().toObjList(tasks, list);
        List<Goal> filtered = new ArrayList<>();
        for (ObjType sub : list) {
            for (Goal g : getGoals()) {
                if (g.getType() == sub) {
                    filtered.add(g);
                    break;
                }
            }
        }
        return filtered;
    }

    public static List<Task> getTasks(List<ObjType> list) {
        // return new ListMaster<Task>().toObjList(tasks, list);
        List<Task> filtered = new ArrayList<>();
        for (ObjType sub : list) {
            for (Task task : tasks) {
                if (task.getType() == sub) {
                    filtered.add(task);
                    break;
                }
            }
        }
        return filtered;
    }

    public static List<Task> getTasks() {

        return tasks;
    }

    public static List<Goal> getGoals() {
        return goals;
    }

    public static void selectEntity(ArcaneEntity entity) {
        SessionWindow window = ArcaneTower.getSessionWindow(entity);
        window.getInfoPanel().selectType(entity.getType());
        setSelectedEntity(entity);
        window.refresh();
        window.getGoalsPanel().getSelectedPanel().refresh();
    }

    public static ArcaneEntity getEntity(AT_OBJ_TYPE TYPE, String typeName) {
        return getEntity(DataManager.getType(typeName, TYPE));
    }

    public static boolean isSwingGraphicsMode() {
        return true;
    }

    public static GatewayWindow getGateWindow() {
        return gateWindow;
    }

    public static void setGateWindow(GatewayWindow gateWindow) {
        ArcaneTower.gateWindow = gateWindow;
    }

    public static boolean isControlSoundsOn() {
        return true;
    }

    public static void setNonTest(Entity t) {
        t.addProperty(G_PROPS.STD_BOOLS, "Valid", true);

    }

    public static boolean isNonTest(Entity t) {
        return t.checkProperty(G_PROPS.STD_BOOLS, "Valid");
    }

    public static List<Direction> getDirections() {
        return directions;
    }

    public static Color getTextColor() {
        // TODO Auto-generated method stub
        return null;
    }

    public static List<?> getPeriods(AT_OBJ_TYPE tYPE) {
        return null;// TODO
    }

}
